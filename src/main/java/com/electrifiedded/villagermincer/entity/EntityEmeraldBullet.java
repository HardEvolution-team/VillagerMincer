package com.electrifiedded.villagermincer.entity;

import com.electrifiedded.villagermincer.VillagerMincer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/** Emerald fired by the Emerald Rifle. A hit on a villager gibs it into Strange Meat. */
public class EntityEmeraldBullet extends EntityArrow {
    public EntityEmeraldBullet(World world) {
        super(world);
    }

    public EntityEmeraldBullet(World world, EntityLivingBase shooter) {
        super(world, shooter);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.EMERALD);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && inGround) setDead();
    }

    @Override
    protected void onHit(RayTraceResult hit) {
        Entity target = hit.entityHit;
        if (target instanceof EntityVillager) {
            if (!world.isRemote) {
                Entity shooter = shootingEntity;
                target.attackEntityFrom(shooter instanceof EntityLivingBase
                        ? DamageSource.causeMobDamage((EntityLivingBase) shooter) : DamageSource.GENERIC, 100.0F);
                world.createExplosion(shooter, posX, posY, posZ, 1.5F, false);
                dropLoot();
            }
            setDead();
            return;
        }
        super.onHit(hit);
    }

    private void dropLoot() {
        for (int i = 0; i < 20; i++) {
            EntityItem meat = new EntityItem(world, posX, posY, posZ, new ItemStack(VillagerMincer.strangeMeat));
            meat.motionX = meat.motionX * 5.0 + rand.nextGaussian();
            meat.motionY = meat.motionY * 5.0 + rand.nextGaussian();
            meat.motionZ = meat.motionZ * 5.0 + rand.nextGaussian();
            world.spawnEntity(meat);
            if (rand.nextInt(5) == 0) world.spawnEntity(new EntityItem(world, posX, posY, posZ, new ItemStack(Items.EMERALD)));
        }
    }
}
