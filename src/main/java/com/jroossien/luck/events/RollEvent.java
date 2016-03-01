package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class RollEvent extends BaseEvent {

    public RollEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @EventHandler(ignoreCancelled = true)
    private void fallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player)event.getEntity();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        event.setCancelled(true);
        ParticleEffect.CLOUD.display(0.8f, 0.2f, 0.8f, 0, 50, player.getLocation());
        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 10, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, Random.Float(0.5f, 1), Random.Float(1.5f, 2f));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CLOTH_STEP, 1, Random.Float(0f, 0.5f));
        sendMessage(player);
    }

}
