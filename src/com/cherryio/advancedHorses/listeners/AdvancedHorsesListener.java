package com.cherryio.advancedHorses.listeners;

import com.cherryio.advancedHorses.entities.AdvancedHorse;
import com.cherryio.advancedHorses.utils.Config;
import net.minecraft.server.v1_9_R1.EnumHorseType;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Created by Kieran on 24-Mar-16 for CherryIO.
 */
public class AdvancedHorsesListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerMoveOnHorse(PlayerMoveEvent event) {
        if (event.getPlayer().getVehicle() == null || event.getPlayer().getVehicle().getType() != EntityType.HORSE) return;
        Player player = event.getPlayer();
        Horse horse = (Horse) player.getVehicle();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void viewStats(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Horse)) return;
        Player player = (Player) event.getDamager();
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.LEASH) return;
        Horse horse = (Horse) event.getEntity();
        event.setDamage(0);
        event.setCancelled(true);
        AdvancedHorse advancedHorse1 = (AdvancedHorse) ((CraftLivingEntity) horse).getHandle();
        player.sendMessage(ChatColor.GRAY + "---------------------------");
        if (horse.getCustomName() == null || horse.getCustomName().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Name" + ChatColor.GRAY + ": " + ChatColor.AQUA + "None");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Name" + ChatColor.GRAY + ": " + ChatColor.AQUA + horse.getCustomName());
        }
        if (advancedHorse1.getHorseGender() == 1) {
            player.sendMessage(ChatColor.YELLOW + "Gender" + ChatColor.GRAY + ": " + ChatColor.AQUA + "Male");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Gender" + ChatColor.GRAY + ": " + ChatColor.AQUA + "Female");
        }
        if (advancedHorse1.isNeutered()) {
            player.sendMessage(ChatColor.YELLOW + "Neutered" + ChatColor.GRAY + ": " + ChatColor.AQUA + "True");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Neutered" + ChatColor.GRAY + ": " + ChatColor.AQUA + "False");
        }
        double jump = horse.getJumpStrength();
        double blockJumpHeight = -0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367;
        blockJumpHeight = (blockJumpHeight*100);
        blockJumpHeight = Math.round(blockJumpHeight);
        blockJumpHeight /= 100;
        double speed = advancedHorse1.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
        double blockPerSecondSpeed = 4.3 * speed * 10;
        blockPerSecondSpeed = (blockPerSecondSpeed*100);
        blockPerSecondSpeed = Math.round(blockPerSecondSpeed);
        blockPerSecondSpeed /= 100;
        player.sendMessage(ChatColor.YELLOW + "Jump Height (Blocks)" + ChatColor.GRAY + ": " + ChatColor.AQUA + blockJumpHeight);
        player.sendMessage(ChatColor.YELLOW + "Speed (Blocks/Second)" + ChatColor.GRAY + ": " + ChatColor.AQUA + blockPerSecondSpeed);
        player.sendMessage(ChatColor.YELLOW + "Health" + ChatColor.GRAY + ": " + ChatColor.AQUA + Math.round(horse.getHealth()) + ChatColor.GRAY + "/" + ChatColor.AQUA + Math.round(horse.getMaxHealth()));
        player.sendMessage(ChatColor.YELLOW + "Hunger" + ChatColor.GRAY + ": " + ChatColor.AQUA + advancedHorse1.getHungerLevel() + ChatColor.GRAY + "/" + ChatColor.AQUA + "500");
        player.sendMessage(ChatColor.YELLOW + "Hydration" + ChatColor.GRAY + ": " + ChatColor.AQUA + advancedHorse1.getHydrationLevel() + ChatColor.GRAY + "/" + ChatColor.AQUA + "500");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void horseDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.HORSE) return;
        final Horse deadHorse = (Horse) event.getEntity();
        if (deadHorse.getVariant() == Horse.Variant.UNDEAD_HORSE || deadHorse.getVariant() == Horse.Variant.SKELETON_HORSE) return;
        if (new Config<Boolean>("settings.undeadHorseAfterDeath").getValue()) {
            if (new Random().nextInt(99) < new Config<Integer>("settings.undeadHorseAfterDeathChance").getValue()) {
                int gender;
                if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
                    gender = 1;
                } else {
                    gender = 0;
                }
                AdvancedHorse advancedHorse = new AdvancedHorse(((CraftWorld) event.getEntity().getWorld()).getHandle(), gender, false);
                advancedHorse.setLocation(deadHorse.getLocation().getX(), deadHorse.getLocation().getY(), deadHorse.getLocation().getZ(), 0 ,0);
                if (new Random().nextBoolean()) {
                    advancedHorse.setType(EnumHorseType.SKELETON);
                } else {
                    advancedHorse.setType(EnumHorseType.ZOMBIE);
                }
                ((CraftWorld) event.getEntity().getWorld()).getHandle().addEntity(advancedHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
                advancedHorse.setLocation(deadHorse.getLocation().getX(), deadHorse.getLocation().getY(), deadHorse.getLocation().getZ(), 0 ,0);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void horseSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.HORSE) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING) return;
        final Entity horse = event.getEntity();
        if (!event.getEntity().isDead()) {
            event.getEntity().remove();
        }
        int gender;
        if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
            gender = 1;
        } else {
            gender = 0;
        }
        AdvancedHorse advancedHorse = new AdvancedHorse(((CraftWorld) event.getEntity().getWorld()).getHandle(), gender, false);
        advancedHorse.setLocation(horse.getLocation().getX(), horse.getLocation().getY(), horse.getLocation().getZ(), 0 ,0);
        ((CraftWorld) event.getEntity().getWorld()).getHandle().addEntity(advancedHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
        advancedHorse.setLocation(horse.getLocation().getX(), horse.getLocation().getY(), horse.getLocation().getZ(), 0 ,0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void horseMate(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) return;
        if (event.getEntity().getType() != EntityType.HORSE) return;
        final Horse babyHorse = (Horse) event.getEntity();
        boolean found = false;
        boolean found2 = false;
        Horse parent1 = babyHorse;
        Horse parent2 = babyHorse;
        boolean shouldSpawn = true;
        for (int i = 0; i < 2; i++) {
            List<Entity> entities = babyHorse.getNearbyEntities(i, 0, i);
            for (Entity e : entities) {
                if (e.getType().equals(EntityType.HORSE)) {
                    if (found) {
                        if (!parent1.equals(e)) {
                            found2 = true;
                            parent2 = (Horse) e;
                        }
                    } else {
                        found = true;
                        parent1 = (Horse) e;
                    }
                    break;
                }
            }
            if (found2) break;
        }
        event.getEntity().remove();
        if (found && found2) {
            AdvancedHorse advancedHorse1 = (AdvancedHorse) ((CraftLivingEntity) parent1).getHandle();
            AdvancedHorse advancedHorse2 = (AdvancedHorse) ((CraftLivingEntity) parent2).getHandle();
            if (advancedHorse1.isNeutered() || advancedHorse2.isNeutered() ) {
                shouldSpawn = false;
            }
            if (advancedHorse1.getHorseGender() == 1) {
                if (advancedHorse1.getHorseGender() == 1) {
                    shouldSpawn = false;
                }
            } else if (advancedHorse1.getHorseGender() == 0) {
                if (advancedHorse1.getHorseGender() == 0) {
                    shouldSpawn = false;
                }
            }
            if (shouldSpawn) {
                int gender;
                if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
                    gender = 1;
                } else {
                    gender = 0;
                }
                AdvancedHorse advancedHorse = new AdvancedHorse(((CraftWorld) event.getEntity().getWorld()).getHandle(), gender, false);
                advancedHorse.setLocation(babyHorse.getLocation().getX(), babyHorse.getLocation().getY(), babyHorse.getLocation().getZ(), 0 ,0);
                ((CraftWorld) event.getEntity().getWorld()).getHandle().addEntity(advancedHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
                advancedHorse.setLocation(babyHorse.getLocation().getX(), babyHorse.getLocation().getY(), babyHorse.getLocation().getZ(), 0 ,0);
                advancedHorse.setMaxHP((advancedHorse1.getMaxHealth() + advancedHorse2.getMaxHealth()) / 1.9);
                advancedHorse.setJump((advancedHorse1.getJumpStrength() + advancedHorse2.getJumpStrength()) / 1.9);
                advancedHorse.setSpeed((advancedHorse1.getHorseSpeed() + advancedHorse2.getHorseSpeed()) / 1.9);
                org.bukkit.entity.Horse horse = (org.bukkit.entity.Horse) advancedHorse.getBukkitEntity();
                horse.setBaby();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void neuterHorse(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Horse)) return;
        Player player = (Player) event.getDamager();
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.SHEARS) return;
        Horse horse = (Horse) event.getEntity();
        AdvancedHorse advancedHorse1 = (AdvancedHorse) ((CraftLivingEntity) horse).getHandle();
        event.setDamage(0);
        event.setCancelled(true);
        if (advancedHorse1.getHorseGender() == 1) {
            if (!advancedHorse1.isNeutered()) {
                advancedHorse1.setNeutered(true);
                player.sendMessage(ChatColor.GREEN + "You have successfully neutered the horse!");
            } else {
                player.sendMessage(ChatColor.RED + "This horse has already been neutered.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You cannot neuter a Female horse.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void interactUndeadHorse(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Horse)) return;
        Player player = event.getPlayer();
        if (player.getItemInHand() == null) return;
        Horse horse = (Horse) event.getRightClicked();
        if (horse.getVariant() != Horse.Variant.UNDEAD_HORSE && horse.getVariant() != Horse.Variant.SKELETON_HORSE) return;
        final float pitch = player.getLocation().getPitch();
        final float yaw = player.getLocation().getYaw();
        if (player.getItemInHand().getType() == Material.LEASH) {
            event.setCancelled(true);
            player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), yaw, pitch), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if (!horse.isLeashed()) {
                horse.setLeashHolder(player);
            } else {
                if (horse.getLeashHolder().equals(player)) {
                    horse.setLeashHolder(null);
                }
            }
        } else if (player.getItemInHand().getType() == Material.SADDLE) {
            if (horse.getOwner() == null || horse.getOwner().equals(player)) {
                event.setCancelled(true);
                horse.setDomestication(horse.getMaxDomestication());
                horse.setTamed(true);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.setOwner(player);
                player.getItemInHand().setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void waterHorse(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Horse)) return;
        Player player = (Player) event.getDamager();
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.WATER_BUCKET) return;
        Horse horse = (Horse) event.getEntity();
        if (!horse.isTamed()) {
            event.setCancelled(true);
            event.setDamage(0);
            player.sendMessage(ChatColor.RED + "You cannot water wild horses!");
            return;
        }
        event.setDamage(0);
        event.setCancelled(true);
        AdvancedHorse advancedHorse1 = (AdvancedHorse) ((CraftLivingEntity) horse).getHandle();
        if (advancedHorse1.getHydrationLevel() < 500) {
            player.sendMessage(ChatColor.GREEN + "You have sated this Horses thirst!");
            advancedHorse1.waterHorse();
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.setItemInHand(new ItemStack(Material.BUCKET, 1));
            }
        } else {
            player.sendMessage(ChatColor.RED + "This horse is not thirsty!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void feedHorse(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Horse)) return;
        Player player = (Player) event.getDamager();
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.WHEAT && player.getItemInHand().getType() != Material.APPLE && player.getItemInHand().getType() != Material.HAY_BLOCK && player.getItemInHand().getType() != Material.SUGAR) return;
        Horse horse = (Horse) event.getEntity();
        if (!horse.isTamed()) {
            event.setCancelled(true);
            event.setDamage(0);
            player.sendMessage(ChatColor.RED + "You cannot feed wild horses!");
            return;
        }
        event.setDamage(0);
        event.setCancelled(true);
        AdvancedHorse advancedHorse1 = (AdvancedHorse) ((CraftLivingEntity) horse).getHandle();
        if (advancedHorse1.getHungerLevel() < 500) {
            player.sendMessage(ChatColor.GREEN + "You have sated this Horses thirst!");
            advancedHorse1.feedHorse();
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (player.getItemInHand().getAmount() > 1) {
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR));
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This horse is not hungry!");
        }

    }
}
