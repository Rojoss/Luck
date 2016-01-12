package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class CritEffect extends BaseEvent {

    public CritEffect(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".critMultiplier", 2d);
    }

    @EventHandler
    private void damage(EntityDamageByEntityEvent event) {
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player)event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            ProjectileSource source = ((Projectile)event.getDamager()).getShooter();
            if (source instanceof Player) {
                damager = (Player)source;
            }
        }

        if (damager == null) {
            return;
        }
        if (!checkChance(gm.getPercentage(damager))) {
            return;
        }

        event.setDamage(event.getDamage() * cfg.getDouble(name + ".critMultiplier"));

        ParticleEffect.CRIT_MAGIC.display(0.5f, 1f, 0.5f, 0, 25, event.getEntity().getLocation());
        ParticleEffect.CRIT.display(0.5f, 1f, 0.5f, 0, 25, event.getEntity().getLocation());
        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 5, event.getEntity().getLocation());
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.SILVERFISH_KILL, 0.5f, 2);
        damager.getWorld().playSound(damager.getLocation(), Sound.SILVERFISH_KILL, 0.5f, 2);
    }
}
