package com.zenovka.harderfarming;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

/** Registry-name matching; optional @metadata suffix, wildcard by default. */
public final class RecoveryItems {
    private static final List<Entry> entries = new ArrayList<Entry>();
    private RecoveryItems() {}

    public static void configure(String[] names) {
        entries.clear();
        for (String raw : names) {
            String value = raw.trim();
            if (value.isEmpty()) continue;
            int at = value.lastIndexOf('@');
            int meta = -1;
            if (at >= 0) {
                try { meta = Integer.parseInt(value.substring(at + 1)); }
                catch (NumberFormatException e) { continue; }
                value = value.substring(0, at);
            }
            if (value.indexOf(':') < 1) continue;
            entries.add(new Entry(value, meta));
        }
    }

    public static boolean matches(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        Object key = Item.itemRegistry.getNameForObject(stack.getItem());
        if (key == null) return false;
        String name = key.toString();
        for (Entry entry : entries)
            if (entry.name.equals(name) && (entry.meta == -1 || entry.meta == stack.getItemDamage()))
                return true;
        return false;
    }

    private static final class Entry {
        final String name;
        final int meta;
        Entry(String name, int meta) { this.name = name; this.meta = meta; }
    }
}
