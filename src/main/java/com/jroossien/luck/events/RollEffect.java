package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class RollEffect extends BaseEvent {

    public RollEffect(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @EventHandler
    private void fallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player)event.getEntity();
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        event.setCancelled(true);
        ParticleEffect.CLOUD.display(0.8f, 0.2f, 0.8f, 0, 50, player.getLocation());
        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 10, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK2, Util.randomFloat(0.5f, 1), Util.randomFloat(1.5f, 2f));
        player.getWorld().playSound(player.getLocation(), Sound.STEP_WOOL, 1, Util.randomFloat(0f, 0.5f));
    }

}
