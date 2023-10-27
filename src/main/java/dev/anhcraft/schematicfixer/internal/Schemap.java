package dev.anhcraft.schematicfixer.internal;

import dev.anhcraft.jvmkit.utils.IOUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Schemap {
    private static final Set<String> VALID_MATERIALS = new HashSet<>();

    static {
        try {
            for (String s : new String(IOUtil.readResource(Helper.class, "/material-1.16.txt"), StandardCharsets.UTF_8).split("\\n")) {
                VALID_MATERIALS.add(s.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<Position, String> population = new HashMap<>();
    private final Set<String> palette = new HashSet<>();

    public void populate(Position pos, String state) {
        this.population.put(pos, state);
    }

    public String locate(Position pos) {
        return this.population.get(pos);
    }

    public void validate() {
        Set<String> invalids = new HashSet<>();
        for (String t : palette) {
            if (!VALID_MATERIALS.contains(t.split("\\[")[0])) {
                invalids.add(t);
            }
        }
        for (String k : invalids) {
            System.out.println("Invalid state: " + k);
        }
    }

    public void searchByMaterial(String material) {
        for (Map.Entry<Position, String> ent : population.entrySet()) {
            if (ent.getValue().split("\\[")[0].toLowerCase().contains(material.toLowerCase())) {
                System.out.println("Found: " + ent.getValue() + " at " + ent.getKey());
            }
        }
    }

    public void searchByState(String state) {
        for (Map.Entry<Position, String> ent : population.entrySet()) {
            if (ent.getValue().toLowerCase().contains(state.toLowerCase())) {
                System.out.println("Found: " + ent.getValue() + " at " + ent.getKey());
            }
        }
    }

    public void removeMaterial(String from) {
        replaceMaterial(from, "minecraft:air", false);
    }

    public void replaceMaterial(String from, String to, boolean keepData) {
        var i = 0;
        for (Map.Entry<Position, String> ent : population.entrySet()) {
            var args = ent.getValue().split("\\[");
            if (args[0].equalsIgnoreCase(from)) {
                ent.setValue(to.toLowerCase()+(args.length > 1 && args[1].endsWith("]") && keepData ? "[" + args[1] : ""));
                i++;
            }
        }
        System.out.printf("Merged %s into %s at %d positions%n", from, to, i);
        palette.remove(from);
    }
}
