package com.electrifiedded.villagermincer;

import com.electrifiedded.archaiclib.legacy.world.LegacyParticles;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.Random;

/**
 * Works only on the upper mincer of a stack (a mincer below it is required). Holds one villager on top, spins it
 * and sinks it into the machine, dropping Strange Meat (sometimes an emerald) below the stack.
 * Server-side only: the villager is simply pinned in place, nothing is stored in the villager's NBT.
 */
public class TileEntitySuperMincer extends TileEntity implements ITickable {
    private final Random rnd = new Random();
    private EntityVillager villager;
    private int runTime;
    private int damageCount;

    public void catchVillager(EntityVillager v) {
        if (villager == null) {
            villager = v;
            runTime = 0;
            damageCount = 0;
        }
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (world.getBlockState(pos.down()).getBlock() != VillagerMincer.superMincer) {
            release();
            return;
        }
        if (villager != null) {
            if (villager.isDead || villager.getHealth() <= 0 || villager.world != world) {
                release();
            } else {
                runTime++;
                hold();
            }
        } else {
            villager = findVillager();
            runTime = 0;
            damageCount = 0;
        }
        if (villager != null && runTime > 20) startMince();
    }

    /** Pin the villager on top of the stack, spinning and sinking once the machine is running. */
    private void hold() {
        float sink = runTime > 20 ? runTime / 80.0F : 0.0F;
        villager.noClip = true;
        villager.motionX = villager.motionY = villager.motionZ = 0;
        villager.fallDistance = 0;
        float yaw = runTime > 20 ? villager.rotationYaw + 45.0F : villager.rotationYaw;
        villager.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1.0 - sink, pos.getZ() + 0.5, yaw, 0.0F);
        villager.renderYawOffset = yaw;
        villager.rotationYawHead = yaw;
        if (runTime > 5 && rnd.nextInt(4) == 0) {
            LegacyParticles.spawnBroadcast(world, "lava", pos.getX() + rnd.nextFloat(), pos.getY() + 2.0,
                    pos.getZ() + rnd.nextFloat(), 0, 0, 0);
        }
    }

    private void release() {
        if (villager != null) villager.noClip = false;
        villager = null;
        runTime = 0;
        damageCount = 0;
    }

    private EntityVillager findVillager() {
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ())
                .grow(1.5, 1.0, 1.5);
        List<EntityVillager> list = world.getEntitiesWithinAABB(EntityVillager.class, box);
        for (EntityVillager v : list) {
            if (!v.isDead && v.getHealth() > 0) return v;
        }
        return null;
    }

    private void startMince() {
        damageCount++;
        if (damageCount == 5) {
            double x = pos.getX() + 0.5, y = pos.getY() - 1.5, z = pos.getZ() + 0.5;
            EntityItem meat = new EntityItem(world, x, y, z, new ItemStack(VillagerMincer.strangeMeat));
            meat.motionX = meat.motionY = meat.motionZ = 0;
            world.spawnEntity(meat);
            if (rnd.nextInt(3) == 0) {
                EntityItem em = new EntityItem(world, x, y, z, new ItemStack(Items.EMERALD));
                em.motionX = em.motionY = em.motionZ = 0;
                world.spawnEntity(em);
            }
            villager.attackEntityFrom(DamageSource.CACTUS, 2.0F);
            damageCount = 0;
        }
    }

    @Override
    public void invalidate() {
        release();
        super.invalidate();
    }
}
