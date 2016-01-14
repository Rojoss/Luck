package com.jroossien.luck.util;

import org.bukkit.command.CommandSender;

public class Util {
    /**
     * Checks if the sender has the specified permission.
     * It will check recursively at starting at the bottom component of the permission.
     * So if the input permission is 'cmdsigns.signs.create.cmd' the player needs to have any of the following permissions.
     * cmdsigns.*
     * cmdsigns.signs.*
     * cmdsigns.signs.create.*
     * cmdsigns.signs.create.cmd
     * @param sender The sender to do the permission check on. (Remember that Player is a CommandSender!)
     * @param permission The permission to check. This should be the FULL permission string not a sub permission.
     * @return true if the sender has any of the permissions and false if not.
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        permission = permission.toLowerCase().trim();
        if (sender.hasPermission(permission)) {
            return true;
        }
        String[] components = permission.split("\\.");
        String perm = "";
        for (String component : components) {
            perm += component + ".";
            if (sender.hasPermission(perm + "*")) {
                return true;
            }
        }
        return false;
    }

    public static double lerp(double start, double end, double perc) {
        if (Double.isNaN(perc) || perc > 1) {
            return end;
        } else if (perc < 0) {
            return start;
        } else {
            return start * (1 - perc) + end * perc;
        }
    }
}
