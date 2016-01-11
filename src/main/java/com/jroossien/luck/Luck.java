package com.jroossien.luck;

import com.jroossien.luck.commands.Commands;
import com.jroossien.luck.config.PluginCfg;
import com.jroossien.luck.config.messages.MessageCfg;
import com.jroossien.luck.listeners.MainListener;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Luck extends JavaPlugin {

    private static Luck instance;
    private Vault vault;
    private Economy economy;

    private PluginCfg cfg;
    private MessageCfg msgCfg;

    private Commands cmds;

    private final Logger log = Logger.getLogger("Luck");

    @Override
    public void onDisable() {
        instance = null;
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;
        log.setParent(this.getLogger());

        Plugin vaultPlugin = getServer().getPluginManager().getPlugin("Vault");
        if (vaultPlugin != null) {
            vault = (Vault)vaultPlugin;
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }
        if (economy == null) {
            log("Failed to load Economy from Vault. The plugin will still work fine but you won't be able to purchase luck!");
        }

        cfg = new PluginCfg("plugins/Luck/Luck.yml");
        msgCfg = new MessageCfg("plugins/Luck/Messages.yml");

        cmds = new Commands(this);

        registerListeners();

        log("loaded successfully");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainListener(this), this);
    }

    public void log(Object msg) {
        log.info("[Luck " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static Luck inst() {
        return instance;
    }

    public Vault getVault() {
        return vault;
    }

    public Economy getEco() {
        return economy;
    }

    public PluginCfg getCfg() {
        return cfg;
    }

    public MessageCfg getMsgCfg() {
        return msgCfg;
    }
}
