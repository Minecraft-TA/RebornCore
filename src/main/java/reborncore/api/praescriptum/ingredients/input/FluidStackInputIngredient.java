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

package reborncore.api.praescriptum.ingredients.input;

import net.minecraftforge.fluids.FluidStack;

import reborncore.common.util.FluidUtils;

import java.util.Objects;

/**
 * @author estebes
 */
public class FluidStackInputIngredient extends InputIngredient<FluidStack> {
    public static FluidStackInputIngredient of(FluidStack ingredient) {
        return new FluidStackInputIngredient(ingredient);
    }

    public static FluidStackInputIngredient copyOf(FluidStack ingredient) {
        return new FluidStackInputIngredient(ingredient.copy());
    }

    protected FluidStackInputIngredient(FluidStack ingredient) {
        super(ingredient);
    }

    @Override
    public Object getUnspecific() {
        return ingredient.getFluid();
    }

    @Override
    public InputIngredient<FluidStack> copy() {
        return of(ingredient.copy());
    }

    @Override
    public boolean isEmpty() {
        return ingredient.amount <= 0;
    }

    @Override
    public int getCount() {
        return ingredient.amount;
    }

    @Override
    public void shrink(int amount) {
        ingredient.amount -= amount;
    }

    @Override
    public boolean matches(Object other) {
        return other instanceof FluidStack &&
                ingredient.isFluidEqual((FluidStack) other);
    }

    @Override
    public boolean matchesStrict(Object other) {
        return other instanceof FluidStack &&
                ingredient.isFluidStackIdentical((FluidStack) other);
    }

    @Override
    public String toFormattedString() {
        return FluidUtils.toFormattedString(ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, consumable);
    }

    @Override
    public boolean equals(Object object) {
        if (getClass() != object.getClass()) return false;

        return matches(((FluidStackInputIngredient) object).ingredient) && this.consumable == ((FluidStackInputIngredient) object).consumable;
    }
}
