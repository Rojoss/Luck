package com.jroossien.luck.events.internal;

import com.jroossien.luck.Luck;
import com.jroossien.luck.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class BaseEvent implements Listener {

    protected Luck luck;
    protected EventManager em;
    protected YamlConfiguration cfg;

    protected String name;

    protected String displayName;
    protected String description;
    protected Double minChance;
    protected Double maxChance;

    public BaseEvent(String name, String description, Double minChance, Double maxChance) {
        luck = Luck.inst();
        em = EventManager.inst();
        cfg = em.getCfg();

        displayName = name;
        this.name = name.toLowerCase().replace(" ", "");
        this.description = description;
        this.minChance = minChance;
        this.maxChance = maxChance;
    }

    public void loadData() {
        cfg.addDefault(name + ".enabled", true);
        cfg.addDefault(name + ".displayName", displayName);
        cfg.addDefault(name + ".description", description);
        cfg.addDefault(name + ".chance.min", minChance);
        cfg.addDefault(name + ".chance.max", maxChance);

        displayName = cfg.getString(name + ".displayName");
        description = cfg.getString(name + ".description");
        minChance = cfg.getDouble(name + ".chance.min");
        maxChance = cfg.getDouble(name + ".chance.max");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getMinChance() {
        return minChance;
    }

    public Double getMaxChance() {
        return maxChance;
    }

    protected Double getChance(float percentage) {
        return Util.lerp(minChance, maxChance, percentage);
    }

    protected boolean checkChance(float percentage) {
        return Util.randomFloat() * 100 <= getChance(percentage);
    }
}
