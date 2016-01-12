package com.jroossien.luck.events.internal;

import com.jroossien.luck.Luck;
import com.jroossien.luck.events.*;
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
        registerEvent(new RushEvent("Rush", "", 0d, 3d));
        registerEvent(new TreeFellerEvent("FellTree", "", 1d, 10d));
        registerEvent(new SnackEvent("Snack", "", 3d, 30d));
        registerEvent(new SmashEvent("Smash", "", 0d, 5d));
        registerEvent(new HasteEvent("Haste", "", 0d, 5d));

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
            }
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
