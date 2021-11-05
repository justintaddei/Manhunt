package com.justintaddei.manhunt.events;

import java.util.Collection;

import com.google.common.collect.Iterables;
import com.justintaddei.manhunt.Manhunt;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

public class PlayerEvents implements Listener {

    static Manhunt plugin;

    public PlayerEvents(Manhunt plugin) {
        PlayerEvents.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            Entity currentSpectatorTarget = player.getSpectatorTarget();

            @NotNull
            Collection<? extends Player> players = player.getServer().getOnlinePlayers();

            boolean spectateNextPlayer = false;
            Player newSpectatorTarget = Iterables.getFirst(players, player);

            for (Player spectatorCandidate : players) {
                if (spectateNextPlayer || currentSpectatorTarget == null) {
                    newSpectatorTarget = spectatorCandidate;
                    break;
                }

                if (spectatorCandidate == currentSpectatorTarget)
                    spectateNextPlayer = true;
            }

            player.setSpectatorTarget(newSpectatorTarget);
            player.sendMessage(ChatColor.AQUA + "Spectating " + newSpectatorTarget.getName());

            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                ItemStack item = event.getItem();

                if (item.getType() != Material.COMPASS)
                    return;

                Player nearestPlayer = Manhunt.getNearestSpeedrunner(player);

                if (nearestPlayer == null) {
                    player.sendMessage(ChatColor.YELLOW + "Pointing to previously tracked location");
                    return;
                }

                if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    Location playerLocation = nearestPlayer.getLocation();
                    Location loadStoneLocation = new Location(player.getWorld(), playerLocation.getBlockX(), 0,
                            playerLocation.getBlockZ());

                    player.getWorld().getBlockAt(loadStoneLocation).setType(Material.LODESTONE);
                    CompassMeta meta = (CompassMeta) event.getItem().getItemMeta();
                    meta.setLodestoneTracked(true);
                    meta.setLodestone(loadStoneLocation);
                    meta.setLodestoneTracked(true);
                    event.getItem().setItemMeta(meta);
                } else {
                    player.setCompassTarget(nearestPlayer.getLocation());
                }

                player.sendMessage(ChatColor.GREEN + "Compass is pointing to " + nearestPlayer.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPlayerDropCompass(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (Manhunt.isSpeedrunner(player))
            return;

        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getType() != Material.COMPASS)
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (Manhunt.isSpeedrunner(player)) {
            Manhunt.speedrunners.remove(player);
            Manhunt.deadSpeedrunners.add(player);

            if (Manhunt.speedrunners.isEmpty())
                PlayerEvents.plugin.gameOver();
        }

        event.getDrops().forEach((drop) -> {
            if (drop.getType() == Material.COMPASS) {
                event.getDrops().remove(drop);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (Manhunt.isSpeedrunner(player))
            player.setGameMode(GameMode.SPECTATOR);
        else
            PlayerEvents.plugin.giveCompass(player);
    }
}
