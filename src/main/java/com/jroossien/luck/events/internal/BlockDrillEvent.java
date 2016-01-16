package com.jroossien.luck.events.internal;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockDrillEvent extends BlockBreakEvent {
    public BlockDrillEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
