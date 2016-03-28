package com.cherryio.advancedHorses.entities;

import net.minecraft.server.v1_9_R1.EntityHorse;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.World;

import java.util.Random;

/**
 * Created by Kieran on 26-Mar-16.
 */
public class AdvancedHorse extends EntityHorse {

    public boolean isNeutered() {
        return isNeutered;
    }

    public int getHorseGender() {
        return horseGender;
    }

    public void setHorseGender(int horseGender) {
        this.horseGender = horseGender;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    private int horseGender; //0 Female, 1 Male
    private boolean isNeutered; //false No, true Yes

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
    }

    public AdvancedHorse(World world) {
        super(world);
    }
}
