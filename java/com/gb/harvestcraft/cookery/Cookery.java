package com.gb.harvestcraft.cookery;

import com.gb.harvestcraft.HarvestCraft;
import com.gb.harvestcraft.cookery.item.ItemCookWare;
import com.gb.harvestcraft.fruit.Fruits;
import com.gb.harvestcraft.veggie.Veggies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Cookery {	
	//region yawn static fields;
	public static ItemCookWare juicer;
	
	public static Ingredient mirepoix_part;
	public static Ingredient sweet_berry;
	public static Ingredient citrus;
	//endregion
	
	public static void preInit(){
		juicer = new ItemCookWare("juicer");
		
		mirepoix_part = new Ingredient("mirepoix_part");
		sweet_berry = new Ingredient("sweet_berry");
		citrus = new Ingredient("citrus");
	};
	
	public static void init(){
		cookwareModels();
		registerIngredients();
		registerRecipes(); // < may move this to post-init
	}
	
	private static void cookwareModels(){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
				juicer,
				0,
				new ModelResourceLocation(String.format("%s:%s", HarvestCraft.MODID, juicer.name), "inventory") 
			);
	}
	
	private static void registerIngredients(){
		mirepoix_part
			.add(Veggies.onion.raw)
			.add(Veggies.celery.raw)
			.add(Veggies.leek.raw)
			.add(Veggies.scallion.raw)
			.add(Veggies.bellpepper.raw)
			.add(Veggies.garlic.raw)
			.add(Items.carrot);
		
		sweet_berry
			.add(Veggies.blueberry.raw)
			.add(Veggies.blackberry.raw)
			.add(Veggies.raspberry.raw)
			.add(Veggies.strawberry.raw)
			.add(Veggies.cranberry.raw);
			
		citrus
			.add(Fruits.lemon.raw)
			.add(Fruits.lime.raw)
			.add(Fruits.grapefruit.raw)
			.add(Fruits.orange.raw);
		
	}
	
	private static void registerRecipes(){
		GameRegistry.addShapelessRecipe(new ItemStack(Fruits.fig.raw), juicer, Veggies.broccoli.raw);
	}
	
}