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
    @Override public Item getItemDropped(int meta, Random rand, int fortune) { return null; }
    @Override public int quantityDropped(Random rand) { return 0; }
}
