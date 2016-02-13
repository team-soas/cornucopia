package com.gb.cornucopia;

import com.gb.cornucopia.bees.Bees;
import com.gb.cornucopia.cookery.Cookery;
import com.gb.cornucopia.cuisine.Cuisine;
import com.gb.cornucopia.cuisine.Dishes;
import com.gb.cornucopia.cuisine.Ingredients;
import com.gb.cornucopia.fruit.Fruits;
import com.gb.cornucopia.veggie.Veggies;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class ServerProxy {

	public void preInit(final FMLPreInitializationEvent e) {
		Veggies.preInit();
		Fruits.preInit();
		Bees.preInit();
		Cookery.preInit();
		Cuisine.preInit();
	}

	public void init(final FMLInitializationEvent e) {
		// TODO: take this ugly shit out and hook into BoP~!
		//GameRegistry.registerWorldGenerator(new DummyGen(), 9999);
		NetworkRegistry.INSTANCE.registerGuiHandler(CornuCopia.instance, new GuiHandler());
	}

	public void postInit(final FMLPostInitializationEvent e) {
		Cookery.initCrafting(); // crafting for kitchen equipment
		
		// very last thing: add ingredients then recipes
		Ingredients.init();
		Dishes.init();
		
		
	}
}