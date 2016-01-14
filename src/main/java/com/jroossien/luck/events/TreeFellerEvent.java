package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.events.internal.TreeTypes;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeFellerEvent extends BaseEvent {

    private List<TreeTypes> allowedtypes = new ArrayList<TreeTypes>();

    public TreeFellerEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();

        cfg.addDefault(name + ".treeTypes", Arrays.asList("OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK"));
        cfg.addDefault(name + ".breakBelow", false);
        cfg.addDefault(name + ".blocksBelowAllowed", 2);
        cfg.addDefault(name + ".animateFall", true);

        allowedtypes.clear();
        for (String str : cfg.getStringList(name + ".treeTypes")) {
            allowedtypes.add(TreeTypes.valueOf(str));
        }
    }


    @EventHandler(ignoreCancelled = true)
    private void breakTree(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
            return;
        }

        if (!Util.hasPermission(event.getPlayer(), "luck.luck." + name)) {
            return;
        }
        
        if (!checkChance(gm.getPercentage(event.getPlayer()))) {
            return;
        }

        TreeTypes type = null;
        for (TreeTypes treeType : allowedtypes) {
            if (treeType.log == block.getType() && treeType.dataTypes.contains(block.getData())) {
                type = treeType;
                break;
            }
        }
        if (type == null) {
            return;
        }

        //Count the logs below the block broken and compare with config value/
        int logsBelow = 0;
        Block startBlock = block;
        while (block.getRelative(BlockFace.DOWN).getType() == type.log && type.dataTypes.contains(block.getRelative(BlockFace.DOWN).getData())) {
            logsBelow++;
            block = block.getRelative(BlockFace.DOWN);
        }
        if (logsBelow > cfg.getInt(name + ".blocksBelowAllowed")) {
            return;
        }

        //If there is air below don't fell anyways.
        if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            return;
        }

        //Start at the bottom if that's what's configured.
        if (cfg.getBoolean(name + ".breakBelow")) {
            startBlock = block;
        }

        //Make sure there are at least 3 blocks.
        int blocks = 0;
        block = startBlock;
        while (blocks < 3 && block.getType() == type.log && type.dataTypes.contains(block.getData())) {
            block = block.getRelative(BlockFace.UP);
            blocks++;
        }
        if (blocks < 3) {
            return;
        }

        ParticleEffect.CRIT.display(0.5f, 1f, 0.5f, 0.0f, 20, block.getLocation().add(0.5f, 0f, 0.5f));
        block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, Random.Float(0.1f, 0.4f), Random.Float(0.2f, 1f));
        sendMessage(event.getPlayer());

        //Break blocks. (finally <3)
        int broken = 0;
        block = startBlock;
        while (block.getType() == type.log && type.dataTypes.contains(block.getData())) {
            breakBlock(block);
            block = block.getRelative(BlockFace.UP);
            broken++;
        }
        if (broken < 3) {
            return;
        }

        //Check for branches and break them.
        for (int x = -4; x < 4; x++) {
            for (int y = -3; y < 3; y++) {
                for (int z = -4; z < 4; z++) {
                    Block b = block.getRelative(x,y,z);
                    if (b.getType() == type.log && type.dataTypes.contains(b.getData())) {
                        breakBlock(b);
                    }
                }
            }
        }
    }

    private void breakBlock(Block block) {
        if (Random.Float() < 0.2f) {
            block.getWorld().playSound(block.getLocation(), Sound.DIG_WOOD, Random.Float(0.5f, 1f), Random.Float(0.5f, 1.5f));
        }
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.6f, 0.6f, 0.6f, 0.2f, 10, block.getLocation().add(0.5f, 0f, 0.5f));
        if (cfg.getBoolean(name + ".animateFall")) {
            Material mat = block.getType();
            byte data = block.getData();
            block.setType(Material.AIR);
            FallingBlock log = block.getWorld().spawnFallingBlock(block.getLocation(), mat, data);
            log.setVelocity(new Vector(Random.Float(-0.2f, 0.2f), 0, Random.Float(-0.2f, 0.2f)));
            log.setMetadata("log", new FixedMetadataValue(luck, true));
        } else {
            block.breakNaturally();
        }
    }

    @EventHandler
    private void blockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.LOG && event.getTo() != Material.LOG_2) {
            return;
        }
        if (!event.getEntity().hasMetadata("log")) {
            return;
        }

        final Block block = event.getBlock();
        new BukkitRunnable() {
            @Override
            public void run () {
                if (Random.Float() < 0.2f) {
                    block.getWorld().playSound(block.getLocation(), Sound.DIG_WOOD, Random.Float(0.5f, 1.2f), Random.Float());
                }
                ParticleEffect.CRIT.display(0.5f, 0.5f, 0.5f, 0.0f, 2, block.getLocation().add(0.5f, 0f, 0.5f));
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.8f, 0.8f, 0.8f, 0.8f, 5, block.getLocation().add(0.5f, 0f, 0.5f));
                block.breakNaturally();
            }
        }.runTaskLater(luck, 1);
    }
}
