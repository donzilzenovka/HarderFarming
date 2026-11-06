package com.zenovka.harderfarming;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

/** Registry-name matching; optional @metadata suffix, wildcard by default. */
public final class RecoveryItems {
    private static final List<Entry> instant = new ArrayList<Entry>();
    private static final List<Entry> boost = new ArrayList<Entry>();
    private RecoveryItems() {}

    public static void configure(String[] instantNames, String[] boostNames) {
        instant.clear();
        boost.clear();
        parse(instantNames, instant);
        parse(boostNames, boost);
    }

    private static void parse(String[] names, List<Entry> entries) {
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

    public static boolean matchesInstant(ItemStack stack) { return matches(stack, instant); }
    public static boolean matchesBoost(ItemStack stack) { return matches(stack, boost); }

    private static boolean matches(ItemStack stack, List<Entry> entries) {
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
