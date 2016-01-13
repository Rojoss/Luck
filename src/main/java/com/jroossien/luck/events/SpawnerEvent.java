package com.jroossien.luck.events;

import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.config.messages.Param;
import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.item.EItem;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnerEvent extends BaseEvent {

    List<EntityType> allowedEntities = new ArrayList<EntityType>();

    public SpawnerEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".entityTypes", Arrays.asList("ZOMBIE", "SKELETON", "SPIDER", "CAVE_SPIDER", "SILVERFISH", "BLAZE"));

        allowedEntities.clear();
        for (String entity : cfg.getStringList(name + ".entityTypes")) {
            EntityType entityType = EntityType.fromName(entity);
            if (entityType != null) {
                allowedEntities.add(entityType);
            }
        }
    }
    
    @EventHandler
    public void breakSpawner(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (!(block.getState() instanceof CreatureSpawner)) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        if (!allowedEntities.contains(spawner.getSpawnedType())) {
            return;
        }

        Player player = event.getPlayer();
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        block.getWorld().playSound(block.getLocation(), Sound.LEVEL_UP, 50f, 2);
        ParticleEffect.VILLAGER_HAPPY.display(3f, 0.2f, 3f, 0f, 200, block.getLocation().add(0.5f, 0f, 0.5f));
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run () {
                if (ticks > 60) {
                    return;
                }
                if (ticks % 2 == 0) {
                    block.getWorld().playSound(block.getLocation(), Sound.ORB_PICKUP, Util.randomFloat(0.6f, 1.2f), Util.randomFloat(1, 2));
                }
                ParticleEffect.VILLAGER_HAPPY.display(0.8f, 0.8f, 0.8f, 0f, 5, block.getLocation().add(0.5f, 0f, 0.5f));
                ParticleEffect.FLAME.display(0.8f, 0.8f, 0.8f, 0f, 1, block.getLocation().add(0.5f, 0f, 0.5f));
                ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.EMERALD, (byte)0), 0.8f, 0.8f, 0.8f, 0.1f, 10, block.getLocation().add(0.5f, 0f, 0.5f));
                ticks++;
            }
        }.runTaskTimer(luck, 10, 1);

        sendMessage(player);
        EItem spawnerItem = new EItem(Material.MOB_SPAWNER).setName(Msg.SPAWNER_NAME_PREFIX.getMsg(false, false, Param.P("{entity}", spawner.getSpawnedType().toString().toLowerCase().replace("_", "")))
                + Msg.SPAWNER_NAME_SUFFIX.getMsg()).setLore("&e&o" + spawner.getSpawnedType().toString().toLowerCase().replace("_", ""));
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5f, 0.5f, 0.5f), spawnerItem);
    }


    @EventHandler
    public void Place(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        ItemStack item = player.getItemInHand();
        if (item.getType() != Material.MOB_SPAWNER) {
            return;
        }
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && Util.removeColor(meta.getDisplayName()).endsWith(Msg.SPAWNER_NAME_SUFFIX.getMsg())) {
                List<String> lore = meta.getLore();
                if (lore.size() > 0) {
                    BlockState state = event.getBlockPlaced().getState();
                    if (state instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        spawner.setCreatureTypeByName(Util.stripAllColor(lore.get(0)));
                    }
                }
            }
        }
    }

}
