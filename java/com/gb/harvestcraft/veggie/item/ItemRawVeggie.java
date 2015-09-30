package com.gb.harvestcraft.veggie.item;

import com.gb.harvestcraft.HarvestCraft;

import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRawVeggie extends ItemFood{
	public final String name; 
	
	public ItemRawVeggie(String name) {
		// raw veggies: not very filling, also not enjoyed by doggies
		super(1, 0.3F, false);
		this.name = "veggie_raw_" + name;
		
		this.setUnlocalizedName(this.name);
		this.setCreativeTab(HarvestCraft.tabRawVeg);
		GameRegistry.registerItem(this, this.name);
	}

}
