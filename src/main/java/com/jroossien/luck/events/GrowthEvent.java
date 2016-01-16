package com.jroossien.luck.events;

import com.jroossien.luck.events.internal.BaseEvent;
import com.jroossien.luck.util.Random;
import com.jroossien.luck.util.Util;
import com.jroossien.luck.util.particles.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrowthEvent extends BaseEvent {

    List<Material> allowedCrops = new ArrayList<Material>();

    public GrowthEvent(String name, String description, String message, Double minChance, Double maxChance) {
        super(name, description, message, minChance, maxChance);
    }

    @Override
    public void loadData() {
        super.loadData();
        cfg.addDefault(name + ".plants", Arrays.asList("SUGAR_CANE", "CACTUS", "SAPLING", "RED_MUSHROOM", "BROWN_MUSHROOM", "SEEDS", "PUMPKIN_SEEDS", "MELON_SEEDS",
                "CARROT_ITEM", "POTATO_ITEM", "INK_SACK", "NETHER_STALK"));

        allowedCrops.clear();
        for (String mat : cfg.getStringList(name + ".plants")) {
            Material material = Material.matchMaterial(mat);
            if (material != null) {
                allowedCrops.add(material);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void interactPlant(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }

        final ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) {
            return;
        }
        if (!allowedCrops.contains(hand.getType())) {
            return;
        }

        //Plant seeds in soil, plant netherwart in soulsand, plant cocoa beans on jungle logs.
        //Cocoa beans grow in stages of 4 instead of 1 and only have 3 grow stages.
        Block block = event.getClickedBlock();
        if ((block.getType() == Material.SOIL && (hand.getType() == Material.SEEDS || hand.getType() == Material.MELON_SEEDS || hand.getType() == Material.PUMPKIN_SEEDS
                || hand.getType() == Material.POTATO_ITEM || hand.getType() == Material.CARROT_ITEM))
                || (block.getType() == Material.SOUL_SAND && hand.getType() == Material.NETHER_STALK)
                || (block.getType() == Material.LOG && block.getData() == 3 && hand.getType() == Material.INK_SACK && hand.getData().getData() == 3)) {
            final Block plantBlock = block.getType() == Material.LOG ? block.getRelative(event.getBlockFace()) : block.getRelative(BlockFace.UP);
            if (plantBlock.getType() == Material.AIR) {
                if (!checkChance(gm.getPercentage(player))) {
                    return;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (plantBlock.getType() == Material.AIR) {
                            cancel();
                            return;
                        }
                        byte data = plantBlock.getData();
                        if (plantBlock.getType() == Material.COCOA) {
                            data += 4;
                        } else {
                            data++;
                        }
                        plantBlock.setData(data);
                        plantBlock.getState().update();
                        ParticleEffect.VILLAGER_HAPPY.display(0.3f, 0.4f, 0.3f, 0, 10, plantBlock.getLocation().add(0.5f, -0.3f, 0.5f));
                        if (data >= 7 || (plantBlock.getType() == Material.COCOA && data >= 12) || (plantBlock.getType() == Material.NETHER_WARTS && data >= 3)) {
                            cancel();
                        }
                    }
                }.runTaskTimer(luck, 2, 1);
                player.playSound(block.getLocation(), Sound.DIG_GRASS, Random.Float(0.8f, 1.2f), Random.Float(0.4f, 1f));
                ParticleEffect.DRIP_WATER.display(0.3f, 0.4f, 0.3f, 0, 30, plantBlock.getLocation().add(0.5f, -0.5f, 0.5f));
                sendMessage(player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void placePlant(BlockPlaceEvent event) {
        //Mushrooms, Saplings, Cactus and Sugarcane
        final ItemStack hand = event.getItemInHand();
        if (hand == null) {
            return;
        }
        final Block block = event.getBlock();
        if (!allowedCrops.contains(hand.getType())) {
            return;
        }

        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !Util.hasPermission(player, "luck.luck." + name)) {
            return;
        }

        if (!checkChance(gm.getPercentage(player))) {
            return;
        }

        //Cactus and sugarcane. (Stack up to 3)
        if (block.getType() == Material.CACTUS || block.getType() == Material.SUGAR_CANE_BLOCK) {
            ParticleEffect.VILLAGER_HAPPY.display(0.3f, 0.3f, 0.3f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));
            sendMessage(player);
            Material type = block.getType();
            Block currBlock = block;
            for (int i = 0; i < 2; i++) {
                currBlock = currBlock.getRelative(BlockFace.UP);
                if (currBlock.getType() == Material.AIR) {
                    currBlock.setType(type);
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(currBlock.getType(), currBlock.getData()), 0.8f, 0.8f, 0.8f, 0f, 20, currBlock.getLocation().add(0.5f, 0f, 0.5f));
                    currBlock.getWorld().playSound(currBlock.getLocation(), type == Material.SUGAR_CANE_BLOCK ? Sound.DIG_GRASS : Sound.DIG_WOOL, 1, Random.Float(0.8f, 1.2f));
                }
            }
            return;
        }

        //Saplings and mushrooms (create tree/shroom)
        final Material plantType = block.getType();
        final byte plantData = block.getData();
        block.setType(Material.AIR);
        new BukkitRunnable() {
            boolean success = false;
            @Override
            public void run () {
                if (hand.getType() == Material.BROWN_MUSHROOM) {
                    success = block.getWorld().generateTree(block.getLocation(), TreeType.BROWN_MUSHROOM);
                } else if (hand.getType() == Material.RED_MUSHROOM) {
                    success = block.getWorld().generateTree(block.getLocation(), TreeType.RED_MUSHROOM);
                } else if (hand.getType() == Material.SAPLING) {
                    byte data = hand.getData().getData();
                    if (data == 0) {
                        if (block.getBiome() == Biome.SWAMPLAND || block.getBiome() == Biome.SWAMPLAND_MOUNTAINS) {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.SWAMP);
                        } else {
                            if (Random.Float() < 0.2f) {
                                success = block.getWorld().generateTree(block.getLocation(), TreeType.BIG_TREE);
                            } else {
                                success = block.getWorld().generateTree(block.getLocation(), TreeType.TREE);
                            }
                        }
                    } else if (data == 1) {
                        if (Random.Float() < 0.2f) {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.MEGA_REDWOOD);
                        } else if (Random.Float() < 0.3f) {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.TALL_REDWOOD);
                        } else {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.REDWOOD);
                        }
                    } else if (data == 2) {
                        if (Random.Float() < 0.3f) {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.TALL_BIRCH);
                        } else {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.BIRCH);
                        }
                    } else if (data == 3) {
                        if (Random.Float() < 0.3f) {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.JUNGLE);
                        } else {
                            success = block.getWorld().generateTree(block.getLocation(), TreeType.SMALL_JUNGLE);
                        }
                    } else if (data == 4) {
                        success = block.getWorld().generateTree(block.getLocation(), TreeType.ACACIA);
                    } else if (data == 5) {
                        success = block.getWorld().generateTree(block.getLocation(), TreeType.DARK_OAK);
                    }
                }
                if (!success) {
                    block.setType(plantType);
                    block.setData(plantData);
                } else {
                    ParticleEffect.VILLAGER_HAPPY.display(0.3f, 0.3f, 0.3f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));
                    sendMessage(player);
                }
            }
        }.runTaskLater(luck, 1);
    }

}
