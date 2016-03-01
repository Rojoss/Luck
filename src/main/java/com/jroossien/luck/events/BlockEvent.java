package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockEvent extends BaseEvent {

    public BlockEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".damageMultiplier", 0.5d);
        cfg.addDefault(name + ".damageMultiplierBlocking", 0d);
        List<String> causes = new ArrayList<String>();
        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            causes.add(cause.toString());
        }
        cfg.addDefault(name + ".damageCauses", causes);
    }

    @EventHandler(ignoreCancelled = true)
    private void damage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!cfg.getStringList(name + ".damageCauses").contains(event.getCause().toString())) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        double multiplier = player.isBlocking() ? cfg.getDouble(name + ".damageMultiplierBlocking") : cfg.getDouble(name + ".damageMultiplier");
        if (multiplier == 0) {
            event.setDamage(0);
        } else {
            event.setDamage(event.getDamage() * multiplier);
        }

        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.PISTON_EXTENSION, (byte)0), 0.5f, 1f, 0.5f, 0, player.isBlocking() ? 40 : 15, event.getEntity().getLocation());
        if (player.isBlocking()) {
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.IRON_FENCE, (byte)0), 0.5f, 1f, 0.5f, 0, 10, event.getEntity().getLocation());
        }
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_METAL_BREAK, 0.2f, player.isBlocking() ? 0 : 1);

        sendMessage(player);
    }
}
