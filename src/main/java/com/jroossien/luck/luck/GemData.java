package com.jroossien.luck.luck;

import com.jroossien.luck.Luck;

import java.util.UUID;

public class GemData {

    private UUID player;
    private Long lastUpdate;
    private int gems;
    private double percentage;

    public GemData(UUID player) {
        this.player = player;
    }

    public void setGems(int gems) {
        this.gems = gems;
        percentage = (double)gems / Luck.inst().getCfg().max_luck_gems;
        lastUpdate = System.currentTimeMillis();
    }

    public UUID getPlayer() {
        return player;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public int getGems() {
        return gems;
    }

    public double getPercentage() {
        return percentage;
    }
}
