package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmashEvent extends BaseEvent {

    List<Material> allowedBlocks = new ArrayList<Material>();

    public SmashEvent(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".blocks", Arrays.asList("OBSIDIAN", "ENDER_STONE"));

        for (String mat : cfg.getStringList(name + ".blocks")) {
            Material material = Material.matchMaterial(mat);
            if (material != null) {
                allowedBlocks.add(material);
            }
        }
    }
    
    @EventHandler
    public void instaBreak(BlockDamageEvent event) {
        Block block = event.getBlock();
        if (!allowedBlocks.contains(block.getType())) {
            return;
        }
        
        //Prevent spam clicking blocks to break them (only check first click)
        if (block.hasMetadata("smashed")) {
            return;
        }
        block.setMetadata("smashed", new FixedMetadataValue(luck, true));

        Player player = event.getPlayer();
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.8f, 0.8f, 0.8f, 0f, 50, block.getLocation().add(0.5f, 0f, 0.5f));
        ParticleEffect.CRIT.display(0.8f, 0.8f, 0.8f, 0f, 10, block.getLocation().add(0.5f, 0f, 0.5f));
        block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, Util.randomFloat(0.2f, 0.6f), Util.randomFloat(0, 0.5f));

        event.setInstaBreak(true);
    }

}
