package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.item.EItem;
import com.jroossien.luck.util.item.ItemParser;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class FortuneEvent extends BaseEvent {

    private Map<Material, List<EItem>> lootMap = new HashMap<Material, List<EItem>>();

    public FortuneEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("OBSIDIAN", Arrays.asList("obsidian", "obsidian 2"));
        map.put("DIAMOND_ORE", Arrays.asList("diamond", "diamond 2"));
        map.put("GLASS", Arrays.asList("glass"));
        map.put("MELON_BLOCK", Arrays.asList("glisteringmelon"));
        map.put("QUARTZ_ORE", Arrays.asList("quartz 3", "quartz 4", "quartz 5"));
        cfg.addDefault(name + ".loot", map);

        lootMap.clear();
        for (Material material : Material.values()) {
            if (!cfg.contains(name + ".loot." + material.toString()) || !cfg.isList(name + ".loot." + material.toString())) {
                continue;
            }

            List<EItem> items = new ArrayList<EItem>();
            for (String item : cfg.getStringList(name + ".loot." + material.toString())) {
                ItemParser parser = new ItemParser(item, true);
                if (parser.getItem() != null) {
                    items.add(parser.getItem());
                } else {
                    luck.log("Invalid item specified for the Fortune event. Material:" + material.toString() + " Input:" + item);
                    luck.log("Error: " + parser.getError());
                }
            }

            if (items.isEmpty()) {
                continue;
            }
            lootMap.put(material, items);
        }
    }

    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!lootMap.containsKey(block.getType())) {
            return;
        }
        Player player = event.getPlayer();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        ParticleEffect.VILLAGER_HAPPY.display(0.5f, 0.5f, 0.5f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));

        EItem item = Random.Item(lootMap.get(block.getType()));
        block.getWorld().dropItemNaturally(block.getLocation(), item);
        sendMessage(player);
    }

}
