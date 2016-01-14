package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RetainExpEvent extends BaseEvent {

    public RetainExpEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @EventHandler(ignoreCancelled = true)
    private void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        event.setDroppedExp(0);
        event.setKeepLevel(true);

        player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2f);
        ParticleEffect.VILLAGER_HAPPY.display(1.0f, 1.0f, 1.0f, 1.0f, 50, player.getLocation());
        sendMessage(player);
    }

}
