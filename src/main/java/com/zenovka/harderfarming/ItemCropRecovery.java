package com.zenovka.harderfarming;

import net.minecraft.item.Item;

/** Simple native treatment item; other mods' items can be configured as alternatives. */
public final class ItemCropRecovery extends Item {
    public ItemCropRecovery() {
        setUnlocalizedName("harderfarming.cropRecovery");
        setTextureName("minecraft:slime_ball");
        setMaxStackSize(64);
    }
}
