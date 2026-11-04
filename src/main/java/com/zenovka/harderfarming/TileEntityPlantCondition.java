package com.zenovka.harderfarming;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public final class TileEntityPlantCondition extends TileEntity {
    public static final byte SICK = 1, DEAD = 2;
    public String originalBlock = "";
    public int originalMetadata = 0;
    public int originalRenderType = 6;
    public byte condition = DEAD;
    public float health = 0;
    public int conditionAge = 0;

    public void capture(Block block, int metadata) {
        Object name = Block.blockRegistry.getNameForObject(block);
        originalBlock = name == null ? "" : name.toString();
        originalMetadata = metadata;
        originalRenderType = block.getRenderType();
        condition = DEAD;
        health = 0;
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
        tag.setByte("Condition", condition);
        tag.setFloat("Health", health);
        tag.setInteger("ConditionAge", conditionAge);
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
