package com.electrifiedded.villagermincer;

import com.electrifiedded.archaiclib.original.OriginalModAssets;
import com.electrifiedded.villagermincer.entity.EntityEmeraldBullet;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Port of VillagerMincer 1.6.2 (jx) to 1.12.2. */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.40.0,)")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class VillagerMincer {
    public static final Logger LOG = LogManager.getLogger(Reference.MOD_ID);

    public static final CreativeTabs TAB = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(superMincer);
        }
    };

    public static Block superMincer;
    public static ItemMeatCan strangeMeat, meatCan, emeraldCan, superEmeraldCan;
    public static Item emptyCan, emeraldGun;

    @Mod.Instance(Reference.MOD_ID)
    public static VillagerMincer instance;

    public static final String ORIGINAL_NAME = "Villager Mincer for 1.6.x (by _jx)";
    public static final String ORIGINAL_URL = "https://www.9minecraft.net/villager-mincer-mod/";

    public VillagerMincer() {
        // textures come from the player's own copy of the original mod (archaic_originals/), never bundled
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/emerald_can.png", "textures/items/emeraldCan.png");
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/emerald_gun.png", "textures/items/emeraldGun.png");
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/empty_can.png", "textures/items/emptyCan.png");
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/meat_can.png", "textures/items/meatCan.png");
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/strange_meat.png", "textures/items/meat.png");
        OriginalModAssets.alias(Reference.MOD_ID, "textures/items/super_emerald_can.png", "textures/items/superEmeraldCan.png");
    }

    @SidedProxy(clientSide = "com.electrifiedded.villagermincer.client.ClientProxy", serverSide = "com.electrifiedded.villagermincer.CommonProxy")
    public static CommonProxy proxy;

    private static <T extends Item> T named(T item, String name) {
        item.setRegistryName(Reference.MOD_ID, name);
        item.setTranslationKey(Reference.MOD_ID + "." + name);
        item.setCreativeTab(TAB);
        return item;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {
        superMincer = new BlockSuperMincer();
        e.getRegistry().register(superMincer);
        GameRegistry.registerTileEntity(TileEntitySuperMincer.class, new ResourceLocation(Reference.MOD_ID, "super_mincer"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        // Strange Meat stacks to 64; the cans are single items that leave an empty can behind.
        strangeMeat = named(new ItemMeatCan(4, 0.1F, false), "strange_meat");
        strangeMeat.addEffects(new PotionEffect(MobEffects.HUNGER, 600, 1), new PotionEffect(MobEffects.NAUSEA, 600, 0));

        meatCan = named(new ItemMeatCan(6, 0.1F, true), "meat_can");
        meatCan.addEffects(new PotionEffect(MobEffects.HUNGER, 1200, 2), new PotionEffect(MobEffects.NAUSEA, 1200, 1));

        emptyCan = named(new Item(), "empty_can");

        emeraldCan = named(new ItemMeatCan(6, 0.1F, true), "emerald_can");
        emeraldCan.setPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60 * 20, 0), 1.0F);
        emeraldCan.addEffects(new PotionEffect(MobEffects.HUNGER, 1200, 2), new PotionEffect(MobEffects.NAUSEA, 1200, 1),
                new PotionEffect(MobEffects.POISON, 1200, 1));

        emeraldGun = named(new ItemEmeraldGun(), "emerald_gun");

        // 120 food, a 10-block smoking explosion centred on the eater and heavy poison.
        superEmeraldCan = named(new ItemMeatCan(120, 0.1F, true), "super_emerald_can");
        superEmeraldCan.setPotionEffect(new PotionEffect(MobEffects.POISON, 600 * 20, 50), 5.0F);
        superEmeraldCan.setExplosion(10.0F);

        e.getRegistry().registerAll(strangeMeat, meatCan, emptyCan, emeraldCan, emeraldGun, superEmeraldCan);
        e.getRegistry().register(new ItemBlock(superMincer).setRegistryName(superMincer.getRegistryName()));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        OriginalModAssets.require(Reference.MOD_ID, "VillagerMincer", ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "emerald_bullet"), EntityEmeraldBullet.class,
                "emerald_bullet", 0, instance, 128, 1, true);
        proxy.preInit();
    }
}
