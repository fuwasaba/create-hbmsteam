package com.keikun1215.createhbmsteam.block.entity;

import com.keikun1215.createhbmsteam.CreateHBMSteam;
import com.keikun1215.createhbmsteam.block.AddonBlocks;
import com.keikun1215.createhbmsteam.block.entity.renderer.SteamTurbineRenderer;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class AddonBlockEntities {
    private static final CreateRegistrate REGISTRATE = CreateHBMSteam.registrate();
    public static final BlockEntityEntry<SteamTurbineBlockEntity> steamTurbineBlockEntity = REGISTRATE
            .<SteamTurbineBlockEntity>blockEntity("steam_turbine", SteamTurbineBlockEntity::new)
            .instance(() -> ShaftInstance::new, true)
            .validBlock(AddonBlocks.steamTurbine)
            .renderer(() -> SteamTurbineRenderer::new)
            .register();

    public static void register() {}
}
