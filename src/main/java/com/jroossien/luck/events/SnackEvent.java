package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SnackEvent extends BaseEvent {

    public SnackEvent(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        cfg.addDefault(name + ".hungerMultiplier", 2d);
        cfg.addDefault(name + ".saturationMultiplier", 2d);
    }
    
    @EventHandler
    public void Eat(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        final float saturation = player.getSaturation();
        final int food = player.getFoodLevel();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getFoodLevel() >= 20) {
                    return;
                }
                float satChange = player.getSaturation() - saturation;
                int change = player.getFoodLevel() - food;

                player.setSaturation(player.getSaturation() + (satChange * ((float)cfg.getDouble(name + ".saturationMultiplier") / 2)));
                player.setFoodLevel(player.getFoodLevel() + (int)Math.round(change * (cfg.getDouble(name + ".hungerMultiplier") / 2)));

                player.getWorld().playSound(player.getLocation(), Sound.BURP, 1.0f, 1.2f);
            }
        }.runTaskLater(luck, 1);
    }

}