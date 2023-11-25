package dev.anhcraft.schematicfixer;

import dev.anhcraft.schematicfixer.internal.Helper;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Reading schematic file...");
        var root = (CompoundTag) NBTUtil.read("Example.schem").getTag();

        //Scan sign, given offset coordinates
        //Helper.scanSign(root, Position.of(1993, 1, 1003));
        System.out.println("Parsing schematic data...");

        // Read block data
        var schem = Helper.readBlockData(root, 0, 0, 0);

        // Rewrite materials & try to downgrade version
        schem.replaceMaterial("minecraft:deepslate_brick_wall", "minecraft:stone_brick_wall", true);
        schem.replaceMaterial("minecraft:mud", "minecraft:gray_terracotta", false);
        schem.removeMaterial("minecraft:light");
        schem.replaceMaterial("minecraft:cobbled_deepslate", "minecraft:basalt", false);
        schem.replaceMaterial("minecraft:polished_deepslate", "minecraft:basalt", false);
        schem.replaceMaterial("minecraft:calcite", "minecraft:white_concrete", false);
        schem.replaceMaterial("minecraft:raw_gold_block", "minecraft:gold_block", false);
        schem.removeMaterial("minecraft:black_candle");
        schem.removeMaterial("minecraft:red_candle");
        schem.replaceMaterial("minecraft:ochre_froglight", "minecraft:shroomlight", false);
        schem.replaceMaterial("minecraft:deepslate_iron_ore", "minecraft:iron_ore", false);
        schem.replaceMaterial("minecraft:deepslate_tile_stairs", "minecraft:polished_blackstone_brick_stairs", true);
        schem.replaceMaterial("minecraft:deepslate_copper_ore", "minecraft:iron_ore", false);
        schem.replaceMaterial("minecraft:cobbled_deepslate_stairs", "minecraft:stone_brick_stairs", true);
        schem.replaceMaterial("minecraft:mangrove_planks", "minecraft:acacia_planks", false);
        schem.removeMaterial("minecraft:brown_candle");
        schem.replaceMaterial("minecraft:deepslate", "minecraft:cobblestone", false);
        schem.replaceMaterial("minecraft:tuff", "minecraft:clay", false);
        schem.replaceMaterial("minecraft:packed_mud", "minecraft:gray_terracotta", false);
        schem.removeMaterial("minecraft:frogspawn");
        schem.replaceMaterial("minecraft:deepslate_tile_slab", "minecraft:blackstone_slab", true);
        schem.replaceMaterial("minecraft:deepslate_brick_slab", "minecraft:polished_blackstone_slab", true);
        schem.replaceMaterial("minecraft:mud_brick_wall", "minecraft:polished_blackstone_brick_wall", true);
        schem.replaceMaterial("minecraft:deepslate_brick_stairs", "minecraft:blackstone_stairs", true);
        schem.replaceMaterial("minecraft:deepslate_bricks", "minecraft:polished_blackstone", false);
        schem.replaceMaterial("minecraft:cobbled_deepslate_slab", "minecraft:polished_blackstone_slab", true);

        // Save schematic data
        System.out.println("Saving schematic data...");
        Helper.writeBlockData(root, schem, 0, 0, 0);

        // Save schematic file
        System.out.println("Writing schematic file...");
        NBTUtil.write(new NamedTag("Schematic", root), "Example.schem");
    }
}