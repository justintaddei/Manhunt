package com.justintaddei.manhunt;

import java.util.HashSet;

import com.justintaddei.manhunt.commands.ManhuntCommand;
import com.justintaddei.manhunt.events.PlayerEvents;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public final class Manhunt extends JavaPlugin {

    public static HashSet<Player> speedrunners = new HashSet<Player>();
    public static HashSet<Player> deadSpeedrunners = new HashSet<Player>();

    public static boolean isSpeedrunner(Player player) {
        return Manhunt.speedrunners.contains(player) || Manhunt.deadSpeedrunners.contains(player);
    }

    public static Player getNearestSpeedrunner(Player player) {
        if (Manhunt.speedrunners.contains(player))
            return null;

        Player nearestSpeedrunner = null;
        double minDistance = 0.0;

        for (Player speedrunner : Manhunt.speedrunners) {
            if (speedrunner.getWorld() != player.getWorld())
                continue;

            double distance = player.getLocation().distanceSquared(speedrunner.getLocation());
            if (distance > minDistance) {
                minDistance = distance;
                nearestSpeedrunner = speedrunner;
            }
        }

        return nearestSpeedrunner;

    }

    public void broadcast(String msg) {
        this.getServer().getOnlinePlayers().forEach((p) -> p.sendMessage(msg));
    }

    public void giveCompass(Player p) {
        p.getInventory().addItem(new ItemStack(Material.COMPASS));
    }

    public void gameOver() {
        this.broadcast(ChatColor.RED + "The hunters have won this round");
    }

    public void startGame() {
        this.getServer().getOnlinePlayers().forEach((p) -> {
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.setHealth(20);
            p.setFoodLevel(20);
            if (!Manhunt.speedrunners.contains(p)) {
                this.giveCompass(p);
            }
        });
        this.broadcast(ChatColor.GOLD + "Manhunt is starting");

        if (Manhunt.speedrunners.size() > 1)
            this.broadcast(ChatColor.AQUA + "The speedrunners for this round are:");
        else
            this.broadcast(ChatColor.AQUA + "The speedrunner for this round is:");

        Manhunt.speedrunners.forEach((p) -> this.broadcast(ChatColor.AQUA + "• " + p.getName()));

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        getCommand("manhunt").setExecutor(new ManhuntCommand(this));
    }

    @Override
    public void onDisable() {
    }
}
