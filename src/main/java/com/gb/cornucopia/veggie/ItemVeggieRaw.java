package com.gb.cornucopia.veggie;

import com.gb.cornucopia.CornuCopia;
import com.gb.cornucopia.InvModel;
import net.minecraft.item.ItemFood;

public class ItemVeggieRaw extends ItemFood {
	public final String name;

	public ItemVeggieRaw(final String name) {
		super(3, 0.6F, false);
		this.name = String.format("veggie_%s_raw", name);
		this.setUnlocalizedName(this.name);
		this.setRegistryName(this.name);
		this.setCreativeTab(CornuCopia.tabVeggies);
		InvModel.add(this);
	}

}