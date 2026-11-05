package com.zenovka.harderfarming;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public final class TileEntityPlantCondition extends TileEntity {
    public static final byte SICK = 1, DEAD = 2;
    public String originalBlock = "";
    public int originalMetadata = 0;
    public int originalRenderType = 6;
    public float minX = .15f, minY = 0f, minZ = .15f;
    public float maxX = .85f, maxY = 1f, maxZ = .85f;
    public byte condition = DEAD;
    public float health = 0;
    public int conditionAge = 0;
    private boolean outcomeRecovery = false;

    public void capture(Block block, int metadata) {
        Object name = Block.blockRegistry.getNameForObject(block);
        originalBlock = name == null ? "" : name.toString();
        originalMetadata = metadata;
        originalRenderType = block.getRenderType();
        condition = DEAD;
        health = 0;
        conditionAge = 0;
        markDirty();
    }

    /** Called once when a sampled healthy crop enters illness. */
    public void beginSickness() {
        if (worldObj == null || worldObj.isRemote) return;
        condition = SICK;
        health = 1.0f;
        conditionAge = 0;
        outcomeRecovery = worldObj.rand.nextDouble() < HarderFarming.naturalRecoveryChance;
        CropHealthStats.get(worldObj).illness();
        markDirty();
    }

    /** A loaded sick crop advances in simulation ticks, not scaled day/night time. */
    @Override public void updateEntity() {
        if (worldObj == null || worldObj.isRemote || condition != SICK) return;
        conditionAge++;
        if (conditionAge < HarderFarming.sicknessDurationMinutes * 60 * 20) {
            if (conditionAge % 200 == 0) markDirty();
            return;
        }
        if (outcomeRecovery && recover()) {
            CropHealthStats.get(worldObj).recovery();
            return;
        }
        condition = DEAD;
        health = 0;
        CropHealthStats.get(worldObj).death();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public static float[] snapshotSelection(World world, int x, int y, int z, Block block) {
        AxisAlignedBB bb = block.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (bb == null) return new float[]{.15f, 0f, .15f, .85f, 1f, .85f};
        return new float[]{local(bb.minX - x), local(bb.minY - y), local(bb.minZ - z),
                local(bb.maxX - x), local(bb.maxY - y), local(bb.maxZ - z)};
    }
    private static float local(double value) { return (float)Math.max(0, Math.min(1, value)); }
    public void setSelection(float[] bounds) {
        minX = bounds[0]; minY = bounds[1]; minZ = bounds[2];
        maxX = bounds[3]; maxY = bounds[4]; maxZ = bounds[5];
        markDirty();
    }

    public Block original() {
        Object value = Block.blockRegistry.getObject(originalBlock);
        return value instanceof Block ? (Block)value : null;
    }

    public boolean recover() {
        if (worldObj == null || worldObj.isRemote || condition != SICK) return false;
        Block block = original();
        if (block == null || block == HarderFarming.witheredCrop) return false;
        return worldObj.setBlock(xCoord, yCoord, zCoord, block, originalMetadata, 3);
    }

    @Override public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("OriginalBlock", originalBlock);
        tag.setInteger("OriginalMetadata", originalMetadata);
        tag.setInteger("OriginalRenderType", originalRenderType);
        tag.setFloat("MinX", minX); tag.setFloat("MinY", minY); tag.setFloat("MinZ", minZ);
        tag.setFloat("MaxX", maxX); tag.setFloat("MaxY", maxY); tag.setFloat("MaxZ", maxZ);
        tag.setByte("Condition", condition);
        tag.setFloat("Health", health);
        tag.setInteger("ConditionAge", conditionAge);
        tag.setBoolean("OutcomeRecovery", outcomeRecovery);
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        originalBlock = tag.getString("OriginalBlock");
        originalMetadata = tag.getInteger("OriginalMetadata");
        Block block = original();
        originalRenderType = tag.hasKey("OriginalRenderType")
                ? tag.getInteger("OriginalRenderType") : (block == null ? 6 : block.getRenderType());
        condition = tag.hasKey("Condition") ? tag.getByte("Condition") : DEAD;
        health = tag.getFloat("Health");
        conditionAge = tag.getInteger("ConditionAge");
        outcomeRecovery = tag.getBoolean("OutcomeRecovery");
        if (tag.hasKey("MaxY")) {
            minX = tag.getFloat("MinX"); minY = tag.getFloat("MinY"); minZ = tag.getFloat("MinZ");
            maxX = tag.getFloat("MaxX"); maxY = tag.getFloat("MaxY"); maxZ = tag.getFloat("MaxZ");
        }
    }
    @Override public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }
    @Override public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
