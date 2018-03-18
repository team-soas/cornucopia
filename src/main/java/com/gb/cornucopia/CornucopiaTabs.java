package com.gb.cornucopia;

import com.gb.cornucopia.bees.Bees;
import com.gb.cornucopia.cookery.Cookery;
import com.gb.cornucopia.cuisine.Cuisine;
import com.gb.cornucopia.fruit.Fruit;
import com.gb.cornucopia.veggie.Veggie;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CornucopiaTabs {

	public class BeesCreativeTab extends CreativeTabs {
		public BeesCreativeTab() {
			super(CornuCopia.MODID + "_bees");
		}

		@Override
		public Item getTabIconItem() {
			return Bees.queen;
		}
	}

	public class CookeryCreativeTab extends CreativeTabs {
		public CookeryCreativeTab() {
			super(CornuCopia.MODID + "_cookery");
		}

		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Cookery.cutting_board);
		}
	}

	public class CuisineCreativeTab extends CreativeTabs {
		public CuisineCreativeTab() {
			super(CornuCopia.MODID + "_cuisine");
		}

		@Override
		public Item getTabIconItem() {
			return Cuisine.bread_dough;
		}
	}

	public class FruitCreativeTab extends CreativeTabs {
		public FruitCreativeTab() {
			super(CornuCopia.MODID + "_fruit");
		}

		@Override
		public Item getTabIconItem() {
			return Fruit.rawMap.get(Fruit.Fruits.peach.name());
		}
	}

	public class VeggieCreativeTab extends CreativeTabs {
		public VeggieCreativeTab() {
			super(CornuCopia.MODID + "_veggies");
		}

		@Override
		public Item getTabIconItem() {
			return Veggie.rawMap.get(Veggie.Veggies.asparagus.name());
		}
	}
}