package com.jroossien.luck;

import com.jroossien.luck.commands.Commands;
import com.jroossien.luck.config.PluginCfg;
import com.jroossien.luck.config.messages.MessageCfg;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.events.internal.EventManager;
import com.jroossien.luck.listeners.MainListener;
import com.jroossien.luck.luck.GemManager;
import com.jroossien.luck.util.item.EItem;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public class Luck extends JavaPlugin {

    private static Luck instance;
    private Vault vault;
    private Economy economy;

    private PluginCfg cfg;
    private MessageCfg msgCfg;

    private Commands cmds;

    private GemManager gm;
    private EventManager em;

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

        gm = new GemManager(this);
        em = new EventManager(this);

        registerRecipe();
        registerListeners();

        log("loaded successfully");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    private void registerRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getGem());
        recipe.shape(cfg.gem_recipe__row1, cfg.gem_recipe__row2, cfg.gem_recipe__row3);
        for (Map<String, String> ingredient : cfg.gem_recipe_items.values()) {
            Material mat = Material.matchMaterial(ingredient.get("material"));
            String character = ingredient.get("char");
            if (mat == null || character.isEmpty()) {
                continue;
            }
            recipe.setIngredient(ingredient.get("char").toCharArray()[0], mat);
        }
        getServer().addRecipe(recipe);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
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

    public GemManager getGM() {
        return gm;
    }

    public EventManager getEM() {
        return em;
    }


    public EItem getGem() {
        return new EItem(Material.EMERALD).setName(Msg.ITEM_NAME.getMsg()).setLore(Msg.ITEM_LORE.getMsg()).makeGlowing(true);
    }
}
