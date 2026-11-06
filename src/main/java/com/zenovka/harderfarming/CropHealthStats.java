package com.zenovka.harderfarming;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/** Per-dimension cumulative illness episode counters, persisted with the world. */
public final class CropHealthStats extends WorldSavedData {
    private static final String KEY = "harderfarming.cropHealthStats";
    public long illnesses, recovered, died, treated, boosted, boostedRecovered, boostedDied;

    public CropHealthStats() { super(KEY); }
    public CropHealthStats(String name) { super(name); }

    public static CropHealthStats get(World world) {
        CropHealthStats data = (CropHealthStats)world.perWorldStorage.loadData(CropHealthStats.class, KEY);
        if (data == null) {
            data = new CropHealthStats(KEY);
            world.perWorldStorage.setData(KEY, data);
        }
        return data;
    }

    public void illness() { illnesses++; markDirty(); }
    public void recovery() { recovered++; markDirty(); }
    public void boost() { boosted++; markDirty(); }
    public void boostedRecovery() { boostedRecovered++; markDirty(); }
    public void boostedDeath() { boostedDied++; markDirty(); }
    public void treatment() { treated++; markDirty(); }
    public void death() { died++; markDirty(); }

    @Override public void readFromNBT(NBTTagCompound tag) {
        illnesses = tag.getLong("Illnesses");
        recovered = tag.getLong("Recovered");
        died = tag.getLong("Died");
        treated = tag.getLong("Treated");
        boosted = tag.getLong("Boosted");
        boostedRecovered = tag.getLong("BoostedRecovered");
        boostedDied = tag.getLong("BoostedDied");
    }
    @Override public void writeToNBT(NBTTagCompound tag) {
        tag.setLong("Illnesses", illnesses);
        tag.setLong("Recovered", recovered);
        tag.setLong("Died", died);
        tag.setLong("Treated", treated);
        tag.setLong("Boosted", boosted);
        tag.setLong("BoostedRecovered", boostedRecovered);
        tag.setLong("BoostedDied", boostedDied);
    }
}
