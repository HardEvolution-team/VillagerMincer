package com.electrifiedded.villagermincer;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSuperMincer extends Block {
    public BlockSuperMincer() {
        super(Material.ROCK);
        setRegistryName(Reference.MOD_ID, "super_mincer");
        setTranslationKey(Reference.MOD_ID + ".super_mincer");
        setHardness(1.5F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);
        setSoundType(SoundType.STONE);
        setCreativeTab(VillagerMincer.TAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySuperMincer();
    }

    /** A villager walking onto the top of a mincer gets caught by it. */
    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityVillager) || entity.posY <= pos.getY()) return;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySuperMincer) ((TileEntitySuperMincer) tile).catchVillager((EntityVillager) entity);
    }

    /** Lava drips under the bottom mincer of a stack, as in the original. */
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rnd) {
        if (world.getBlockState(pos.down()).getBlock() == this) {
            world.spawnParticle(EnumParticleTypes.DRIP_LAVA, pos.getX() + rnd.nextFloat(), pos.getY() - 0.05,
                    pos.getZ() + rnd.nextFloat(), 0, 0, 0);
        }
    }
}
