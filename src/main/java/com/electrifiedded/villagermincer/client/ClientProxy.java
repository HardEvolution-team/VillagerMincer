package com.electrifiedded.villagermincer.client;

import com.electrifiedded.villagermincer.CommonProxy;
import com.electrifiedded.villagermincer.VillagerMincer;
import com.electrifiedded.villagermincer.entity.EntityEmeraldBullet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        RenderingRegistry.registerEntityRenderingHandler(EntityEmeraldBullet.class,
                m -> new RenderSnowball<>(m, Items.EMERALD, Minecraft.getMinecraft().getRenderItem()));
    }

    @SubscribeEvent
    public void models(ModelRegistryEvent e) {
        for (Item item : new Item[]{Item.getItemFromBlock(VillagerMincer.superMincer), VillagerMincer.strangeMeat, VillagerMincer.meatCan,
                VillagerMincer.emptyCan, VillagerMincer.emeraldCan, VillagerMincer.emeraldGun, VillagerMincer.superEmeraldCan}) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
