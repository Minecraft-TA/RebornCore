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


package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class TilePowerAcceptor extends RebornMachineTile implements
	IEnergyInterfaceTile, IListInfoProvider // TechReborn
{
	private EnumPowerTier tier;
	private double energy;

	public double extraPowerStoage;
	public double extraPowerInput;
	public int extraTeir;
	public double powerChange;
	public double powerLastTick;
	public boolean checkOverfill = true; //Set to false to disable the overfill check.
	// Some external power systems (EU) support multiple energy packets per tick, this allows machines to possibly emit
	// multiple packets in a tick. Other power systems such as FE will ignore this option.
	public int maxPacketsPerTick = 1;

	private List<ExternalPowerHandler> powerManagers;

	public TilePowerAcceptor() {
		checkTeir();
		setupManagers();
	}

	// don't manually set tiers
	@Deprecated
	public TilePowerAcceptor(EnumPowerTier tier) {
		this();
	}

	private void setupManagers(){
		final TilePowerAcceptor tile = this;
        List<ExternalPowerHandler> list = new ArrayList<>();
        for (ExternalPowerManager externalPowerManager : ExternalPowerSystems.externalPowerHandlerList) {
            ExternalPowerHandler powerHandler = externalPowerManager.createPowerHandler(tile);
            if (powerHandler != null) {
                list.add(powerHandler);
            }
        }
        powerManagers = list;
	}


	public void checkTeir() {
		if (getBaseTier() == null) {
			if (this.getMaxInput() == 0) {
				tier = EnumPowerTier.getTeir((int) this.getBaseMaxOutput());
			} else {
				tier = EnumPowerTier.getTeir((int) this.getBaseMaxInput());
			}
		}
	}

	public void setExtraPowerStoage(double extraPowerStoage) {
		this.extraPowerStoage = extraPowerStoage;
	}

	public void setMaxPacketsPerTick(int maxPacketsPerTick) {
		this.maxPacketsPerTick = maxPacketsPerTick;
	}

	public double getFreeSpace() {
		return getMaxPower() - getEnergy();
	}

	/**
	 * Charge machine from battery placed inside inventory slot
	 *
	 * @param slot int Slot ID for battery slot
	 */
	public void charge(int slot) {
		if (world.isRemote) {
			return;
		}

		double chargeEnergy = Math.min(getFreeSpace(), getMaxInput());
		if (chargeEnergy <= 0.0 ) {
			return;
		}

		ItemStack batteryStack = getStackInSlot(slot);
		if (batteryStack.isEmpty()) {
			return;
		}

		if (ExternalPowerSystems.isPoweredItem(batteryStack)) {
			ExternalPowerSystems.dischargeItem(this, batteryStack);
		}

	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
	}

	public void readFromNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT())
			this.setEnergy(data.getDouble("energy"));
	}

	public NBTTagCompound writeToNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", energy);
		tag.setTag("TilePowerAcceptor", data);
		return tag;
	}

	public boolean shouldHanldeEnergyNBT() {
		return true;
	}

	public boolean handleTierWithPower() {
		return true;
	}

	public double getPowerChange() {
		return powerChange;
	}

	public void setPowerChange(double powerChange) {
		this.powerChange = powerChange;
	}


	// RebornMachineTile
	@Override
	public void update() {
		super.update();
		if(world.isRemote){
			return;
		}

        for (ExternalPowerHandler powerManager : powerManagers) {
            powerManager.tick();
        }

        powerChange = getEnergy() - powerLastTick;
		powerLastTick = getEnergy();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT())
			this.setEnergy(data.getDouble("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", getEnergy());
		tag.setTag("TilePowerAcceptor", data);
		return tag;
	}

	@Override
	public void resetUpgrades() {
		super.resetUpgrades();
		extraPowerStoage = 0;
		extraTeir = 0;
		extraPowerInput = 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        for (ExternalPowerHandler externalPowerHandler : powerManagers) {
            if (externalPowerHandler != null) {
                if (externalPowerHandler.hasCapability(capability, facing)) {
                    return true;
                }
            }
        }
        return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        T externalCap = null;
        for (ExternalPowerHandler externalPowerHandler : powerManagers) {
            if (externalPowerHandler != null) {
                T externalPowerHandlerCapability = externalPowerHandler.getCapability(capability, facing);
                if (externalPowerHandlerCapability != null) {
                    externalCap = externalPowerHandlerCapability;
                    break;
                }
            }
        }

        if(externalCap != null){
			return externalCap;
		}

		return super.getCapability(capability, facing);
	}

	public abstract double getBaseMaxPower();

	public abstract double getBaseMaxOutput();

	public abstract double getBaseMaxInput();

	@Deprecated //Dont set the tier any where
	public EnumPowerTier getBaseTier() {
		return null;
	}

	// TileEntity
	@Override
	public void invalidate() {
		super.invalidate();

        for (ExternalPowerHandler powerManager : powerManagers) {
            powerManager.invalidate();
        }
    }

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();

        for (ExternalPowerHandler powerManager : powerManagers) {
            powerManager.unload();
        }
    }

	// IEnergyInterfaceTile
	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		if(!checkOverfill){
			this.energy = energy;
			return;
		}
		this.energy = Math.max(Math.min(energy, getMaxPower()), 0);
	}

	@Override
	public double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	@Override
	public double addEnergy(double energy, boolean simulate) {
		double energyReceived = Math.min(getMaxPower(), Math.min(getFreeSpace(), energy));

		if (!simulate) {
			setEnergy(getEnergy() + energyReceived);
		}
		return energyReceived;
	}

	@Override
	public boolean canUseEnergy(double input) {
		return input <= energy;
	}

	@Override
	public double useEnergy(double energy) {
		return useEnergy(energy, false);
	}

	@Override
	public double useEnergy(double extract, boolean simulate) {
		if (extract > energy) {
			extract = energy;
		}
		if (!simulate) {
			setEnergy(energy - extract);
		}
		return extract;
	}

	@Override
	public boolean canAddEnergy(double energyIn) {
		return getEnergy() + energyIn <= getMaxPower();
	}

	@Override
	public double getMaxPower() {
		return getBaseMaxPower() + extraPowerStoage;
	}

	@Override
	public double getMaxOutput() {
		double maxOutput = 0;
		if (this.extraTeir > 0) {
			maxOutput = this.getTier().getMaxOutput();
		}
		else {
			maxOutput = getBaseMaxOutput();
		}
		return maxOutput;
	}

	@Override
	public double getMaxInput() {
		double maxInput = 0;
		if (this.extraTeir > 0) {
			maxInput = this.getTier().getMaxInput();
		}
		else {
			maxInput = getBaseMaxInput();
		}
		return maxInput + extraPowerInput;
	}

	public EnumPowerTier getPushingTier() {
		return getTier();
	}

	@Override
	public EnumPowerTier getTier() {
		EnumPowerTier baseTier = getBaseTier();
		if (baseTier == null) {
			if (tier == null) {
				checkTeir();
			}
			baseTier = tier;
		}
		if (extraTeir > 0) {
			for (EnumPowerTier tier : EnumPowerTier.values()) {
				if (tier.getIC2Tier() == baseTier.getIC2Tier() + extraTeir) {
					return tier;
				}
			}
			return EnumPowerTier.INFINITE;
		}
		return baseTier;
	}

	// IListInfoProvider
	@Override
	public void addInfo(List<String> info, boolean isRealTile) {
		info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.maxEnergy") + ": "
				+ TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxPower()));
		if (getMaxInput() != 0) {
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.inputRate") + ": "
					+ TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxInput()));
		}
		if (getMaxOutput() != 0) {
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.outputRate")
					+ ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxOutput()));
		}
		info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.tier") + ": "
				+ TextFormatting.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString()));
		if(isRealTile){
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.change")
				+ ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getPowerChange()) + "/t");
		}
	}

	// Old cofh stuff, still used to implement Forge Energy, should be removed at somepoint
	@Deprecated
	public boolean canConnectEnergy(EnumFacing from) {
		return canAcceptEnergy(from) || canProvideEnergy(from);
	}

	@Deprecated
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!canAcceptEnergy(from)) {
			return 0;
		}
		int feReceived = (int) Math.min(getMaxEnergyStored(from) - getEnergyStored(from), Math.min(getMaxInput() * RebornCoreConfig.euPerFU, maxReceive));
		int euReceived = feReceived / RebornCoreConfig.euPerFU;
		feReceived = euReceived * RebornCoreConfig.euPerFU;

		if (!simulate) {
			setEnergy(getEnergy() + euReceived);
		}
		return feReceived;
	}

	@Deprecated
	public int getEnergyStored(EnumFacing from) {
		return ((int) getEnergy() * RebornCoreConfig.euPerFU);
	}

	@Deprecated
	public int getMaxEnergyStored(EnumFacing from) {
		return ((int) getMaxPower() * RebornCoreConfig.euPerFU);
	}

	@Deprecated
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (!canProvideEnergy(from)) {
			return 0;
		}
		int euExtracted = Math.min(getEnergyStored(null), maxExtract / RebornCoreConfig.euPerFU);
		int feExtracted = euExtracted * RebornCoreConfig.euPerFU;

		if (!simulate) {
			setEnergy(getEnergy() - euExtracted);
		}
		return feExtracted;
	}
}
