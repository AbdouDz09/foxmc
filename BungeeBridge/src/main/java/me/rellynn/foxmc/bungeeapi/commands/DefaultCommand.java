package me.rellynn.foxmc.bungeeapi.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 18/04/2017.
 * FoxMC Network.
 */
public abstract class DefaultCommand extends Command implements TabExecutor {

    protected DefaultCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return new ArrayList<>();
        }
        return ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())).map(CommandSender::getName).collect(Collectors.toSet());
    }
}
