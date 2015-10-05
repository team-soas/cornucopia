package com.gb.harvestcraft.fruit;

import com.gb.harvestcraft.fruit.block.BlockCropFruit;
import com.gb.harvestcraft.fruit.block.BlockLeafFruit;
import com.gb.harvestcraft.fruit.block.BlockSaplingFruit;
import com.gb.harvestcraft.fruit.item.ItemRawFruit;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class Fruit {
	public final ItemRawFruit raw;
	public final BlockSaplingFruit sapling;
	public final BlockLeafFruit leaf;
	public final BlockCropFruit crop;
		
	public Fruit(ItemRawFruit raw, BlockSaplingFruit sapling, BlockLeafFruit leaf, BlockCropFruit crop, IBlockState wood){
		this.raw = raw;
		this.sapling = sapling;
		this.leaf = leaf;
		this.crop = crop;
		
		// hook up needfuls 
		this.sapling.setTreeStates(wood, leaf.getDefaultState());
		this.leaf.setGrows(crop);
		this.crop.setLeaf(leaf);
		this.crop.setDrops(raw, sapling);
	}
}
