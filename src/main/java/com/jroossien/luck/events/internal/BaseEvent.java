package com.jroossien.luck.events.internal;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.config.messages.Param;
import com.jroossien.luck.luck.GemManager;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BaseEvent implements Listener {

    protected Luck luck;
    protected GemManager gm;
    protected EventManager em;
    protected YamlConfiguration cfg;

    protected String name;

    protected String displayName;
    protected String description;
    private String message;
    protected Double minChance;
    protected Double maxChance;

    public BaseEvent(String name, String description, String message, Double minChance, Double maxChance) {
        luck = Luck.inst();
        gm = luck.getGM();
        em = EventManager.inst();
        cfg = em.getCfg();

        displayName = name;
        this.name = name.toLowerCase().replace(" ", "");
        this.description = description;
        this.message = message;
        this.minChance = minChance;
        this.maxChance = maxChance;
    }

    public void loadData() {
        cfg.addDefault(name + ".enabled", true);
        cfg.addDefault(name + ".displayName", displayName);
        cfg.addDefault(name + ".description", description);
        cfg.addDefault(name + ".message", message);
        cfg.addDefault(name + ".chance.min", minChance);
        cfg.addDefault(name + ".chance.max", maxChance);

        displayName = cfg.getString(name + ".displayName");
        description = cfg.getString(name + ".description");
        message = cfg.getString(name + ".message");
        minChance = cfg.getDouble(name + ".chance.min");
        maxChance = cfg.getDouble(name + ".chance.max");
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public void sendMessage(Player player) {
        if (message.trim().isEmpty()) {
            return;
        }
        Msg.MESSAGE_SYNTAX.send(player, false, true, Param.P("{message}", message));
    }

    public Double getMinChance() {
        return minChance;
    }

    public Double getMaxChance() {
        return maxChance;
    }

    public Double getChance(double percentage) {
        return Util.lerp(minChance / 100, maxChance / 100, percentage);
    }

    protected boolean checkChance(double percentage) {
        return Random.Float() <= getChance(percentage);
    }
}
