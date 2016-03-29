package com.cherryio.advancedHorses.entities;

import com.cherryio.advancedHorses.utils.Config;
import com.cherryio.advancedHorses.utils.Utils;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Effect;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Kieran on 26-Mar-16.
 */
public class AdvancedHorse extends EntityHorse {

    private int horseGender; //0 Female, 1 Male
    private boolean isNeutered; //false No, true Yes
    private int hungerLevel;
    private int hydrationLevel;
    private int bO;
    private int bV;
    private int domestication;

    public AdvancedHorse(World world, int horseGender, boolean isNeutered) {
        super(world);
        this.canPickUpLoot = false;
        this.persistent = true;
        this.horseGender = horseGender;
        this.isNeutered = isNeutered;
        double speed = new Random().nextFloat() * (0.3375 - 0.1125) + 0.1;
        double jump = new Random().nextFloat() * (1 - 0.4) + 0.35;
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        this.getAttributeInstance(attributeJumpStrength).setValue(jump);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((new Random().nextInt(16) + 15));
        this.setHealth((float) this.getAttributeInstance(GenericAttributes.maxHealth).getValue());
        this.setHungerLevel(500);
        this.setHydrationLevel(500);
        this.setDomestication(1);
    }

    public AdvancedHorse(World world) {
        super(world);
    }

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public int getHorseGender() {
        return horseGender;
    }

    public void setHorseGender(int horseGender) {
        this.horseGender = horseGender;
    }

    public int getHungerLevel() {
        return hungerLevel;
    }


    public int getDomestication() {
        return domestication;
    }

    private void setHungerLevel(int hungerLevel) {
        if (hungerLevel > 500) {
            this.hungerLevel = 500;
            return;
        }
        if (hungerLevel < 0) {
            this.hungerLevel = 0;
            return;
        }
        this.hungerLevel = hungerLevel;
    }

    public int getHydrationLevel() {
        return hydrationLevel;
    }

    private void setHydrationLevel(int hydrationLevel) {
        if (hydrationLevel > 500) {
            this.hydrationLevel = 500;
            return;
        }
        if (hydrationLevel < 0) {
            this.hydrationLevel = 0;
            return;
        }
        this.hydrationLevel = hydrationLevel;
    }

