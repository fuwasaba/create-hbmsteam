package com.keikun1215.createhbmsteam;

import com.keikun1215.createhbmsteam.block.entity.renderer.AddonPartialModels;
import com.mojang.logging.LogUtils;
import com.keikun1215.createhbmsteam.block.AddonBlocks;
import com.keikun1215.createhbmsteam.item.AddonItems;
import com.keikun1215.createhbmsteam.block.entity.AddonBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateHBMSteam.MODID)
public class CreateHBMSteam {
    public static final String MODID = "createhbmsteam";
    private static final CreateRegistrate registrate = CreateRegistrate.create(MODID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab itemGroup = new CreativeModeTab(MODID) {
        @Override
        @NotNull
        public ItemStack makeIcon() {
            return new ItemStack(AddonBlocks.steamTurbine.get());
        }
    };

    public CreateHBMSteam()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AddonBlocks.register();
        AddonItems.register(eventBus);
        AddonBlockEntities.register();
        registrate().registerEventListeners(eventBus);
        AddonPartialModels.init();
        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(AddonBlocks.steamTurbine.get(), RenderType.cutout());
    }

    public static CreateRegistrate registrate() {
        return registrate;
    }
}
