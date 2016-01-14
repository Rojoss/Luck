package com.jroossien.luck.commands;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.config.messages.Param;
import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Str;
import com.jroossien.luck.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    private Luck lu;

    public Commands(Luck lu) {
        this.lu = lu;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (label.equalsIgnoreCase("luck") || label.equalsIgnoreCase("lucky") || label.equalsIgnoreCase("lu") || label.equalsIgnoreCase("l")) {

            //Display user info
            if (args.length < 1 || args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("stats")) {
                if (!Util.hasPermission(sender, "luck.cmd.luck")) {
                    Msg.NO_PERMISSION.send(sender);
                    return true;
                }

                if (!(sender instanceof Player)) {
                    showHelp(sender);
                    return true;
                }

                Player player = (Player)sender;
                int gems = lu.getGM().getGems(player, true);
                double percentage = lu.getGM().getPercentage(player);

                Msg.LUCK.send(player, false, true, Param.P("{gems}", gems), Param.P("{percentage}", percentage * 100 + "%"));
                return true;
            }


            //Help/Command list
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("cmds") || args[0].equalsIgnoreCase("commands")) {
                showHelp(sender);
                return true;
            }


            //Reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (!Util.hasPermission(sender, "luck.cmd.reload")) {
                    Msg.NO_PERMISSION.send(sender);
                    return true;
                }

                lu.getCfg().load();
                lu.getMsgCfg().load();
                lu.getEM().load();
                lu.getEM().registerEvents();

                Msg.RELOADED.send(sender);
                return true;
            }


            //Info
            if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("plugin") || args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(Str.color("&8===== &4&lLuck Plugin &8=====\n" +
                        "&6&lAuthor&8&l: &aWorstboy(Jos)\n" +
                        "&6&lVersion&8&l: &a" + lu.getDescription().getVersion() + "\n" +
                        "&6&lSpigot URL&8&l: &9https://www.spigotmc.org/resources/luck.16930/"));
                return true;
            }


            //Events
            if (args[0].equalsIgnoreCase("events") || args[0].equalsIgnoreCase("eventlist")) {
                if (!Util.hasPermission(sender, "luck.cmd.events")) {
                    Msg.NO_PERMISSION.send(sender);
                    return true;
                }

                List<String> names = new ArrayList<String>();
                for (BaseEvent event : lu.getEM().getEvents().values()) {
                    names.add(event.getDisplayName());
                }

                Msg.EVENTS_LIST.send(sender, Param.P("{events}", Str.implode(names, ", ", " & ")));
                return true;
            }


            //Event info
            if (args[0].equalsIgnoreCase("event") || args[0].equalsIgnoreCase("e")) {
                if (!Util.hasPermission(sender, "luck.cmd.event")) {
                    Msg.NO_PERMISSION.send(sender);
                    return true;
                }

                if (args.length < 2) {
                    Msg.INVALID_USAGE.send(sender, Param.P("{usage}", "/" + label + " " + args[0] + " {event-name}"));
                    return true;
                }

                if (!lu.getEM().getEvents().containsKey(args[1].toLowerCase())) {
                    Msg.UNKNOWN_EVENT.send(sender);
                    return true;
                }
                BaseEvent event = lu.getEM().getEvents().get(args[1].toLowerCase());

                Msg.EVENT_INFO.send(sender, false, true, Param.P("{name}", event.getName()), Param.P("{display-name}", event.getDisplayName()), Param.P("{description}", event.getDescription()),
                        Param.P("{min-chance}", event.getMinChance() + "%"), Param.P("{max-chance}", event.getMaxChance() + "%"));
                if (sender instanceof Player) {
                    Msg.EVENT_INFO_YOUR_CHANCE.send(sender, false, true, Param.P("{chance}", (event.getChance(lu.getGM().getPercentage((Player)sender)) * 100) + "%"));
                }
                //TODO: Display any extra config data.
                return true;
            }

            /*
            //Buy
            if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("purchase")) {
                if (!Util.hasPermission(sender, "luck.cmd.buy")) {
                    Msg.NO_PERMISSION.send(sender);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Msg.PLAYER_COMMAND.send(sender);
                    return true;
                }
                //TODO: Purchase luck
                return true;
            }
            */

            showHelp(sender);
            return true;
        }
        return false;
    }

    private void showHelp(CommandSender sender) {
        Msg.HELP.send(sender, false, true, Param.P("{price}", lu.getCfg().gem_purchase_price));
    }
}