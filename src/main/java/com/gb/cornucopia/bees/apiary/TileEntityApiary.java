package com.gb.cornucopia.bees.apiary;

import java.util.ArrayList;
import java.util.Random;

import com.gb.cornucopia.bees.Bees;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class TileEntityApiary extends TileEntity implements IUpdatePlayerListBox, IInventory {
	public static final int TICK_PERIOD = 40; // debug = 2 seconds //* 60 * 2; // tick every two minutes, because it might be kind of expensive >__>
	public static final Random RANDOM = new Random();
	private final ItemStack[] contents = new ItemStack[9]; // 0 = queen slot, 1 = worker slot, 2-8 are the honeycombs
	private int ticks = 0;
	private ArrayList<IBlockState> flower_survey = new ArrayList<>();
	private int flower_score = 0;

	//System.out.format("\n");


	@Override
	public void update() {
		if (!this.hasWorldObj()) return;
		final World world = this.getWorld();
		if (world.isRemote) return;
		if (this.ticks++ < TICK_PERIOD){ return; }

		// time to actually do the tick!
		this.ticks = 0;
		//System.out.format("\n   ===   TICKING!   ===\n");

		// no bees???? nothing I can do
		if (this.contents[1] == null){ return; }

		// hungry beeds? no soup for u
		if (!this.feedBees()) {return;}

		flowerSurvey();
		produce();
		cloneFlower();

		this.markDirty();

	}

	private void produce(){
		// 75%~ chance to produce with max bees and max flowers!
		final int fs = Math.min(this.flower_score, 32);
		if (fs == 0) {return;} // need at least one flower!
		int grow_chance = (this.beeCount() + fs) / (1 + (this.getNeighboringApiaries() * 2));
		//System.out.format(" @ produce bee gifts (%d / 128)\n ~ ( %d bees + %d flowers ) / (%d neighbors * 2)\n", grow_chance, this.beeCount(), fs, this.getNeighboringApiaries());
		if (RANDOM.nextInt(128) < grow_chance){
			// look for empties first:
			for (int i=2; i<9; i++){
				if (this.contents[i] == null) {
					this.contents[i] = (new ItemStack(Bees.waxcomb));
					//System.out.format("    make a wax\n");
					return;
				}
			}

			// then look for plain wax:
			for (int i=2; i<9; i++){
				if (this.contents[i].getItem() == Bees.waxcomb) {
					this.contents[i].setItem(Bees.honeycomb);
					//System.out.format("    make a honey comb\n");
					return;
				}
			}

			// once the hive is full of honey, 
			// if there's a queen in the queen slot and slot 6 is not already jelly
			// AND you've got a royal flower AAAND you're really lucky...
			if (this.contents[6].getItem() != Bees.royal_jelly && this.nearRoyalBloom() && RANDOM.nextInt(64) == 0) {									
				// can happen without a queen, but it's super rare!
				if (this.hasQueen() || RANDOM.nextInt(64) == 0) {
					// royal jelly comes at a heavy price though. it uses up most of the honey, and the bee population will suffer
					for (int i : new int[]{2,3,4,5,7,8}) { // outer comb slots
						// 1/4 chance that wax will be left bee-hind 
						if (RANDOM.nextInt(4) == 0){
							this.contents[i] = new ItemStack(Bees.waxcomb);
						}
						else {
							this.contents[i] = null;
						}

					}
					this.contents[6].setItem(Bees.royal_jelly);

					return;
				}
			}
		}
	}

	private int getNeighboringApiaries() {
		// remember to leave 4 blocks of space between your apiaries!
		int n = 0;
		for (int x = -4; x < 5; x++){
			for (int z = -4; z < 5; z++){
				if (this.getWorld().getBlockState(this.pos.add(x, 0, z)).getBlock() == Bees.apiary){
					n++;
				}
			}			
		}
		return n-1;
	}


	private boolean feedBees(){
		final int bc = this.beeCount();		
		int food = 0;
		if (bc > 8){  food--; } // you get eight.... freebies XD
		if (bc > 16){ food--; }
		if (bc > 32){ food--; }
		if (bc > 48){ food--; }

		for (int i = 2; i < 9; i++){
			if (this.contents[i] != null && this.contents[i].getItem() == Bees.honeycomb){
				food++;
			}
		}

		final ItemStack workers = this.contents[1];

		if (food < 0){
			workers.stackSize += food;
			//System.out.format("   bees died :(\n");
			if (workers.stackSize < 1){
				this.contents[1] = null; // technically bees can't die out like this... but ya never know so BEE safe~!
			}
			// return false // < actually, try allowing starving bees to produce. that way, a brood can recover after producing the costly royal jelly
		}
		// with a queen and an excess of honeycomb, theres a chance to spawn some new bees, based on the amount of excess food and the number of nearby apiaries
		// 1 out of ( (7-amount of food) * (number of apiaries within 5 blocks) )
		else if (food > 0 && this.hasQueen() && RANDOM.nextInt((8 - food) * (1 + this.getNeighboringApiaries())) == 0){
			//System.out.format("   bees grew! :)\n");
			workers.stackSize = Math.min(workers.stackSize + 1, workers.getMaxStackSize()); 
		}
		return true;
	}


	private boolean nearRoyalBloom(){
		for (int x = -2; x < 3; x++){
			for (int z = -2; z < 3; z++){
				if (this.getWorld().getBlockState(this.pos.add(x, 0, z)).getBlock() == Bees.royal_bloom){
					return true;
				}
			}			
		} 
		return false;
	}

	private void cloneFlower(){
		// pick a random block near the apiary. TODO: figure out a way to make them grow closer more often
		final BlockPos fpos = this.pos.add(RANDOM.nextInt(15) - 7, 0, RANDOM.nextInt(15) - 7);
		if ((this.worldObj.isAirBlock(fpos) || this.worldObj.getBlockState(fpos).getBlock() == Blocks.tallgrass) && this.worldObj.getBlockState(fpos.down()).getBlock() == Blocks.grass && RANDOM.nextInt(80) < this.beeCount()){
			// stop growing if there are 48 flowers in the vicinity (don't grow at all if there's no flowwers :()
			if (this.flower_score > 48 || this.flower_survey.size() < 1 ){ return; }
			this.worldObj.setBlockState(fpos, this.flower_survey.get(RANDOM.nextInt(this.flower_survey.size())), 0);
			this.worldObj.markBlockForUpdate(fpos);
		}

		// royal flower!
		final BlockPos rpos = this.pos.add(RANDOM.nextInt(5) - 2, 0, RANDOM.nextInt(5) - 2);
		if (this.beeCount() == 64 && this.flower_score > 16 && !this.nearRoyalBloom() && RANDOM.nextInt(this.hasQueen() ? 512 : 1024) == 0){
			if((this.worldObj.isAirBlock(rpos) || this.worldObj.getBlockState(rpos).getBlock() == Blocks.tallgrass || this.worldObj.getBlockState(rpos).getBlock() instanceof BlockFlower) && this.worldObj.getBlockState(rpos.down()).getBlock() == Blocks.grass){
				this.worldObj.setBlockState(rpos, Bees.royal_bloom.getDefaultState(), 0);
				this.worldObj.markBlockForUpdate(rpos);				
			}
		}
	}

	private void flowerSurvey(){
		this.flower_survey.clear();
		this.flower_score = this.flowerSample(this.pos, 7, true);
	}

	private int flowerSample(final BlockPos pos, final int r, final boolean record_states){
		// sample a radius * radius area around the apiary N times, return the number of flowers found
		int density = 0;
		for (int z = -r; z <= r; z++){
			for (int x = -r; x <= r; x++){
				for (int y = -1; y <= 1; y++) {
					// pollenate-able?
					BlockPos fpos = pos.add(x, y, z);
					IBlockState bs = this.getWorld().getBlockState(fpos);
					if (bs.getBlock() instanceof BlockFlower){
						density++;
						// this list could potentially slow the server down, searching a 15x15 area...
						if (record_states){
							this.flower_survey.add(bs);
						}
						// stop counting after a while?
					}
				}

			}			
		}		
		return density;
	}

	public boolean hasQueen(){
		// theoretically slot permissions means we don't need to check the item
		return contents[0] != null;
	}

	public int beeCount(){
		return contents[1] == null ? 0 : contents[1].stackSize;
	}

	// for writing to nbt
	public int[] combSlots(){
		final int[] a = new int[7];

		for (int i = 2; i < 9; i++) {
			// 0 == empty
			if (contents[i] == null){
				a[i-2] = 0;
				continue;
			}

			Item comb = contents[i].getItem();
			if(comb == Bees.waxcomb) {
				a[i-2] = 1;
			}
			else if(comb == Bees.honeycomb) {
				a[i-2] = 2;
			}
			else if(comb == Bees.royal_jelly) {
				a[i-2] = 3;
			}
			else { // if something else hid here... gtfo!
				a[i-2] = 0;				
			}
		}

		return a;
	}

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		final int metadata = getBlockMetadata();
		return new S35PacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void writeToNBT(final NBTTagCompound parentNBTTagCompound)
	{
		super.writeToNBT(parentNBTTagCompound); // The super call is required to save the tiles location
		parentNBTTagCompound.setBoolean("hasQueen", this.hasQueen() );
		parentNBTTagCompound.setInteger("beeCount", this.beeCount() );
		parentNBTTagCompound.setIntArray("combSlots", this.combSlots());
	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void readFromNBT(final NBTTagCompound parentNBTTagCompound)
	{
		super.readFromNBT(parentNBTTagCompound); // The super call is required to load the tiles location

		final boolean hasQueen = parentNBTTagCompound.getBoolean("hasQueen");
		final int beeCount = parentNBTTagCompound.getInteger("beeCount");
		final int[] combSlots =  parentNBTTagCompound.getIntArray("combSlots");

		// queen bee
		if (hasQueen){
			this.contents[0] = new ItemStack(Bees.queen);
		}
		else{
			this.contents[0] = null;
		}

		// worker bees
		if (beeCount > 0){
			this.contents[1] = new ItemStack(Bees.bee, Math.min(beeCount, 64));
		}
		else{
			this.contents[1] = null;
		}

		// honeycomb slots
		for (int i = 2; i < 9; i++){
			switch(combSlots[i-2]){
			case 1:
				this.contents[i] = new ItemStack(Bees.waxcomb);
				break;
			case 2:
				this.contents[i] = new ItemStack(Bees.honeycomb);
				break;
			case 3:
				this.contents[i] = new ItemStack(Bees.royal_jelly);
				break;
			case 0: default:
				this.contents[i] = null;
			}
		}
	}

	@Override
	public String getName() { return "apiary"; }

	@Override
	public boolean hasCustomName() { return false; }

	@Override
	public IChatComponent getDisplayName() { return new ChatComponentText("apiary"); }

	@Override
	public int getSizeInventory() {	return 9; }

	@Override
	public int getInventoryStackLimit() {return 64;}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {	
		return player.getDistanceSq(this.pos) < 6;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		// 0 - queen
		// 1 - workers
		// 2-8 - honeycomb output slots
		return contents[index];
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count) { 
		if (this.contents[index] != null)
		{
			final ItemStack itemstack = this.contents[index].splitStack(Math.min(count, this.contents[index].stackSize));
			if (this.contents[index].stackSize == 0)
			{
				this.contents[index] = null;
			}
			this.markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int index)
	{
		if (this.contents[index] != null)
		{
			final ItemStack itemstack = this.contents[index];
			this.contents[index] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(final int index, final ItemStack stack)
	{
		this.contents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public void openInventory(final EntityPlayer player) {}

	@Override
	public void closeInventory(final EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		// afaict this doesn't do shit???
		return false;
	}

	public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer player)
	{
		return new ContainerApiary(playerInventory, this, this.worldObj, this.pos);
	}

	@Override
	public int getField(final int id) {
		return 0;
	}

	@Override
	public void setField(final int id, final int value) {

	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.contents.length; ++i)
		{
			this.contents[i] = null;
		}
	}

}