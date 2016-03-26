package com.cherryio.listeners;

import com.cherryio.AdvancedHorses;
import com.cherryio.utils.Config;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Random;

/**
 * Created by Kieran on 24-Mar-16.
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
        EntityLiving nmsHorse = ((CraftLivingEntity) horse).getHandle();
        if (horse.hasMetadata("genderVal")) {
            player.sendMessage(ChatColor.YELLOW + "Gender" + ChatColor.GRAY + ": " + ChatColor.AQUA + horse.getMetadata("genderVal").get(0).asString());
        } else {
            player.sendMessage(ChatColor.YELLOW + "Gender" + ChatColor.GRAY + ": " + ChatColor.AQUA + "N/A");
        }
        if (horse.hasMetadata("neutered")) {
            player.sendMessage(ChatColor.YELLOW + "Neutered" + ChatColor.GRAY + ": " + ChatColor.AQUA + "Yes");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Neutered" + ChatColor.GRAY + ": " + ChatColor.AQUA + "No");
        }
        double jump = horse.getJumpStrength();
        double blockJumpHeight = -0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367;
        blockJumpHeight = (blockJumpHeight*100);
        blockJumpHeight = Math.round(blockJumpHeight);
        blockJumpHeight /= 100;
        double speed = nmsHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
        double blockPerSecondSpeed = 4.3 * speed * 10;
        blockPerSecondSpeed = (blockPerSecondSpeed*100);
        blockPerSecondSpeed = Math.round(blockPerSecondSpeed);
        blockPerSecondSpeed /= 100;
        player.sendMessage(ChatColor.YELLOW + "Jump Height (Blocks)" + ChatColor.GRAY + ": " + ChatColor.AQUA + blockJumpHeight);
        player.sendMessage(ChatColor.YELLOW + "Speed (Blocks/Second)" + ChatColor.GRAY + ": " + ChatColor.AQUA + blockPerSecondSpeed);
        player.sendMessage(ChatColor.YELLOW + "Health" + ChatColor.GRAY + ": " + ChatColor.AQUA + Math.round(horse.getHealth()) + ChatColor.GRAY + "/" + ChatColor.AQUA + Math.round(horse.getMaxHealth()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void horseDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.HORSE) return;
        Horse deadHorse = (Horse) event.getEntity();
        if (deadHorse.getVariant() == Horse.Variant.UNDEAD_HORSE || deadHorse.getVariant() == Horse.Variant.SKELETON_HORSE) return;
        if (new Config<Boolean>("settings.undeadHorseAfterDeath").getValue()) {
            if (new Random().nextInt(99) < new Config<Integer>("settings.undeadHorseAfterDeathChance").getValue()) {
                final Location location = event.getEntity().getLocation();
                Horse horse = (Horse) event.getEntity().getWorld().spawnEntity(location, EntityType.HORSE);
                if (new Random().nextBoolean()) {
                    horse.setVariant(Horse.Variant.UNDEAD_HORSE);
                } else {
                    horse.setVariant(Horse.Variant.SKELETON_HORSE);
                }
                if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
                    horse.setMetadata("genderVal", new FixedMetadataValue(AdvancedHorses.getInstance(), "Male"));
                } else {
                    horse.setMetadata("genderVal", new FixedMetadataValue(AdvancedHorses.getInstance(), "Female"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void horseSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.HORSE) return;
        if (new Random().nextInt(99) < new Config<Integer>("settings.maleHorseOnBirthChance").getValue()) {
            event.getEntity().setMetadata("genderVal", new FixedMetadataValue(AdvancedHorses.getInstance(), "Male"));
        } else {
            event.getEntity().setMetadata("genderVal", new FixedMetadataValue(AdvancedHorses.getInstance(), "Female"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void horseMate(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) return;
        if (event.getEntity().getType() != EntityType.HORSE) return;
        Horse babyHorse = (Horse) event.getEntity();
        boolean found = false;
        boolean found2 = false;
        Horse parent1 = babyHorse;
        Horse parent2 = babyHorse;
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
        if (found && found2) {
            if (parent1.hasMetadata("neutered") || parent2.hasMetadata("neutered")) {
                babyHorse.remove();
                return;
            }
            if (parent1.hasMetadata("genderVal") && parent2.hasMetadata("genderVal")) {
                if (parent1.getMetadata("genderVal").get(0).asString().equalsIgnoreCase("male")) {
                    if (parent2.getMetadata("genderVal").get(0).asString().equalsIgnoreCase("male")) {
                        babyHorse.remove();
                    }
                } else if (parent1.getMetadata("genderVal").get(0).asString().equalsIgnoreCase("female")) {
                    if (parent2.getMetadata("genderVal").get(0).asString().equalsIgnoreCase("female")) {
                        babyHorse.remove();
                    }
                }
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
        event.setDamage(0);
        event.setCancelled(true);
        if (horse.hasMetadata("genderVal")) {
            if (horse.getMetadata("genderVal").get(0).asString().equalsIgnoreCase("male")) {
                if (!(horse.hasMetadata("neutered"))) {
                    horse.setMetadata("neutered", new FixedMetadataValue(AdvancedHorses.getInstance(), "neutered"));
                    player.sendMessage(ChatColor.GREEN + "You have successfully neutered the horse!");
                } else {
                    player.sendMessage(ChatColor.RED + "This horse has already been neutered.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot neuter a Female horse.");
            }
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
}
