package me.rellynn.foxmc.bungeeapi;

import me.rellynn.foxmc.bungeeapi.commands.HelpCommand;
import me.rellynn.foxmc.bungeeapi.commands.SetRankCommand;
import me.rellynn.foxmc.bungeeapi.commands.TSCommand;
import me.rellynn.foxmc.bungeeapi.hubs.HubCommand;
import me.rellynn.foxmc.bungeeapi.listeners.ChatListener;
import me.rellynn.foxmc.bungeeapi.listeners.PlayerListener;
import me.rellynn.foxmc.bungeeapi.listeners.ServerListener;
import me.rellynn.foxmc.bungeeapi.listeners.VoteListener;
import me.rellynn.foxmc.bungeeapi.moderation.commands.BanIPCommand;
import me.rellynn.foxmc.bungeeapi.moderation.commands.ReportCommand;
import net.md_5.bungee.api.ProxyServer;

/**
 * Created by gwennaelguich on 21/04/2017.
 * FoxMC Network.
 */
abstract class PluginRegistry {

    static void registerEverything() {
        registerListeners();
        registerCommands();
    }

    private static void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(BridgePlugin.get(), new ChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(BridgePlugin.get(), new PlayerListener());
        ProxyServer.getInstance().getPluginManager().registerListener(BridgePlugin.get(), new ServerListener());
        if (ProxyServer.getInstance().getPluginManager().getPlugin("NuVotifier") != null) {
            // Hook only if the plugin is on the proxy
            ProxyServer.getInstance().getPluginManager().registerListener(BridgePlugin.get(), new VoteListener());
        }
    }

    private static void registerCommands() {
        registerAdminCommands();
        registerRegularCommands();
    }

    private static void registerAdminCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new SetRankCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new BanIPCommand());
    }

    private static void registerRegularCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new HelpCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new HubCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new ReportCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new TSCommand());
    }
}
