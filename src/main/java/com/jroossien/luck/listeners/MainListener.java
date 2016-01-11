package com.jroossien.luck.listeners;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.util.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainListener implements Listener {

    private Luck lu;

    public MainListener(Luck lu) {
        this.lu = lu;
    }

    @EventHandler
    private void onCraft(PrepareItemCraftEvent event){
        //Cancel crafting with gems.
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack item : items) {
            if (item == null || item.getType() != Material.EMERALD || !item.hasItemMeta()) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasLore() || !meta.hasDisplayName() || !Util.removeColor(meta.getDisplayName()).equalsIgnoreCase(Msg.ITEM_NAME.getMsg())) {
                continue;
            }
            event.getInventory().setResult(null);
            return;
        }
    }

    @EventHandler
    private void onTradeClick(InventoryClickEvent event) {
        //Cancel trading gems with villagers
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }
        if (event.getSlot() > 2) {
            return;
        }
        ItemStack item = event.getCursor();
        if (item == null || item.getType() != Material.EMERALD || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore() || !meta.hasDisplayName() || !Util.removeColor(meta.getDisplayName()).equalsIgnoreCase(Msg.ITEM_NAME.getMsg())) {
            return;
        }
        event.setCancelled(true);
        return;
    }

    @EventHandler
    private void onTradeDrag(InventoryDragEvent event) {
        //Cancel trading gems with villagers
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }
        boolean check = false;
        for (int slot : event.getInventorySlots()) {
            if (slot < 3) {
                check = true;
            }
        }
        if (!check) {
            return;
        }
        ItemStack item = event.getOldCursor();
        if (item == null || item.getType() != Material.EMERALD || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore() || !meta.hasDisplayName() || !Util.removeColor(meta.getDisplayName()).equalsIgnoreCase(Msg.ITEM_NAME.getMsg())) {
            return;
        }
        event.setCancelled(true);
    }


}
