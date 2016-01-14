package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SnackEvent extends BaseEvent {

    public SnackEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".hungerMultiplier", 2d);
        cfg.addDefault(name + ".saturationMultiplier", 2d);
    }

    @EventHandler
    public void Eat(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        if (!Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }
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

                player.getWorld().playSound(player.getLocation(), Sound.BURP, Random.Float(0.8f, 1.4f), Random.Float(0.6f, 1.5f));
                sendMessage(player);
            }
        }.runTaskLater(luck, 1);
    }

}
