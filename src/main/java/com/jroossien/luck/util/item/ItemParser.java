package com.jroossien.luck.util.item;

import com.jroossien.luck.Luck;
import com.jroossien.luck.config.messages.Msg;
import com.jroossien.luck.config.messages.Param;
import com.jroossien.luck.util.Util;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemParser {

    private String string = null;
    private EItem item = null;
    private String error = "";
    private boolean success = false;

    /**
     * Parse the given string in to a item.
     * @param string String with item format.
     * @param ignoreErrors If this is true it will continue parsing even if there are non breaking errors.
     */
    public ItemParser(String string, boolean ignoreErrors) {
        this.string = string;

        String[] words = string.split(" ");
        if (string == null || string.trim().length() < 1) {
            error = Msg.NO_ITEM_SPECIFIED.getMsg();
            return;
        }

        EItem item = new EItem(Material.AIR);
        List<String> sections = Util.splitQuotedString(string.trim());

        //Item:data
        Material mat = null;
        Short data = 0;

        String[] nameSplit = sections.get(0).split(":");
        if (Util.getInt(nameSplit[0]) != null) {
            //Get item by Id
            mat = Material.getMaterial(Util.getInt(nameSplit[0]));
        } else {
            //Get item by name
            mat = Material.matchMaterial(nameSplit[0]);
        }
        //Get data/durability
        if (nameSplit.length > 1) {
            data = Util.getShort(nameSplit[1]);
        }

        //Get item through Vault.
        if (mat == null && Luck.inst().getVault() != null) {
            ItemInfo itemInfo = Items.itemByName(sections.get(0));
            if (itemInfo != null) {
                mat = itemInfo.getType();
                data = itemInfo.getSubTypeId();
            }
        }

        if (mat == null) {
            error = Msg.UNKNOWN_ITEM_NAME.getMsg(Param.P("{input}", sections.get(0)));
            return;
        }
        item.setType(mat);
        item.setDurability(data == null ? 0 : data);
        item.setAmount(1); //Default

        //If it's air or if there is no meta specified we're done parsing...
        if (item.getType() == Material.AIR || sections.size() < 2) {
            this.item = item;
            success = true;
            return;
        }
        ItemMeta defaultMeta = Bukkit.getServer().getItemFactory().getItemMeta(item.getType());

        //Create a map with all meta keys/values.
        Map<String, String> metaMap = new HashMap<String, String>();
        for (String section : sections) {
            //Amount
            Integer intVal = Util.getInt(section);
            if (intVal != null) {
                item.setAmount(intVal);
                continue;
            }

            String[] split = section.split(":");
            //No data after semicolon
            if (split.length < 2) {
                error = Msg.MISSING_META_VALUE.getMsg(Param.P("{meta}", split[0]));
                if (ignoreErrors) {
                    continue;
                } else {
                    return;
                }
            }
            metaMap.put(split[0].toLowerCase(), split[1]);
        }

        //Name
        if (metaMap.containsKey("name")) {
            item.setName(metaMap.get("name"));
            metaMap.remove("name");
        }

        //Lore
        if (metaMap.containsKey("lore")) {
            String[] lore =  metaMap.get("lore").split("\\|");
            item.setLore(lore);
            metaMap.remove("lore");
        }

        //Color
        if (metaMap.containsKey("leather")) {
            Color color = Util.getColor(metaMap.get("leather"));
            if (color == null) {
                error = Msg.INVALID_COLOR.getMsg(Param.P("{input}", metaMap.get("leather")));
                if (!ignoreErrors) {
                    return;
                }
            } else {
                item.setColor(color);
            }
            metaMap.remove("leather");
        }

        //Skulls
        if (metaMap.containsKey("player")) {
            item.setSkull(metaMap.get("player"));
            metaMap.remove("player");
        }

        //Banners
        if (metaMap.containsKey("basecolor")) {
            DyeColor color;
            if (Util.getByte(metaMap.get("basecolor")) != null) {
                color = DyeColor.getByData(Util.getByte(metaMap.get("basecolor")));
            } else {
                color = DyeColor.valueOf(metaMap.get("basecolor"));
            }
            if (color == null) {
                error = Msg.INVALID_DYE_COLOR.getMsg(Param.P("{input}", metaMap.get("basecolor")));
                if (!ignoreErrors) {
                    return;
                }
            } else {
                item.setBaseColor(color);
            }
            metaMap.remove("basecolor");
        }

        //Firework
        FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();
        boolean hasFireworkMeta = false;
        boolean hasShape = false;
        boolean hasColor = false;
        if (metaMap.containsKey("power")) {
            if (Util.getInt(metaMap.get("power")) == null) {
                error = Msg.NOT_A_NUMBER.getMsg(Param.P("{input}", metaMap.get("power")));
                if (!ignoreErrors) {
                    return;
                }
            } else {
                item.setPower(Util.getInt(metaMap.get("power")));
            }
            metaMap.remove("power");
        }
        if (metaMap.containsKey("shape")) {
            FireworkEffect.Type shape = FireworkEffect.Type.valueOf(metaMap.get("shape"));
            if (shape == null) {
                error = Msg.INVALID_FIREWORK_SHAPE.getMsg(Param.P("{input}", metaMap.get("shape")));
                if (!ignoreErrors) {
                    return;
                }
            } else {
                fireworkBuilder.with(shape);
            }
            metaMap.remove("shape");
            hasFireworkMeta = true;
            hasShape = true;
        }
        if (metaMap.containsKey("color")) {
            String[] colorSplit = metaMap.get("color").split(";");
            List<Color> colors = new ArrayList<Color>();
            for (String color : colorSplit) {
                Color clr = Util.getColor(color);
                if (clr == null) {
                    error = Msg.INVALID_COLOR.getMsg(Param.P("{input}", metaMap.get("color")));
                    if (!ignoreErrors) {
                        return;
                    }
                } else {
                    colors.add(clr);
                }
            }
            if (colors.size() > 0) {
                fireworkBuilder.withColor(colors);
            }
            metaMap.remove("color");
            hasFireworkMeta = true;
            hasColor = true;
        }
        if (metaMap.containsKey("fade")) {
            String[] colorSplit = metaMap.get("fade").split(";");
            List<Color> colors = new ArrayList<Color>();
            for (String color : colorSplit) {
                Color clr = Util.getColor(color);
                if (clr == null) {
                    error = Msg.INVALID_COLOR.getMsg(Param.P("{input}", metaMap.get("fade")));
                    if (!ignoreErrors) {
                        return;
                    }
                } else {
                    colors.add(clr);
                }
            }
            if (colors.size() > 0) {
                fireworkBuilder.withFade(colors);
            }
            metaMap.remove("fade");
            hasFireworkMeta = true;
        }
        if (metaMap.containsKey("flicker")) {
            if (Util.getBool(metaMap.get("flicker"))) {
                fireworkBuilder.withFlicker();
            }
            metaMap.remove("twinkle");
            hasFireworkMeta = true;
        }
        if (metaMap.containsKey("trail")) {
            if (Util.getBool(metaMap.get("trail"))) {
                fireworkBuilder.withTrail();
            }
            metaMap.remove("trail");
            hasFireworkMeta = true;
        }
        try {
            item.addEffect(fireworkBuilder.build());
        } catch (Exception e) {
            if (hasFireworkMeta) {
                if (!hasShape) {
                    error = Msg.MISSING_FIREWORK_SHAPE.getMsg();
                    if (!ignoreErrors) {
                        return;
                    }
                }
                if (!hasColor) {
                    error = Msg.MISSING_FIREWORK_COLOR.getMsg();
                    if (!ignoreErrors) {
                        return;
                    }
                }
            }
        }


        //If there is any meta remaining do enchants, effects and banner patterns.
        if (metaMap.size() > 0) {
            for (Map.Entry<String, String> entry : metaMap.entrySet()) {
                //Enchantments
                Enchantment enchant = Enchantment.getByName(entry.getKey());
                if (enchant != null) {
                    if (Util.getInt(entry.getValue()) == null) {
                        error = Msg.INVALID_ENCHANT_VALUE.getMsg(Param.P("{input}", entry.getValue()));
                        return;
                    }
                    item.addEnchant(enchant, Util.getInt(entry.getValue()));
                    continue;
                }

                //Potion effects
                PotionEffectType effect = PotionEffectType.getByName(entry.getKey());
                if (effect != null) {
                    String[] split = entry.getValue().split("\\.");
                    if (split.length < 2) {
                        error = Msg.INVALID_POTION_VALUE.getMsg(Param.P("{input}", entry.getValue()));
                        return;
                    }
                    if (Util.getInt(split[0]) == null || Util.getInt(split[1]) == null) {
                        error = Msg.INVALID_POTION_VALUE.getMsg(Param.P("{input}", entry.getValue()));
                        return;
                    }
                    item.addEffect(new PotionEffect(effect, Util.getInt(split[0]), Util.getInt(split[1])), true);
                    continue;
                }

                //Banner patterns
                PatternType pattern = PatternType.getByIdentifier(entry.getKey());
                if (pattern == null) {
                    PatternType.valueOf(entry.getKey());
                }
                if (pattern != null) {
                    DyeColor color;
                    if (Util.getByte(entry.getValue()) != null) {
                        color = DyeColor.getByData(Util.getByte(entry.getValue()));
                    } else {
                        color = DyeColor.valueOf(entry.getValue());
                    }
                    if (color == null) {
                        error = Msg.INVALID_DYE_COLOR.getMsg(Param.P("{input}", entry.getValue()));
                        return;
                    }
                    item.addPattern(pattern, color);
                    continue;
                }
            }
        }

        //Done parsing!
        this.item = item;
    }

    /**
     * Parse the given item in to a string.
     * @param itemStack item which needs to be parsed.
     */
    public ItemParser(ItemStack itemStack) {
        EItem item = new EItem(itemStack);
        this.item = item;

        //Don't do anything for air.
        if (item.getType() == Material.AIR) {
            this.string = "Air";
            return;
        }

        List<String> components = new ArrayList<String>();

        //Material[:data]
        String itemString = item.getType().toString().toLowerCase().replaceAll("_", "");
        if (item.getDurability() > 0) {
            itemString += ":" + item.getDurability();
        }
        components.add(itemString);

        //Amount
        components.add(Integer.toString(item.getAmount()));

        //No meta
        if (!item.hasItemMeta()) {
            this.string = Util.implode(components, " ");
            return;
        }
        ItemMeta meta = item.getItemMeta();

        //Name
        if (meta.hasDisplayName()) {
            components.add("name:" + Util.removeColor(meta.getDisplayName()).replaceAll(" ", "_"));
        }

        //Lore
        if (meta.hasLore()) {
            String lore = Util.implode(meta.getLore(), "|");
            components.add("lore:" + Util.removeColor(lore).replaceAll(" ", "_"));
        }

        //Enchants
        if (meta.hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                components.add(entry.getKey().getName().toLowerCase().replaceAll("_", "") + ":" + entry.getValue());
            }
        }

        //Leather color
        if (meta instanceof LeatherArmorMeta) {
            Color color = ((LeatherArmorMeta)meta).getColor();
            if (color != null) {
                components.add("leather:" + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
            }
        }

        //Skulls
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta)meta;

            if (skullMeta.hasOwner() && skullMeta.getOwner() != null) {
                components.add("player:" + skullMeta.getOwner());
            }
        }

        //Banners
        if (meta instanceof BannerMeta) {
            BannerMeta bannerMeta = (BannerMeta)meta;

            components.add("basecolor:" + bannerMeta.getBaseColor().toString().toLowerCase().replaceAll("_", ""));
            if (bannerMeta.getPatterns() != null && bannerMeta.getPatterns().size() > 0) {
                for (Pattern pattern : bannerMeta.getPatterns()) {
                    components.add(pattern.getPattern().toString().toLowerCase().replaceAll("_", "")
                            + ":" + pattern.getColor().toString().toLowerCase().replaceAll("_", ""));
                }
            }
        }

        //Firework
        if (meta instanceof FireworkMeta) {
            FireworkMeta fireworkMeta = (FireworkMeta)meta;
            components.add("power:" + fireworkMeta.getPower());

            if (fireworkMeta.hasEffects()) {
                for (FireworkEffect effect : fireworkMeta.getEffects()) {
                    components.add("shape:" + effect.getType().toString().toLowerCase().replaceAll("_", ""));

                    components.add("flicker:" + effect.hasFlicker());
                    components.add("trail:" + effect.hasTrail());

                    List<String> colors = new ArrayList<String>();
                    if (effect.getColors() != null && effect.getColors().size() > 0) {
                        for (Color color : effect.getColors()) {
                            colors.add(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                        }
                        components.add("color:" + Util.implode(colors, ";"));
                    }

                    if (effect.getFadeColors() != null && effect.getFadeColors().size() > 0) {
                        colors.clear();
                        for (Color color : effect.getFadeColors()) {
                            colors.add(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                        }
                        components.add("fade:" + Util.implode(colors, ";"));
                    }
                }
            }
        }

        //Potion effects
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta)meta;
            if (potionMeta.hasCustomEffects()) {
                for (PotionEffect effect : potionMeta.getCustomEffects()) {
                    components.add(effect.getType().getName().toLowerCase().replaceAll("_", "") + ":" + effect.getDuration() + "." + effect.getAmplifier());
                }
            }
        }

        //DONE PARSING!
        this.string = Util.implode(components, " ");
    }


    /**
     * Get the parsed string.
     * Will return null if the parser failed.
     * @return Parsed string with all meta and such.
     */
    public String getString() {
        return string;
    }

    /**
     * Get the parsed item.
     * Will return null if the parser failed.
     * @return Parsed EItem with all meta and such.
     */
    public EItem getItem() {
        return item;
    }

    /**
     * Check if the parsing was successful or not.
     * If not you can call getError to get the error message.
     * @return Whether or not the parsing was successful.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * If the parsing wasn't successful this will return the error message.
     * @return Error message if there is one otherwise an empty string.
     */
    public String getError() {
        return error;
    }
}