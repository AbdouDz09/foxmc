package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by gwennaelguich on 16/05/2017.
 * FoxMC Network.
 */
public class ModCommand extends ModerationCommand {

    public ModCommand() {
        super("mod", Rank.HELPER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/mod <message>"));
            return;
        }
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mod ");
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eSend a new message"));
        BaseComponent[] components = TextComponent.fromLegacyText(ModerationCommand.PREFIX + "§e" + sender.getName() + " §7» §r" + StringUtils.join(args, " "));
        for (BaseComponent component : components) {
            if (component.getClickEvent() == null) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }
        }
        sendModMessage(components);
    }
}
