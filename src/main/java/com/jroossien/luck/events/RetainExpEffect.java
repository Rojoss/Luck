package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RetainExpEffect extends BaseEvent {

    public RetainExpEffect(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        event.setDroppedExp(0);
        event.setKeepLevel(true);

        player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2f);
        ParticleEffect.VILLAGER_HAPPY.display(1.0f, 1.0f, 1.0f, 1.0f, 50, player.getLocation());
    }

}