    public double getHorseSpeed() {
        return this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    public void setSpeed(double avgParentSpeed) {
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(avgParentSpeed);
    }

    public void setMaxHP(double avgParentHP) {
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(avgParentHP);
    }

    public void setJump(double avgParentJump) {
        this.getAttributeInstance(attributeJumpStrength).setValue(avgParentJump);
    }

    public void setDomestication(int domestication) {
        if (domestication > 100) {
            this.domestication = 100;
            return;
        }
        if (domestication < 0) {
            this.domestication = 0;
            return;
        }
        this.domestication = domestication;
    }

    @Override
    public void n() {
        if(this.random.nextInt(200) == 0) {
            this.bv = 1;
        }

        super.n();
        if(!this.world.isClientSide) {
            if (this.isTamed()) {
                if (this.random.nextInt(2000) == 0) {
                    if (!Utils.getNearbyWaterSource(this.getBukkitEntity().getLocation(), 8)) {
                        if (this.isVehicle()) {
                            this.setHydrationLevel(this.getHydrationLevel() - 2);
                            if (this.getHydrationLevel() <= 50) {
                                this.setSpeed(this.getHorseSpeed() / 2);
                                this.setJump(this.getJumpStrength() / 2);
                            }
                        } else {
                            this.setHydrationLevel(this.getHydrationLevel() - 1);
                            if (this.getHydrationLevel() <= 50) {
                                this.setSpeed(this.getHorseSpeed() / 2);
                                this.setJump(this.getJumpStrength() / 2);
                            }
                        }
                    } else {
                        this.waterHorse();
                    }
                }
                if (this.random.nextInt(1500) == 0) {
                    if (!Utils.getNearbyFoodSource(this.getBukkitEntity().getLocation(), 8)) {
                        if (this.isVehicle()) {
                            this.setHungerLevel(this.getHydrationLevel() - 2);
                            if (this.getHungerLevel() <= 0) {
                                if (new Config<Boolean>("settings.horseDeathNoFood").getValue()) {
                                    this.die();
                                }
                            }
                        } else {
                            this.setHungerLevel(this.getHydrationLevel() - 1);
                            if (this.getHungerLevel() <= 0) {
                                if (new Config<Boolean>("settings.horseDeathNoFood").getValue()) {
                                    this.die();
                                }
                            }
                        }
                    }
                } else {
                    this.feedHorse();
                }
                if (this.getDomestication() < 100) {
                    if (this.isVehicle()) {
                        if (this.random.nextInt(1000) == 0) {
                            if (this.random.nextInt(100) > this.getDomestication()) {
                                this.getBukkitEntity().setPassenger(null);
                                this.getBukkitEntity().getWorld().playEffect(this.getBukkitEntity().getLocation(), Effect.SMOKE, 1);
                            }
                            this.setDomestication(this.getDomestication() + 2);
                        }
                    }
                }
            }

            if(this.random.nextInt(900) == 0 && this.deathTicks == 0) {
                this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN);
            }

            if(!this.dm() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY) - 1, MathHelper.floor(this.locZ))).getBlock() == Blocks.GRASS) {
                this.u(true);
            }

            if(this.dm() && ++this.bO > 50) {
                this.bO = 0;
                this.u(false);
            }

            if(this.do_() && !this.db() && !this.dm()) {
                EntityHorse entityhorse = this.a(this, 16.0D);
                if(entityhorse != null && this.h(entityhorse) > 4.0D) {
                    this.navigation.a(entityhorse);
                }
            }

            if(this.dG() && this.bV++ >= 18000) {
                this.die();
            }
        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.dm());
        nbttagcompound.setBoolean("ChestedHorse", this.hasChest());
        nbttagcompound.setBoolean("HasReproduced", this.hasReproduced());
        nbttagcompound.setBoolean("Bred", this.do_());
        nbttagcompound.setInt("Type", this.getType().k());
        nbttagcompound.setInt("Variant", this.getVariant());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTamed());
        nbttagcompound.setBoolean("SkeletonTrap", this.dG());
        nbttagcompound.setInt("SkeletonTrapTime", this.bV);
        nbttagcompound.setInt("GenderValue", this.getHorseGender());
        nbttagcompound.setBoolean("NeuterValue", this.isNeutered());
        nbttagcompound.setInt("HungerValue", this.getHungerLevel());
        nbttagcompound.setInt("HydrationValue", this.getHydrationLevel());
        nbttagcompound.setInt("DomesticationValue", this.getDomestication());
        if(this.getOwnerUUID() != null) {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }

        nbttagcompound.setInt("Bukkit.MaxDomestication", this.maxDomestication);
        if(this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for(int i = 2; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);
                if(itemstack != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte)i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

        if(this.inventoryChest.getItem(1) != null) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

        if(this.inventoryChest.getItem(0) != null) {
            nbttagcompound.set("SaddleItem", this.inventoryChest.getItem(0).save(new NBTTagCompound()));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.u(nbttagcompound.getBoolean("EatingHaystack"));
        this.q(nbttagcompound.getBoolean("Bred"));
        this.setHasChest(nbttagcompound.getBoolean("ChestedHorse"));
        this.s(nbttagcompound.getBoolean("HasReproduced"));
        this.setType(EnumHorseType.a(nbttagcompound.getInt("Type")));
        this.setVariant(nbttagcompound.getInt("Variant"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTame(nbttagcompound.getBoolean("Tame"));
        this.x(nbttagcompound.getBoolean("SkeletonTrap"));
        this.bV = nbttagcompound.getInt("SkeletonTrapTime");
        this.setHorseGender(nbttagcompound.getInt("GenderValue"));
        this.setNeutered(nbttagcompound.getBoolean("NeuterValue"));
        this.setHungerLevel(nbttagcompound.getInt("HungerValue"));
        this.setHydrationLevel(nbttagcompound.getInt("HydrationValue"));
        this.setDomestication(nbttagcompound.getInt("DomesticationValue"));
        String s;
        if(nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String attributeinstance = nbttagcompound.getString("Owner");
            s = NameReferencingFileConverter.a(this.h(), attributeinstance);
        }

        if(!s.isEmpty()) {
            this.setOwnerUUID(UUID.fromString(s));
        }

        if(nbttagcompound.hasKey("Bukkit.MaxDomestication")) {
            this.maxDomestication = nbttagcompound.getInt("Bukkit.MaxDomestication");
        }

        AttributeInstance var8 = this.getAttributeMap().a("Speed");
        if(var8 != null) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(var8.b() * 0.25D);
        }

        if(this.hasChest()) {
            NBTTagList itemstack = nbttagcompound.getList("Items", 10);
            this.loadChest();

            for(int i = 0; i < itemstack.size(); ++i) {
                NBTTagCompound nbttagcompound1 = itemstack.get(i);
                int j = nbttagcompound1.getByte("Slot") & 255;
                if(j >= 2 && j < this.inventoryChest.getSize()) {
                    this.inventoryChest.setItem(j, ItemStack.createStack(nbttagcompound1));
                }
            }
        }

        ItemStack var9;
        if(nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            var9 = ItemStack.createStack(nbttagcompound.getCompound("ArmorItem"));
            if(var9 != null && EnumHorseArmor.b(var9.getItem())) {
                this.inventoryChest.setItem(1, var9);
            }
        }

        if(nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            var9 = ItemStack.createStack(nbttagcompound.getCompound("SaddleItem"));
            if(var9 != null && var9.getItem() == Items.SADDLE) {
                this.inventoryChest.setItem(0, var9);
            }
        }
        this.dK();
    }

    private void dK() {
        if(!this.world.isClientSide) {
            this.t(this.inventoryChest.getItem(0) != null);
            if(this.getType().j()) {
                this.f(this.inventoryChest.getItem(1));
            }
        }

    }

    public void feedHorse() {
        this.setHungerLevel(this.getHungerLevel() + new Config<Integer>("settings.hungerFromFood").getValue());
    }

    public void waterHorse() {
        final int prevLevel = this.getHydrationLevel();
        this.setHydrationLevel(this.getHydrationLevel() + new Config<Integer>("settings.thirstFromWater").getValue());
        if (this.getHydrationLevel() > 50 && prevLevel <= 50) {
            this.setSpeed(this.getHorseSpeed() * 2);
            this.setJump(this.getJumpStrength() * 2);
        }
    }
}
