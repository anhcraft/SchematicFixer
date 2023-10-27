package dev.anhcraft.schematicfixer.internal;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.Tag;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static void scanSign(CompoundTag root) {
        for (Tag<?> t : root.getListTag("BlockEntities")) {
            if (t instanceof CompoundTag tag && tag.getString("Id").contains("sign")) {
                System.out.println(Arrays.toString(tag.getIntArrayTag("Pos").getValue()));
                System.out.println("- " + tag.getString("Text1"));
                System.out.println("- " + tag.getString("Text2"));
                System.out.println("- " + tag.getString("Text3"));
                System.out.println("- " + tag.getString("Text4"));
            }
        }
    }

    public static Schemap readBlockData(CompoundTag root, int offsetX, int offsetY, int offsetZ) {
        int width = root.getShort("Width") & 0xFFFF;
        //int height = root.getShort("Height") & 0xFFFF;
        int length = root.getShort("Length") & 0xFFFF;

        if (root.containsKey("Metadata")) {
            offsetX += root.getCompoundTag("Metadata").getInt("WEOffsetX");
            offsetY += root.getCompoundTag("Metadata").getInt("WEOffsetY");
            offsetZ += root.getCompoundTag("Metadata").getInt("WEOffsetZ");
        }

        var palette = root.getCompoundTag("Palette");
        Map<Integer, String> paletteMap = new HashMap<>();
        for (Map.Entry<String, Tag<?>> t : palette.entrySet()) {
            paletteMap.put(((IntTag) t.getValue()).asInt(), t.getKey());
        }

        var schem = new Schemap();

        var blocks = root.getByteArray("BlockData");
        System.out.println("Loading " + blocks.length + " blocks...");
        int index = 0;
        for (VarIntIterator iter = new VarIntIterator(blocks); iter.hasNext(); index++) {
            int nextBlockId = iter.nextInt();
            String state = paletteMap.get(nextBlockId);
            int[] position = decodePositionFromDataIndex(width, length, index);
            position[0] += offsetX;
            position[1] += offsetY;
            position[2] += offsetZ;
            schem.populate(Position.of(position), state);
            if (index % 1e6 == 0) {
                System.out.printf("Loading %.2f%%...%n", index / ((double) blocks.length) * 100d);
            }
        }

        System.out.println("Loaded " + index + " blocks");

        return schem;
    }

    public static void writeBlockData(CompoundTag root, Schemap schem, int offsetX, int offsetY, int offsetZ) {
        int width = root.getShort("Width") & 0xFFFF;
        int height = root.getShort("Height") & 0xFFFF;
        int length = root.getShort("Length") & 0xFFFF;

        if (root.containsKey("Metadata")) {
            offsetX += root.getCompoundTag("Metadata").getInt("WEOffsetX");
            offsetY += root.getCompoundTag("Metadata").getInt("WEOffsetY");
            offsetZ += root.getCompoundTag("Metadata").getInt("WEOffsetZ");
        }

        Map<String, Integer> palette = new HashMap<>();
        CompoundTag paletteTag = new CompoundTag();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(width * height * length);
        System.out.println("Saving " + (width*height*length) + " blocks...");

        int paletteMax = 0;
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    var p = new Position(offsetX + x, offsetY + y, offsetZ + z);
                    var state = schem.locate(p);
                    if (state == null) {
                        System.out.println("Missing state at " + p);
                        state = "minecraft:air";
                    }

                    int blockId;
                    if (palette.containsKey(state)) {
                        blockId = palette.get(state);
                    } else {
                        blockId = paletteMax;
                        palette.put(state, blockId);
                        paletteTag.put(state, new IntTag(blockId));
                        paletteMax++;
                    }

                    while ((blockId & -128) != 0) {
                        buffer.write(blockId & 127 | 128);
                        blockId >>>= 7;
                    }
                    buffer.write(blockId);
                    if (count % 1e6 == 0) {
                        System.out.printf("Saving %.2f%%...%n", count / ((double) width*height*length) * 100d);
                    }
                    count++;
                }
            }
        }

        root.putInt("PaletteMax", paletteMax);
        root.put("Palette", paletteTag);
        root.putByteArray("BlockData", buffer.toByteArray());

        System.out.println("Written " + (width*height*length) + " blocks, palette's size is " + paletteMax);
    }

    private static int[] decodePositionFromDataIndex(int width, int length, int index) {
        // index = (y * width * length) + (z * width) + x
        int y = index / (width * length);
        int remainder = index - (y * width * length);
        int z = remainder / width;
        int x = remainder - z * width;
        return new int[]{x, y, z};
    }
}
