package dev.anhcraft.schematicfixer.internal;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public class VarIntIterator implements PrimitiveIterator.OfInt {

    private final byte[] source;
    private int index;
    private boolean hasNextInt;
    private int nextInt;

    public VarIntIterator(byte[] source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        if (hasNextInt) {
            return true;
        }
        if (index >= source.length) {
            return false;
        }

        nextInt = readNextInt();
        return hasNextInt = true;
    }

    private int readNextInt() {
        int value = 0;
        for (int bitsRead = 0; ; bitsRead += 7) {
            if (index >= source.length) {
                throw new IllegalStateException("Ran out of bytes while reading VarInt (probably corrupted data)");
            }
            byte next = source[index];
            index++;
            value |= (next & 0x7F) << bitsRead;
            if (bitsRead > 7 * 5) {
                throw new IllegalStateException("VarInt too big (probably corrupted data)");
            }
            if ((next & 0x80) == 0) {
                break;
            }
        }
        return value;
    }

    @Override
    public int nextInt() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        hasNextInt = false;
        return nextInt;
    }
}