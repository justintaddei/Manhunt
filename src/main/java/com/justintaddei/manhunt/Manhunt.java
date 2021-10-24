package com.justintaddei.manhunt;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Manhunt extends JavaPlugin {

    private ConsoleCommandSender sender = getServer().getConsoleSender();

    @Override
    public void onEnable() {
        sender.sendMessage("Manhunt: enabled");
    }

    @Override
    public void onDisable() {
        sender.sendMessage("Manhunt: disabled");
    }
}
