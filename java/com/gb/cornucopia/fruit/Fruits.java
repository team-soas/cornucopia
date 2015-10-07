package com.gb.cornucopia.fruit;

import java.util.HashMap;

import com.gb.cornucopia.CornuCopia;
import com.gb.cornucopia.fruit.block.BlockCropFruit;
import com.gb.cornucopia.fruit.block.BlockLeafFruit;
import com.gb.cornucopia.fruit.block.BlockSaplingFruit;
import com.gb.cornucopia.fruit.item.ItemRawFruit;

import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Fruits {
	protected static final HashMap<String, Fruit> fruitMap = new HashMap<String, Fruit>();

	//region all the fruits
	public static Fruit peach;
	//endregion
	
	public static Fruit createFruit(String name, ItemRawFruit raw, BlockSaplingFruit sapling, BlockLeafFruit leaf, BlockCropFruit crop, IBlockState wood){
		// take fresh instances and save them in static fields
		Fruit fs = new Fruit(raw, sapling, leaf, crop, wood);
		fruitMap.put(name, fs);
		return fs;

	}
	
	public static Fruit createFruit(String name){
		return createFruit(name,
				new ItemRawFruit(name),
				new BlockSaplingFruit(name),
				new BlockLeafFruit(name, BlockPlanks.EnumType.OAK),
				new BlockCropFruit(name),
				Blocks.log.getDefaultState()
				);
	}

	public static void preInit(){
		//region instantiate Fruit objects

		peach = createFruit("peach");

		//endregion
	}
	
	public static void init(){
		createModels();
	}

	private static void createModels(){
		for (Fruit f : fruitMap.values()) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
					f.raw,
					0,
					new ModelResourceLocation(String.format("%s:%s", CornuCopia.MODID, f.raw.name), "inventory") 
				);

			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
					Item.getItemFromBlock(f.sapling),
					0,
					new ModelResourceLocation(String.format("%s:%s", CornuCopia.MODID, f.sapling.name), "inventory")
				);
			
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
					Item.getItemFromBlock(f.leaf),
					0,
					new ModelResourceLocation("minecraft:oak_leaves", "inventory")
				);
			
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
					Item.getItemFromBlock(f.crop),
					0,
					new ModelResourceLocation(String.format("%s:%s", CornuCopia.MODID, f.crop.name), "inventory")
				);
			
		}
	}


}
