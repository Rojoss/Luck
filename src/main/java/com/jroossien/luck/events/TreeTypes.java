package com.jroossien.luck.events;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum TreeTypes {
    OAK(Material.LOG, Material.LEAVES, Arrays.asList((byte)0, (byte)4, (byte)8, (byte)12)),
    SPRUCE(Material.LOG, Material.LEAVES, Arrays.asList((byte)1, (byte)5, (byte)9, (byte)13)),
    BIRCH(Material.LOG, Material.LEAVES, Arrays.asList((byte)2, (byte)6, (byte)10, (byte)14)),
    JUNGLE(Material.LOG, Material.LEAVES, Arrays.asList((byte)3, (byte)7, (byte)11, (byte)15)),
    ACACIA(Material.LOG_2, Material.LEAVES_2, Arrays.asList((byte)0, (byte)4, (byte)8, (byte)12)),
    DARK_OAK(Material.LOG_2, Material.LEAVES_2, Arrays.asList((byte)1, (byte)5, (byte)9, (byte)13));

    public Material log;
    public Material leaf;
    public byte data;
    public List<Byte> dataTypes;

    TreeTypes(Material log, Material leaf, List<Byte> dataTypes) {
        this.log = log;
        this.leaf = leaf;
        this.dataTypes = dataTypes;
        this.data = dataTypes.get(0);
    }

}
