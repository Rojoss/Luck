package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.events.internal.BlockDrillEvent;
import com.jroossien.luck.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class DrillEvent extends BaseEvent {

    private BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    public DrillEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();

        cfg.addDefault(name + ".blocks", Arrays.asList("IRON_ORE", "GOLD_ORE", "COAL_ORE", "DIAMOND_ORE", "EMERALD_ORE", "LAPIS_ORE", "QUARTZ_ORE", "REDSTONE_ORE", "GRAVEL"));
        cfg.addDefault(name + ".maxBlocks", 15);
        cfg.addDefault(name + ".maxDistance", 3d);
    }

    @EventHandler(ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event) {
        if (event instanceof BlockDrillEvent) {
            return;
        }
        Block block = event.getBlock();
        if (!cfg.getStringList(name + ".blocks").contains(block.getType().toString())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        breakNearby(event.getBlock().getType(), event.getBlock().getLocation().add(0.5f,0.5f,0.5f), player, block, 0);
        sendMessage(player);
    }

    private boolean breakNearby(Material type, Location source, Player player, Block block, int count) {
        if (block.getType() != type) {
            return false;
        }
        if (count >= cfg.getInt(name + ".maxBlocks")) {
            return false;
        }
        if (source.distance(block.getLocation().add(0.5f, 0.5f, 0.5f)) > cfg.getDouble(name + ".maxDistance")) {
            return false;
        }
        count++;
        if (!source.equals(block.getLocation().add(0.5f, 0.5f, 0.5f))) {
            BlockDrillEvent blockBreak = new BlockDrillEvent(block, player);
            luck.getServer().getPluginManager().callEvent(blockBreak);
            if (!blockBreak.isCancelled() && block.getType() == type) {
                block.breakNaturally();
            }
        }
        for (BlockFace face : faces) {
            if (breakNearby(type, source, player, block.getRelative(face), count)) {
                return true;
            }
        }
        return false;
    }

}
