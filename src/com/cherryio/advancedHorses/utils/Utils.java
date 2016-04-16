package com.cherryio.advancedHorses.utils;

import com.cherryio.advancedHorses.entities.AdvancedHorse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

/**
 * Created by Kieran on 29-Mar-16.
 */
public class Utils {

    public static boolean getNearbyWaterSource(Location location, int radius) {
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER) {
                        return true;
                    } else if (block.getType() == Material.CAULDRON) {
                        if (!block.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean getNearbyFoodSource(Location location, int radius) {
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.HAY_BLOCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void spawnHorse(Location location) {
        int gender;
        if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
            gender = 1;
        } else {
            gender = 0;
        }
        AdvancedHorse advancedHorse = new AdvancedHorse(((CraftWorld) location.getWorld()).getHandle(), gender, false);
        advancedHorse.setLocation(location.getX(), location.getY(), location.getZ(), 0 ,0);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(advancedHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
        advancedHorse.setLocation(location.getX(), location.getY(), location.getZ(), 0 ,0);
    }
}
