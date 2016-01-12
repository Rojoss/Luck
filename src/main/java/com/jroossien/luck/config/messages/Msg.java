package com.jroossien.luck.config.messages;

import com.jroossien.luck.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public enum Msg {
    NO_PERMISSION(Cat.GENERAL, "&cInsufficient permissions."),
    PLAYER_COMMAND(Cat.GENERAL, "&cThis is a player command only."),
    INVALID_USAGE(Cat.GENERAL, "&cInvalid usage! &7{usage}"),

    NO_ITEM_SPECIFIED(Cat.ITEM_PARSER, "&cNo item specified!"),
    UNKNOWN_ITEM_NAME(Cat.ITEM_PARSER, "&cThe item &4{input} &cis not a valid item!"),
    MISSING_META_VALUE(Cat.ITEM_PARSER, "&cNo value specified for meta &4{meta}&c!"),
    NOT_A_NUMBER(Cat.ITEM_PARSER, "&4{input} &cis not a number!"),
    INVALID_COLOR(Cat.ITEM_PARSER, "&4{input} &cis not a valid color!"),
    INVALID_DYE_COLOR(Cat.ITEM_PARSER, "&4{input} &cis not a valid dye color!"),
    INVALID_FIREWORK_SHAPE(Cat.ITEM_PARSER, "&4{input} &cis not a valid firework shape!"),
    MISSING_FIREWORK_SHAPE(Cat.ITEM_PARSER, "&cTo create a firework effect, you need to specify the shape!"),
    MISSING_FIREWORK_COLOR(Cat.ITEM_PARSER, "&cTo create a firework effect, you need to set at least one color!"),
    INVALID_ENCHANT_VALUE(Cat.ITEM_PARSER, "&4{input} &cis not a valid enchantment level."),
    INVALID_POTION_VALUE(Cat.ITEM_PARSER, "&4{input} &cis not a valid potion effect value. It should be {duration}.{amplifier}&c."),

    HELP(Cat.COMMAND, "&8======== &4&l/luck help &8========\n&6/luck &8- &7Display personal luck details.\n&6/luck help &8- &7Display this page.\n&6/luck info &8- &7Display plugin information.\n" +
            "&6/luck events &8- &7List all the luck event names.\n&6/luck event {event} &8- &7Display event specific details.\n&6/luck recipe &8- &7See the lucky gem recipe.\n" +
            "&6/luck buy &8- &7Purchase lucky gems for &e&l${price}&7.\n&6/luck reload &8- &7Reload configuration files."),
    RELOADED(Cat.COMMAND, "&6All configurations reloaded!"),
    EVENTS_LIST(Cat.COMMAND, "&6&lLuck Events&8&l: &7{events}"),
    UNKNOWN_EVENT(Cat.COMMAND, "&cUnknown event specified! &7See &c/luck events &7for a list!"),
    EVENT_INFO(Cat.COMMAND, "&8======== &4&l{display-name} &8========\n&7&o{description}\n&6&lName&8&l: &a{name}\n&6&lMin chance&8&l: &a{min-chance} &6&lMax chance&8&l: &a{max-chance}"),
    EVENT_INFO_YOUR_CHANCE(Cat.COMMAND, "&6&lYour chance&8&l: &a{chance}"),
    EVENT_INFO_EXTRA_KEY(Cat.COMMAND, "&6&l"),
    EVENT_INFO_EXTRA_SEPERATOR(Cat.COMMAND, "&8&l: "),
    EVENT_INFO_EXTRA_VALUE(Cat.COMMAND, "&7"),

    ITEM_NAME(Cat.GEM, "&a&lLucky Gem"),
    ITEM_LORE(Cat.GEM, "&7Carrying this gem gives you &aluck&7!\n&7The more you have the more luck!"),

    SPAWNER_NAME_PREFIX(Cat.LUCK, "&a&l{entity}"),
    SPAWNER_NAME_SUFFIX(Cat.LUCK, " &6&lSpawner!"),
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
        ITEM_PARSER,
        COMMAND,
        GEM,
        LUCK,
        ;
    }
}
