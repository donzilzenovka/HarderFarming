package com.zenovka.harderfarming;

import net.minecraft.block.BlockBush;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import java.util.Random;

/** Persistent dead plant; the original sprite and render type are stored in its tile entity. */
public final class WitheredCrop extends BlockBush implements net.minecraft.block.ITileEntityProvider {
    private IIcon icon;
    public WitheredCrop() {
        super(Material.plants);
        setBlockName("harderfarming.witheredCrop");
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        setBlockBounds(.15F, 0F, .15F, .85F, 1F, .85F);
    }
    @Override public TileEntity createNewTileEntity(World world, int metadata) { return new TileEntityPlantCondition(); }
    @Override public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPlantCondition) {
            TileEntityPlantCondition plant = (TileEntityPlantCondition)tile;
            setBlockBounds(plant.minX, plant.minY, plant.minZ, plant.maxX, plant.maxY, plant.maxZ);
        } else {
            setBlockBounds(.15f, 0f, .15f, .85f, 1f, .85f);
        }
    }
    @Override public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(x + getBlockBoundsMinX(), y + getBlockBoundsMinY(), z + getBlockBoundsMinZ(),
                x + getBlockBoundsMaxX(), y + getBlockBoundsMaxY(), z + getBlockBoundsMaxZ());
    }
    @Override public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) { return null; }
    @Override public int getRenderType() { return -1; }
    @Override public boolean isOpaqueCube() { return false; }
    @Override public boolean renderAsNormalBlock() { return false; }
    @Override public void registerBlockIcons(IIconRegister registry) {
        icon = registry.registerIcon("minecraft:deadbush");
    }
    @Override public IIcon getIcon(int side, int meta) { return icon; }
    @Override public int colorMultiplier(IBlockAccess world, int x, int y, int z) { return 0x887B69; }
    @Override public int getRenderColor(int meta) { return 0x887B69; }
    @Override public int getBlockColor() { return 0x887B69; }
    @Override protected boolean canPlaceBlockOn(net.minecraft.block.Block ground) { return ground == Blocks.farmland; }
    @Override public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getCurrentEquippedItem();
        boolean instant = RecoveryItems.matchesInstant(held);
        boolean boost = !instant && RecoveryItems.matchesBoost(held);
        if (!instant && !boost) return false;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPlantCondition)
                || ((TileEntityPlantCondition)tile).condition != TileEntityPlantCondition.SICK) return false;
        TileEntityPlantCondition plant = (TileEntityPlantCondition)tile;
        if (boost && plant.recoveryBoosted) return false;
        if (world.isRemote) return true;
        if (instant) {
            if (!plant.recover()) return false;
            CropHealthStats.get(world).treatment();
        } else {
            if (!plant.boostRecovery()) return false;
            world.playAuxSFX(2005, x, y, z, 0); // Vanilla bonemeal particles
        }
        if (!player.capabilities.isCreativeMode) {
            held.stackSize--;
            if (held.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            player.inventory.markDirty();
        }
        return true;
    }
    @Override public Item getItemDropped(int meta, Random rand, int fortune) { return null; }
    @Override public int quantityDropped(Random rand) { return 0; }
}
