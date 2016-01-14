package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HasteEvent extends BaseEvent {

    List<Material> allowedBlocks = new ArrayList<Material>();

    public HasteEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".blocks", Arrays.asList("OBSIDIAN", "ENDER_STONE", "SAND", "DIRT", "GRAVEL", "STONE", "NETHERRACK", "SOUL_SAND"));
        cfg.addDefault(name + ".amplifier", 5);
        cfg.addDefault(name + ".durationTicks", 100);
        cfg.addDefault(name + ".stackEffects", false);

        allowedBlocks.clear();
        for (String mat : cfg.getStringList(name + ".blocks")) {
            Material material = Material.matchMaterial(mat);
            if (material != null) {
                allowedBlocks.add(material);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void haste(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!allowedBlocks.contains(block.getType())) {
            return;
        }

        Player player = event.getPlayer();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING) && !cfg.getBoolean(name + ".stackEffects")) {
            return;
        }

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        ParticleEffect.SPELL_WITCH.display(0.2f, 0.3f, 0.2f, 0f, 20, block.getLocation().add(0.5f, 0f, 0.5f));
        block.getWorld().playSound(block.getLocation(), Sound.WITHER_SHOOT, Random.Float(0.2f, 0.5f), 0f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, cfg.getInt(name + ".durationTicks"), cfg.getInt(name + ".amplifier"), true), true);
        sendMessage(player);
    }

}
