package com.justintaddei.manhunt.commands;

import com.justintaddei.manhunt.Manhunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class ManhuntCommand implements CommandExecutor {

    private Manhunt plugin;

    public ManhuntCommand(Manhunt plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0)
                return false;

            Player selectedPlayer = null;

            switch (args[0]) {
            case "add":

                if (args.length < 2)
                    selectedPlayer = player;
                else
                    selectedPlayer = this.plugin.getServer().getPlayer(args[1]);

                if (selectedPlayer == null)
                    return false;

                if (Manhunt.deadSpeedrunners.contains(selectedPlayer))
                    Manhunt.deadSpeedrunners.remove(selectedPlayer);

                if (!Manhunt.isSpeedrunner(selectedPlayer))
                    Manhunt.speedrunners.add(selectedPlayer);

                if (selectedPlayer == player)
                    selectedPlayer.sendMessage(ChatColor.GREEN + "You are now a speedrunner!");
                else {
                    player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.AQUA + selectedPlayer.getName()
                            + ChatColor.GREEN + " as a speedrunner!");
                    selectedPlayer.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN
                            + " added you to the speedrunner list!");
                }

                break;
            case "remove":
                if (args.length < 2)
                    selectedPlayer = player;
                else
                    selectedPlayer = this.plugin.getServer().getPlayer(args[1]);

                if (selectedPlayer == null)
                    return false;

                if (Manhunt.speedrunners.contains(selectedPlayer))
                    Manhunt.speedrunners.remove(selectedPlayer);

                if (Manhunt.deadSpeedrunners.contains(selectedPlayer))
                    Manhunt.deadSpeedrunners.remove(selectedPlayer);

                if (selectedPlayer == player)
                    player.sendMessage(ChatColor.RED + "You are no longer a speedrunner");
                else {
                    player.sendMessage(ChatColor.RED + "Removed " + ChatColor.YELLOW + selectedPlayer.getName()
                            + ChatColor.RED + " from the speedrunner list");
                    selectedPlayer.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED
                            + " removed you from the speedrunner list");
                }
                break;
            case "list":
                player.sendMessage(ChatColor.AQUA + "Speedrunners:");
                Manhunt.speedrunners.forEach((p) -> player.sendMessage(ChatColor.GREEN + p.getName()));
                break;
            case "start":
                this.plugin.startGame();
                break;
            }
        }
        return true;
    }
}
