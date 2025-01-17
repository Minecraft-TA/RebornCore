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

package reborncore.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

/**
 * @author estebes
 */
public class PacketSendFluidStack implements INetworkPacket<PacketSendFluidStack> {
    public PacketSendFluidStack() {
        // NO-OP
    }

    public PacketSendFluidStack(int id, FluidStack value) {
        this.id = id;
        this.value = value;
    }

    public PacketSendFluidStack(int id, FluidStack value, Container container) {
        this.id = id;
        this.value = value;
        this.container = container.getClass().getName();
    }

    @Override
    public void writeData(ExtendedPacketBuffer buffer) throws IOException {
        buffer.writeInt(id);
        buffer.writeFluidStack(value);
        buffer.writeInt(container.length());
        buffer.writeString(container);
    }

    @Override
    public void readData(ExtendedPacketBuffer buffer) throws IOException {
        id = buffer.readInt();
        value = buffer.readFluidStack();
        container = buffer.readString(buffer.readInt());
    }

    @Override
    public void processData(PacketSendFluidStack message, MessageContext context) {
        handle();
    }

    @SideOnly(Side.CLIENT)
    public void handle() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof GuiBase){
            BuiltContainer container = ((GuiBase) gui).container;
            if (container != null) container.handleFluidStack(id, value);
        }
    }

    // Fields >>
    int id;
    FluidStack value;
    String container;
    // << Fields
}
