package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.item.EItem;
import com.jroossien.luck.util.item.ItemParser;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;

public class LootEvent extends BaseEvent {

    private Map<EntityType, List<EItem>> lootMap = new HashMap<EntityType, List<EItem>>();

    public LootEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("HORSE", Arrays.asList("saddle", "leash", "ironbarding", "goldbarding", "diamondbarding"));
        map.put("WITCH", Arrays.asList("blazerod"));
        map.put("CREEPER", Arrays.asList("sulphur 4", "sulphur 3", "sulphur 2", "sulphur"));
        map.put("ENDERMAN", Arrays.asList("enderpearl", "enderpearl 2"));
        map.put("BUNNY", Arrays.asList("goldencarrot"));
        map.put("BAT", Arrays.asList("leather"));
        map.put("PIG", Arrays.asList("leash"));
        map.put("PIG_ZOMBIE", Arrays.asList("goldingot"));
        map.put("ENDERMITE", Arrays.asList("enderpearl"));
        map.put("SPIDER", Arrays.asList("string", "string 2"));
        map.put("RABBIT", Arrays.asList("goldencarrot"));
        map.put("VILLAGER", Arrays.asList("emerald 2", "emerald 3", "emerald 4", "emerald 5"));
        map.put("BLAZE", Arrays.asList("blazerod"));
        map.put("IRON_GOLEM", Arrays.asList("ironingot 3", "ironingot 4", "ironingot 5", "pumpkin"));
        map.put("SNOWMAN", Arrays.asList("pumpkin"));
        map.put("WITHER", Arrays.asList("397:1"));
        cfg.addDefault(name + ".loot", map);

        for (EntityType entityType : EntityType.values()) {
            if (!cfg.contains(name + ".loot." + entityType.toString()) || !cfg.isList(name + ".loot." + entityType.toString())) {
                continue;
            }

            List<EItem> items = new ArrayList<EItem>();
            for (String item : cfg.getStringList(name + ".loot." + entityType.toString())) {
                ItemParser parser = new ItemParser(item, true);
                if (parser.getItem() != null) {
                    items.add(parser.getItem());
                } else {
                    luck.log("Invalid item specified for the Loot event. Entity:" + entityType.toString() + " Input:" + item);
                    luck.log("Error: " + parser.getError());
                }
            }

            if (items.isEmpty()) {
                continue;
            }
            lootMap.put(entityType, items);
        }
    }

    @EventHandler
    private void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() == null) {
            return;
        }
        if (!lootMap.containsKey(event.getEntityType())) {
            return;
        }
        Player player = entity.getKiller();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        ParticleEffect.VILLAGER_HAPPY.display(0.5f, 1, 0.5f, 0, 10, entity.getLocation());

        EItem item = Util.random(lootMap.get(event.getEntityType()));
        entity.getWorld().dropItemNaturally(entity.getLocation(), item);
        sendMessage(player);
    }

}
