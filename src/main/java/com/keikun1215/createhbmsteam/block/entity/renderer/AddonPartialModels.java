package com.keikun1215.createhbmsteam.block.entity.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.keikun1215.createhbmsteam.CreateHBMSteam;
import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;

public class AddonPartialModels {
    public static final PartialModel
            STEAM_TURBINE_BLOCK = block("steam_turbine"),
            STEAM_TURBINE_BLADE = block("steam_turbine_blade");
    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(CreateHBMSteam.MODID, "block/" + path));
    }
    public static void init() {}
}
