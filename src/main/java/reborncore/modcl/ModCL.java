/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package reborncore.modcl;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prospector
 */
public abstract class ModCL {
	public CreativeTabCL tab = new CreativeTabCL(this);
	public List<Item> itemModelsToRegister = new ArrayList<>();
	public List<Block> blockModelsToRegister = new ArrayList<>();
	public HashMap<ItemMetadataCL, String> customBlockStates = new HashMap<>();

	public CreativeTabs getTab() {
		return tab;
	}

	public abstract String getServerProxy();

	public abstract String getClientProxy();

	public abstract RegistryCL getRegistry();

	public ItemStack getTabStack() {
		return ItemStack.EMPTY;
	}

	public String getPrefix() {
		return getModID() + ":";
	}

	public abstract String getModName();

	public abstract String getModID();

	public abstract String getModVersion();

	public abstract String getModDependencies();

	private class CreativeTabCL extends CreativeTabs {
		ModCL mod;

		public CreativeTabCL(ModCL mod) {
			super(mod.getModID());
			this.mod = mod;
		}

		@Override
		public ItemStack createIcon() {
			return mod.getTabStack();
		}
	}
}
