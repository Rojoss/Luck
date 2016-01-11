package com.jroossien.luck.config;

import java.util.HashMap;
import java.util.Map;

public class PluginCfg extends EasyConfig {

    public int max_luck_gems = 64;
    public Double gem_purchase_price = 100d;
    public String gem_recipe__row1 = " $ ";
    public String gem_recipe__row2 = "$@$";
    public String gem_recipe__row3 = " $ ";
    public Map<String, Map<String, String>> gem_recipe_items = new HashMap<String, Map<String, String>>();

    public PluginCfg(String fileName) {
        this.setFile(fileName);
        load();

        if (gem_recipe_items.isEmpty()) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("char", "$");
            data.put("material", "EMERALD");
            gem_recipe_items.put("1", data);

            data = new HashMap<String, String>();
            data.put("char", "@");
            data.put("material", "GLOWSTONE_DUST");
            gem_recipe_items.put("2", data);

            save();
        }
    }
}
