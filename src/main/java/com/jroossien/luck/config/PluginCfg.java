package com.jroossien.luck.config;

public class PluginCfg extends EasyConfig {

    public PluginCfg(String fileName) {
        this.setFile(fileName);
        load();
    }
}
