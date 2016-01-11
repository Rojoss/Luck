package com.jroossien.luck.config;

public class PluginCfg extends EasyConfig {

    public Double gem_purchase_price = 100d;

    public PluginCfg(String fileName) {
        this.setFile(fileName);
        load();
    }
}
