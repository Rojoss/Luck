package com.jroossien.luck.config.messages;

import com.jroossien.luck.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public enum Msg {
    IN_DEVELOPMENT(Cat.GENERAL, "&cThis plugin is still in development and currently it doesn't do anything!"),
    NO_PERMISSION(Cat.GENERAL, "&cInsufficient permissions."),
    PLAYER_COMMAND(Cat.GENERAL, "&cThis is a player command only."),
    INVALID_USAGE(Cat.GENERAL, "&cInvalid usage! &7{usage}"),

    RELOADED(Cat.COMMAND, "&6All configurations reloaded!"),

    ;


    private Cat cat;
    private String msg;

    Msg(Cat cat, String msg) {
        this.cat = cat;
        this.msg = msg;
    }


    public String getDefault() {
        return msg;
    }

    public String getName() {
        return toString().toLowerCase().replace("_", "-");
    }

    public String getCategory() {
        return cat.toString().toLowerCase().replace("_", "-");
    }

    public String getMsg() {
        return MessageCfg.inst().getMessage(getCategory(), getName());
    }

    public String getMsg(Param... params) {
        return getMsg(false, false, params);
    }

    public String getMsg(boolean prefix, boolean color, Param... params) {
        String message = (prefix ? MessageCfg.inst().prefix : "") + getMsg();
        for (Param p : params) {
            message = message.replace(p.getParam(), p.toString());
        }
        if (color) {
            message = Util.color(message);
        }
        return message;
    }


    public void broadcast(Param... params) {
        broadcast(true, true, params);
    }

    public void broadcast(boolean prefix, boolean color, Param... params) {
        Bukkit.broadcastMessage(getMsg(prefix, color, params));
    }

    public void send(CommandSender sender, Param... params) {
        send(sender, true, true, params);
    }

    public void send(CommandSender sender, boolean prefix, boolean color, Param... params) {
        if (sender != null) {
            sender.sendMessage(getMsg(prefix, color, params));
        }
    }


    private enum Cat {
        GENERAL,
        COMMAND,
        ;
    }
}
