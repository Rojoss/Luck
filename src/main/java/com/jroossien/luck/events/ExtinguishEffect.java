package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtinguishEffect extends BaseEvent {

    private List<UUID> lavaPlayers = new ArrayList<UUID>();

    public ExtinguishEffect(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".durationTicks", 200);
    }

    @EventHandler
    private void lavaDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.LAVA) {
            return;
        }

        Player player = (Player)event.getEntity();
        if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            return;
        }

        if (lavaPlayers.contains(player.getUniqueId())) {
            return;
        }
        lavaPlayers.add(player.getUniqueId());

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        event.setCancelled(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, cfg.getInt(name + ".durationTicks"), 0));
        ParticleEffect.LAVA.display(1f, 0.2f, 1f, 0, 50, player.getLocation());
        ParticleEffect.FLAME.display(0.5f, 1f, 0.5f, 0, 20, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.FIZZ, Util.randomFloat(0.5f, 1), Util.randomFloat(0, 0.8f));
    }

    @EventHandler
    private void playerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getTo().getBlock().getType() != Material.LAVA && event.getTo().getBlock().getType() != Material.STATIONARY_LAVA && lavaPlayers.contains(player.getUniqueId())) {
            lavaPlayers.remove(player.getUniqueId());
            return;
        }
    }

}
