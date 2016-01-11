package com.jroossien.luck.luck;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GemManager {

    private Luck lu;
    private Map<UUID, GemData> playerGems = new HashMap<UUID, GemData>();

    public GemManager(Luck lu) {
        this.lu = lu;

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateGems(player, true);
        }
    }


    public int getGems(Player player) {
        return getGems(player, false);
    }

    public int getGems(Player player, boolean force) {
        if (player == null) {
            return 0;
        }
        updateGems(player, force);
        return playerGems.get(player.getUniqueId()).getGems();
    }


    public double getPercentage(Player player) {
        return getPercentage(player, false);
    }

    public double getPercentage(Player player, boolean force) {
        if (player == null) {
            return 0;
        }
        updateGems(player, force);
        return playerGems.get(player.getUniqueId()).getPercentage();
    }


    private void updateGems(Player player, boolean force) {
        UUID uuid = player.getUniqueId();

        //Create new gem data if player has none.
        if (!playerGems.containsKey(uuid)) {
            GemData data = new GemData(uuid);
            data.setGems(getGemCount(player));
            playerGems.put(uuid, data);
        }

        //Load gem data and update if needed. (at least 10 seconds delay between checking gem counts to improve performance)
        GemData data = playerGems.get(uuid);
        if (force || data.getLastUpdate() + 10000 < System.currentTimeMillis()) {
            data.setGems(getGemCount(player));
            playerGems.put(uuid, data);
        }
    }


    private int getGemCount(Player player) {
        if (player == null) {
            return 0;
        }

        int count = 0;
        ItemMeta meta;
        String gemName = Msg.ITEM_NAME.getMsg();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.EMERALD || !item.hasItemMeta()) {
                continue;
            }
            meta = item.getItemMeta();
            if (!meta.hasLore() || !meta.hasDisplayName() || !Util.removeColor(meta.getDisplayName()).equalsIgnoreCase(gemName)) {
                continue;
            }
            count += item.getAmount();
        }

        return count;
    }
}
