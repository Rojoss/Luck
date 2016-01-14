package com.jroossien.luck.events.internal;

import com.jroossien.luck.Luck;
import com.jroossien.luck.events.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private Luck lu;
    private static EventManager instance;

    private File cfgFile;
    private YamlConfiguration cfg;

    private Map<String, BaseEvent> events = new HashMap<String, BaseEvent>();

    public EventManager(Luck lu) {
        this.lu = lu;
        instance = this;

        cfgFile = new File(lu.getDataFolder(), "Events.yml");
        cfg = YamlConfiguration.loadConfiguration(cfgFile);
        cfg.options().copyDefaults(true);

        registerEvents();
    }

    public void registerEvents() {
        registerEvent(new RushEvent("Rush", "Gain a speed boost when you start sprinting.", "Woosh.. Run faster!", 0d, 3d));
        registerEvent(new TreeFellerEvent("FellTree", "Chop down the entire tree when breaking the bottom blocks.", "BAM! That tree is gone!", 1d, 10d));
        registerEvent(new SnackEvent("Snack", "Restore extra hunger and saturation when eating food.", "Nom nom nom Some extra hunger/saturation for you!", 3d, 30d));
        registerEvent(new SmashEvent("Smash", "Break a block instantly when you hit it.", "SMASH!", 0d, 5d));
        registerEvent(new HasteEvent("Haste", "Gain a haste effect when you break blocks.", "Dig faster!", 0d, 5d));
        registerEvent(new SpawnerEvent("Spawner", "Get the spawner item from the spawner you broke.", "Woah! You got a spawner!", 0d, 1d));
        registerEvent(new LootEvent("Loot", "Get extra loot from killing mobs.", "Extra loot for you!", 0d, 5d));
        registerEvent(new FortuneEvent("Fortune", "Get extra loot from breaking blocks.", "Extra loot for you!", 0d, 5d));
        registerEvent(new GrowthEvent("Growth", "Grow crops, trees and other plants instantly when you plant them.", "Look at it grow!", 2d, 20d));
        registerEvent(new SalvageEvent("Salvage", "Get all items back when an item breaks.", "Lucky you.. Item salvaged!", 0d, 5d));
        registerEvent(new ExtinguishEvent("Extinguish", "Get a temporary fire resistance buff when you take damage by lava.", "Phew... Now get out quick!", 5d, 40d));
        registerEvent(new RollEvent("Roll", "Roll when you take fall damage to stop the damage.", "Roll away!", 3d, 30d));
        registerEvent(new RetainExpEvent("RetainExp", "Keep all your experience when you die.", "Look on the bright side, you kept your experience!", 3d, 20d));
        registerEvent(new LuckyEvent("Lucky", "Get a lucky gem when breaking emerald ore.", "How much luck does one need?", 4d, 20d));
        registerEvent(new CritEvent("Crit", "Deal extra damage when you hit a player/mob.", "CRIT!", 0d, 4d));
        registerEvent(new BlockEvent("Block", "Take less damage! When blocking with a sword you take even less damage.", "BLOCK!", 0d, 4d));

        save();
    }

    public void registerEvent(BaseEvent event) {
        if (event == null) {
            return;
        }
        String name = event.getName().toLowerCase().replace(" ","");
        //Don't register if it already is.
        if (events.containsKey(name)) {
            //Unregister event if it has been disabled.
            if (!cfg.getBoolean(name + ".enabled")) {
                HandlerList.unregisterAll(event);
                events.remove(name);
                return;
            }
            events.get(name).loadData();
            return;
        }
        //Load default config data.
        event.loadData();
        //Only register if it's enabled.
        if (!cfg.getBoolean(name + ".enabled")) {
            return;
        }
        //Register event.
        lu.getServer().getPluginManager().registerEvents(event, lu);
        events.put(name, event);
        return;
    }

    public YamlConfiguration getCfg() {
        return cfg;
    }

    public void load() {
        try {
            cfg.load(cfgFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            cfg.save(cfgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, BaseEvent> getEvents() {
        return events;
    }

    public static EventManager inst() {
        return instance;
    }
}
