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
    public byte condition = DEAD;
    public float health = 0;
    public int conditionAge = 0;
    public int originalRenderType = 6;
    public float minX = .25f, minY = 0f, minZ = .25f;
    public float maxX = .75f, maxY = .25f, maxZ = .75f;

    public void capture(Block block, int metadata) {
        Object name = Block.blockRegistry.getNameForObject(block);
        originalBlock = name == null ? "" : name.toString();
        originalMetadata = metadata;
        condition = DEAD;
        health = 0;
        markDirty();
    }

    /** Snapshot the original selection box while the original crop still occupies the world. */
    public static float[] snapshotBounds(World world, int x, int y, int z, Block block) {
        AxisAlignedBB bb = block.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (bb == null) return new float[]{.25f, 0f, .25f, .75f, .25f, .75f};
        return new float[]{clamp(bb.minX - x), clamp(bb.minY - y), clamp(bb.minZ - z),
                clamp(bb.maxX - x), clamp(bb.maxY - y), clamp(bb.maxZ - z)};
    }
    private static float clamp(double v) { return (float)Math.max(0, Math.min(1, v)); }
    public void captureGeometry(float[] bounds, int renderType) {
        minX = bounds[0]; minY = bounds[1]; minZ = bounds[2];
        maxX = bounds[3]; maxY = bounds[4]; maxZ = bounds[5];
        originalRenderType = renderType;
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
        tag.setByte("Condition", condition);
        tag.setFloat("Health", health);
        tag.setInteger("ConditionAge", conditionAge);
        tag.setInteger("OriginalRenderType", originalRenderType);
        tag.setFloat("MinX", minX); tag.setFloat("MinY", minY); tag.setFloat("MinZ", minZ);
        tag.setFloat("MaxX", maxX); tag.setFloat("MaxY", maxY); tag.setFloat("MaxZ", maxZ);
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        originalBlock = tag.getString("OriginalBlock");
        originalMetadata = tag.getInteger("OriginalMetadata");
        condition = tag.hasKey("Condition") ? tag.getByte("Condition") : DEAD;
        health = tag.getFloat("Health");
        conditionAge = tag.getInteger("ConditionAge");
        if (tag.hasKey("MaxY")) {
            originalRenderType = tag.getInteger("OriginalRenderType");
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
