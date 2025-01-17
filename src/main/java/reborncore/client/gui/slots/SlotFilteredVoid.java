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


package reborncore.client.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlotFilteredVoid extends BaseSlot {

	private List<ItemStack> filter = new ArrayList<ItemStack>();

	public SlotFilteredVoid(IInventory par1iInventory, int id, int x, int y) {
		super(par1iInventory, id, x, y);
	}

	public SlotFilteredVoid(IInventory par1iInventory, int id, int x, int y, ItemStack[] filterList) {
		super(par1iInventory, id, x, y);
		for (ItemStack itemStack : filterList)
			this.filter.add(itemStack);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		for (ItemStack itemStack : filter)
			if (itemStack.getItem().equals(stack.getItem()) && itemStack.getItemDamage() == stack.getItemDamage())
				return false;

		return super.isItemValid(stack);
	}

	@Override
	public void putStack(ItemStack arg0) {
	}
}
