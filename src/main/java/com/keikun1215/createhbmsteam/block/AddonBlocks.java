package com.keikun1215.createhbmsteam.block;

import com.keikun1215.createhbmsteam.CreateHBMSteam;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;

public class AddonBlocks {
    private static final CreateRegistrate REGISTRATE = CreateHBMSteam.registrate().creativeModeTab(
            () -> CreateHBMSteam.itemGroup
    );
    public static final BlockEntry<SteamTurbineBlock> steamTurbine = REGISTRATE.block("steam_turbine", SteamTurbineBlock::new)
            .simpleItem()
            .register();
    public static void register() {
    }
}
