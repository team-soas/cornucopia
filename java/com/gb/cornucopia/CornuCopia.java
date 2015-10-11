package com.gb.cornucopia;

import java.util.ArrayList;

import com.gb.cornucopia.bees.Bees;
import com.gb.cornucopia.cookery.Cookery;
import com.gb.cornucopia.fruit.Fruits;
import com.gb.cornucopia.veggie.Veggies;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


@Mod(modid = CornuCopia.MODID, version = CornuCopia.VERSION)
public class CornuCopia
{
	public static final String NAME = "CornuCopia";
    public static final String MODID = "cornucopia";
    public static final String VERSION = "1.0";
    
    @Instance(value = CornuCopia.MODID)
    public static CornuCopia instance;
   
    //region creative tabs
    public static final CreativeTabs tabBees = new CreativeTabs("hc_bees"){
		@Override
		public Item getTabIconItem() {
			return Bees.queen;
		}    	
    };
    public static final CreativeTabs tabRawVeg = new CreativeTabs("hc_rawveg"){
		@Override
		public Item getTabIconItem() {
			return Veggies.asparagus.raw;
		}
    };
    public static final CreativeTabs tabCropVeg = new CreativeTabs("hc_cropveg"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Veggies.asparagus.crop);
		}
    };
    public static final CreativeTabs tabSeedVeg = new CreativeTabs("hc_seedveg"){
		@Override
		public Item getTabIconItem() {
			return Veggies.asparagus.seed;
		}
    };
    public static final CreativeTabs tabWildVeg = new CreativeTabs("hc_wildveg"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Veggies.asparagus.wild);
		}
    };
    public static final CreativeTabs tabRawFruit = new CreativeTabs("hc_rawfruit"){
		@Override
		public Item getTabIconItem() {
			return Fruits.peach.raw;
		}
    };    
	public static final CreativeTabs tabCropFruit = new CreativeTabs("hc_cropfruit"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Fruits.peach.crop);
		}
    };
	public static final CreativeTabs tabSaplingFruit = new CreativeTabs("hc_saplingfruit"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Fruits.peach.sapling);
		}
    };
	public static final CreativeTabs tabLeafFruit = new CreativeTabs("hc_leaffruit"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Fruits.peach.leaf);
		}
    };
    public static final CreativeTabs tabCookeryBlock = new CreativeTabs("hc_cookeryblock"){
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Cookery.cutting_board);
		}
    };
    
    //endregion
 
    @SidedProxy(clientSide="com.gb.cornucopia.ClientProxy", serverSide="com.gb.cornucopia.ServerProxy")
    public static ServerProxy proxy;


    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
    	this.proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
    	this.proxy.init(e);
    }
    
    @EventHandler
    public void PostInit(FMLPostInitializationEvent e)
    {
    	this.proxy.postInit(e);
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
      event.registerServerCommand(new CMDGrowTree());
    }
}
