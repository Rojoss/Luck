package com.jroossien.luck.commands;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commands {
    private Luck lu;

    public Commands(Luck lu) {
        this.lu = lu;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (label.equalsIgnoreCase("luck") || label.equalsIgnoreCase("lucky") || label.equalsIgnoreCase("lu") || label.equalsIgnoreCase("l")) {

            if (args.length < 1) {
                //TODO: Display current luck.
                Msg.IN_DEVELOPMENT.send(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                lu.getCfg().load();
                lu.getMsgCfg().load();
                lu.getEM().registerEvents();

                Msg.RELOADED.send(sender);
                return true;
            }

            //TODO: Display help.
            Msg.IN_DEVELOPMENT.send(sender);
            return true;
        }
        return false;
    }
}