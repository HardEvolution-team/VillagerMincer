package com.electrifiedded.villagermincer;

import com.electrifiedded.archaiclib.legacy.world.LegacyParticles;
import com.electrifiedded.villagermincer.entity.EntityEmeraldBullet;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/** A bow that shoots emeralds. */
public class ItemEmeraldGun extends ItemBow {
    private static boolean hasEmerald(EntityPlayer p) {
        for (ItemStack s : p.inventory.mainInventory) if (s.getItem() == Items.EMERALD) return true;
        return p.getHeldItemOffhand().getItem() == Items.EMERALD;
    }

    private static void consumeEmerald(EntityPlayer p) {
        if (p.getHeldItemOffhand().getItem() == Items.EMERALD) {
            p.getHeldItemOffhand().shrink(1);
            return;
        }
        for (ItemStack s : p.inventory.mainInventory) {
            if (s.getItem() == Items.EMERALD) {
                s.shrink(1);
                return;
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.capabilities.isCreativeMode || hasEmerald(player)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        boolean free = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        if (!free && !hasEmerald(player)) return;

        float f = (getMaxItemUseDuration(stack) - timeLeft) / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f < 0.1F) return;
        if (f > 1.0F) f = 1.0F;

        if (!world.isRemote) {
            EntityEmeraldBullet bullet = new EntityEmeraldBullet(world, player);
            bullet.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 7.5F, 1.0F);
            if (f == 1.0F) bullet.setIsCritical(true);
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            if (power > 0) bullet.setDamage(bullet.getDamage() + power * 0.5 + 0.5);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
            if (punch > 0) bullet.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) bullet.setFire(100);
            stack.damageItem(1, player);
            bullet.pickupStatus = free ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.DISALLOWED;
            world.spawnEntity(bullet);
            for (int i = 0; i < 5; i++) {
                LegacyParticles.spawnBroadcast(world, "angryVillager",
                        player.posX + (itemRand.nextFloat() * 2 - 1) * player.width,
                        player.posY + itemRand.nextFloat() * player.height,
                        player.posZ + (itemRand.nextFloat() * 2 - 1) * player.width, 0, 0, 0);
            }
        }
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        if (!free) consumeEmerald(player);
    }
}
