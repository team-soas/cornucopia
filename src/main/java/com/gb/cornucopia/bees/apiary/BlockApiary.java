package com.gb.cornucopia.bees.apiary;

import com.gb.cornucopia.CornuCopia;
import com.gb.cornucopia.GuiHandler;
import com.gb.cornucopia.InvModel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import static com.gb.cornucopia.CornuCopia.MODID;


public class BlockApiary extends Block implements ITileEntityProvider {
	public final String name = "bee_apiary";

	public BlockApiary() {
		super(Material.WOOD);
		this.setCreativeTab(CornuCopia.tabBees);
		this.setHardness(0.6F);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		GameRegistry.registerTileEntity(TileEntityApiary.class, MODID + this.getUnlocalizedName());
		InvModel.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(final World worldIn, final int meta) {
		return new TileEntityApiary();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(CornuCopia.instance, GuiHandler.APIARY, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;

	}

	public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
		TileEntity apiary = world.getTileEntity(pos);
		IItemHandler itemHandler = apiary.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		for (int i = 0; i < 9; i++) {
			if(!itemHandler.getStackInSlot(i).isEmpty()){
				EntityItem droppedItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
				world.spawnEntity(droppedItem);
			}
		}
		super.breakBlock(world, pos, state);
	}

}

