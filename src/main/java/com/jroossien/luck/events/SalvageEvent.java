package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalvageEvent extends BaseEvent {

    List<Material> allowedItems = new ArrayList<Material>();
    
    public SalvageEvent(String name, String description, Double minChance, Double maxChance) {
        super(name, description, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".items", Arrays.asList("DIAMOND_SWORD", "DIAMOND_PICKAXE", "DIAMOND_AXE", "DIAMOND_HOE", "DIAMOND_SPADE", "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
                "IRON_SWORD", "IRON_PICKAXE", "IRON_AXE", "IRON_HOE", "IRON_SPADE", "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
                "GOLD_SWORD", "GOLD_PICKAXE", "GOLD_AXE", "GOLD_HOE", "GOLD_SPADE", "GOLD_HELMET", "GOLD_CHESTPLATE", "GOLD_LEGGINGS", "GOLD_BOOTS",
                "STONE_SWORD", "STONE_PICKAXE", "STONE_AXE", "STONE_HOE", "STONE_SPADE",
                "WOODEN_SWORD", "WOODEN_PICKAXE", "WOODEN_AXE", "WOODEN_HOE", "WOODEN_SPADE",
                "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
                "FISHING_ROD", "SHEARS", "CARROT_STICK", "FLINT_AND_STEEL", "BOW"));

        for (String mat : cfg.getStringList(name + ".items")) {
            Material material = Material.matchMaterial(mat);
            if (material != null) {
                allowedItems.add(material);
            }
        }
    }

    @EventHandler
    public void itemBreak(PlayerItemBreakEvent event) {
        final Player player = event.getPlayer();
        if (!allowedItems.contains(event.getBrokenItem().getType())) {
            return;
        }

        List<Recipe> recipes = luck.getServer().getRecipesFor(new ItemStack(event.getBrokenItem().getType()));
        if (recipes.size() < 1) {
            return;
        }

        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        if (recipes.get(0) instanceof ShapedRecipe) {
            ingredients.addAll(((ShapedRecipe)recipes.get(0)).getIngredientMap().values());
        } else if (recipes.get(0) instanceof ShapelessRecipe) {
            ingredients.addAll(((ShapelessRecipe)recipes.get(0)).getIngredientList());
        }

        if (ingredients.size() < 1) {
            return;
        }

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        for (ItemStack item : ingredients) {
            if (item == null) {
                continue;
            }
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ANVIL_USE, 1, 2);
        ParticleEffect.ENCHANTMENT_TABLE.display(0.8f, 2f, 0.8f, 0, 300, player.getLocation());
    }

}
