package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RushEvent extends BaseEvent {

    public RushEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".amplifier", 2);
        cfg.addDefault(name + ".durationTicks", 100);
    }

    @EventHandler(ignoreCancelled = true)
    private void sprint(PlayerToggleSprintEvent event) {
        if (event.isSprinting()) {
            return;
        }
        if (!Util.hasPermission(event.getPlayer(), "luck.luck." + name)) {
            return;
        }
        if (checkChance(gm.getPercentage(event.getPlayer()))) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, cfg.getInt(name + ".durationTicks"), cfg.getInt(name + ".amplifier")));
            sendMessage(event.getPlayer());
        }
    }

}
