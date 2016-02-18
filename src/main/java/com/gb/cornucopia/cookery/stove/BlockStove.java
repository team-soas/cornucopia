package com.gb.cornucopia.cookery.stove;

import java.util.Random;

import com.gb.cornucopia.CornuCopia;
import com.gb.cornucopia.InvModel;
import com.gb.cornucopia.cookery.Cookery;
import com.gb.cornucopia.cookery.Vessel;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStove extends Block  implements ITileEntityProvider{
	public final String name;
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ON = PropertyBool.create("on");

	public BlockStove()
	{
		super(Material.iron);
		this.name = "cookery_stove";
		this.setUnlocalizedName(this.name);
		this.setHardness(1.5F);
		this.setCreativeTab(CornuCopia.tabCookery);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ON, false).withProperty(FACING, EnumFacing.NORTH));
		GameRegistry.registerBlock(this, this.name);
		GameRegistry.registerTileEntity(TileEntityStove.class, "cookery_stove_entity");
		InvModel.add(this, this.name);
	}

	public int getLightValue(IBlockAccess world, BlockPos pos)
	{
		final Block block = world.getBlockState(pos).getBlock();
		if (block != this)
		{
			return block.getLightValue(world, pos);
		}
		return (boolean)world.getBlockState(pos).getValue(ON) ? 2 : 0;
	}
	
	public static final PropertyEnum VESSEL = PropertyEnum.create("vessel", Vessel.class);
	public static Vessel getVessel(World world, BlockPos pos) {
		if ( world.getBlockState(pos.up()).getBlock() == Cookery.stovetop ) {
			return (Vessel) world.getBlockState(pos.up()).getValue(VESSEL);
		} else {
			// sometimes there is no stove top block there. cest la vie!
			return Vessel.NONE;
		}
	}

	public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY, final float hitZ)
	{
		//final IBlockState top = world.getBlockState(pos.up());
		if (world.isRemote || world.getBlockState(pos.up()).getBlock() != Cookery.stovetop) { return true; }
		System.out.format("ok go \n" );
		if (BlockStove.getVessel(world, pos) == Vessel.NONE) {			
			final Vessel v = (player.getHeldItem() == null) ? Vessel.NONE : Vessel.fromItem(player.getHeldItem().getItem());
			// if the held item is associated with any vessel, place that vessel
			if (v != Vessel.NONE) {
				world.setBlockState(pos.up(), world.getBlockState(pos.up()).withProperty(BlockStoveTop.VESSEL, v));
				// this should be okay, since all cookware stacks to one
				player.destroyCurrentEquippedItem();
				return true;
			}
		}
		
		// if there's a vessel already in place, open the crafting table 
		// ( also hue hue we doin this... none type gui)
		player.openGui(CornuCopia.instance, (BlockStove.getVessel(world, pos)).meta, world, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}

	@Override
	public boolean canPlaceBlockAt(final World world, final BlockPos pos){
		return super.canPlaceBlockAt(world, pos) && super.canPlaceBlockAt(world, pos.up());
	}


	public IBlockState onBlockPlaced(final World world, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(ON, false);
	}

	public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack)
	{
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(ON, false), 2);
		world.setBlockState(pos.up(), Cookery.stovetop.getDefaultState().withProperty(BlockStoveTop.FACING, placer.getHorizontalFacing().getOpposite()));
	}

	public void breakBlock(final World world, final BlockPos pos, final IBlockState state)
	{

		TileEntity stove = world.getTileEntity(pos);

		if (stove instanceof TileEntityStove)
		{
			Vessel v = BlockStove.getVessel(world, pos);
			if (v != Vessel.NONE){
				world.spawnEntityInWorld(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(v.getItem())));
				world.setBlockToAir(pos.up());
			}
			InventoryHelper.dropInventoryItems(world, pos, (TileEntityStove)stove);
		}

		super.breakBlock(world, pos, state);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(final World world, final BlockPos pos, final IBlockState state, final Random rand){
		if ((boolean) state.getValue(ON)){
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D + 0.3D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d3 = rand.nextDouble() * 0.6D - 0.3D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);

		}
	}
	
	public IBlockState getStateFromMeta(final int meta)
	{
		return this.getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
				.withProperty(ON, (meta & 4) == 4 )
				;
	}

	public int getMetaFromState(final IBlockState state)
	{
		return (
				(EnumFacing)state.getValue(FACING)).getHorizontalIndex()
				| (((boolean)state.getValue(ON)) ? 4 : 0
						);
	}
	

	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {ON, FACING});
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new TileEntityStove();
	}

}