package com.electrifiedded.villagermincer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Food that always applies its (bad) potion effects; cans leave an empty can behind. */
public class ItemMeatCan extends ItemFood {
    private final List<PotionEffect> effects = new ArrayList<>();
    private final boolean isCan;
    private float explosion;

    public ItemMeatCan(int amount, float saturation, boolean isCan) {
        super(amount, saturation, false);
        this.isCan = isCan;
        setAlwaysEdible();
        if (isCan) setMaxStackSize(1);
    }

    public ItemMeatCan addEffects(PotionEffect... list) {
        for (PotionEffect e : list) effects.add(e);
        return this;
    }

    public ItemMeatCan setExplosion(float size) {
        explosion = size;
        return this;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (world.isRemote) return;
        if (explosion > 0) world.createExplosion(null, player.posX, player.posY, player.posZ, explosion, true);
        for (PotionEffect e : effects) player.addPotionEffect(new PotionEffect(e));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack result = super.onItemUseFinish(stack, world, entity);
        if (isCan && result.isEmpty()) return new ItemStack(VillagerMincer.emptyCan);
        return result;
    }
}
