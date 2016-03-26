package com.cherryio.entities;

import net.minecraft.server.v1_9_R1.EntityHorse;
import net.minecraft.server.v1_9_R1.World;

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

    private int horseGender;
    private boolean isNeutered;

    public AdvancedHorse(World world, int horseGender, boolean isNeutered) {
        super(world);
        this.canPickUpLoot = false;
        this.persistent = true;
        this.horseGender = horseGender;
        this.isNeutered = isNeutered;
    }

    public AdvancedHorse(World world) {
        super(world);
    }
}
