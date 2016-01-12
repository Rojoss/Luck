package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class LuckyEvent extends BaseEvent {

    public LuckyEvent(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.EMERALD_ORE) {
            return;
        }
        Player player = event.getPlayer();
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        block.getWorld().dropItemNaturally(block.getLocation(), luck.getGem());

        ParticleEffect.VILLAGER_HAPPY.display(0.5f, 0.5f, 0.5f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));
        block.getWorld().playSound(block.getLocation(), Sound.LEVEL_UP, 0.3f, 2);
    }

}
