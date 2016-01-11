package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import org.bukkit.Material;
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

    public TreeFellerEvent(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();

        cfg.addDefault(name + ".treeTypes", Arrays.asList("OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK"));
        cfg.addDefault(name + ".breakBelow", false);
        cfg.addDefault(name + ".blocksBelowAllowed", 2);
        cfg.addDefault(name + ".animateFall", true);

        for (String str : cfg.getStringList(name + ".treeTypes")) {
            allowedtypes.add(TreeTypes.valueOf(str));
        }
    }


    @EventHandler
    private void breakTree(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
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
        if (cfg.getBoolean(name + ".animateFall")) {
            Material mat = block.getType();
            byte data = block.getData();
            block.setType(Material.AIR);
            FallingBlock log = block.getWorld().spawnFallingBlock(block.getLocation(), mat, data);
            log.setVelocity(new Vector(Util.randomFloat(-0.2f, 0.2f), 0, Util.randomFloat(-0.2f, 0.2f)));
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
                block.breakNaturally();
            }
        }.runTaskLater(luck, 1);
    }
}
