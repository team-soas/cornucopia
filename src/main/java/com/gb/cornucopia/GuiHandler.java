package com.gb.cornucopia;

import com.gb.cornucopia.bees.Bees;
import com.gb.cornucopia.bees.crafting.ContainerApiary;
import com.gb.cornucopia.bees.crafting.GuiApiary;
import com.gb.cornucopia.cookery.Cookery;
import com.gb.cornucopia.cookery.block.TileEntityStove;
import com.gb.cornucopia.cookery.crafting.ContainerCuttingBoard;
import com.gb.cornucopia.cookery.crafting.ContainerMill;
import com.gb.cornucopia.cookery.crafting.ContainerPresser;
import com.gb.cornucopia.cookery.crafting.ContainerStove;
import com.gb.cornucopia.cookery.crafting.GuiCuttingBoard;
import com.gb.cornucopia.cookery.crafting.GuiMill;
import com.gb.cornucopia.cookery.crafting.GuiPresser;
import com.gb.cornucopia.cookery.crafting.GuiStove;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		if (block == Cookery.cutting_board) { // cutting board
			return new ContainerCuttingBoard(player.inventory, world, new BlockPos(x, y ,z));
		}
		else if (block == Cookery.stove){
			return new ContainerStove(player.inventory, (TileEntityStove)world.getTileEntity(new BlockPos(x, y ,z)));
		}
		else if (block == Cookery.presser){
			return new ContainerPresser(player.inventory, (IInventory)world.getTileEntity(new BlockPos(x, y ,z)));
		}
		else if (block == Cookery.mill){
			return new ContainerMill(player.inventory, (IInventory)world.getTileEntity(new BlockPos(x, y ,z)));
		}
		else if (block == Bees.apiary){
			return new ContainerApiary(player.inventory, (IInventory)world.getTileEntity(new BlockPos(x, y ,z)), world, new BlockPos(x, y ,z));
		}
		else {
			throw new RuntimeException("no gui: idk how to server container for this");
		}
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, int x, int y, int z) {
		final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		
		// i dont think these should be passing in world >___> deal w/ it later TODO
		if (block == Cookery.cutting_board) {
			return new GuiCuttingBoard(world, player.inventory, new BlockPos(x,y,z));
		}
		else if (block == Cookery.stove){
			return new GuiStove(player.inventory, (TileEntityStove)world.getTileEntity(new BlockPos(x, y ,z)));
		}
		else if (block == Cookery.presser){
			return new GuiPresser(world, player.inventory, new BlockPos(x, y ,z));
		}
		else if (block == Cookery.mill){
			return new GuiMill(world, player.inventory, new BlockPos(x, y ,z));
		}
		else if (block == Bees.apiary){
			return new GuiApiary(world, player.inventory, new BlockPos(x, y ,z));
		}
		else {
			throw new RuntimeException("no gui: idk how client container/gui for this");
		}
	}
}