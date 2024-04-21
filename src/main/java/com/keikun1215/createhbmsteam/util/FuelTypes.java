package com.keikun1215.createhbmsteam.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class FuelTypes {
    public static TypesF TYPES = new TypesF();
    public static final FuelType STEAM = TYPES.add(new FuelType(new ResourceLocation("nucleartech", "steam"), 90, 80, new ResourceLocation("nucleartech", "spent_steam")));
    public static final FuelType DENSE_STEAM = TYPES.add(new FuelType(new ResourceLocation("nucleartech", "hot_steam"), 70, 50, STEAM));
    public static final FuelType SUPER_DENSE_STEAM = TYPES.add(new FuelType(new ResourceLocation("nucleartech", "super_hot_steam"), 40, 30, DENSE_STEAM));
    public static final FuelType ULTRA_DENSE_STEAM = TYPES.add(new FuelType(new ResourceLocation("nucleartech", "ultra_hot_steam"), 10, 20, SUPER_DENSE_STEAM));
    public static class TypesF {
        public List<FuelType> types = new ArrayList<>();
        public FuelType add(FuelType f) {
            types.add(f);
            return f;
        }
        public FuelType get(Fluid fluid) {
            FuelType result = null;
            for (FuelType type : types) {
                if (type.fluid == fluid.getRegistryName()) {
                    result = type;
                    break;
                }
            }
            return result;
        }
    }
    public static class FuelType {
        public final int rate;
        public final int gen;
        public final ResourceLocation fluid;
        public final ResourceLocation cooled;
        public FuelType(ResourceLocation fluid, int rate, int gen, ResourceLocation cooled) {
            this.fluid = fluid;
            this.rate = rate;
            this.gen = gen;
            this.cooled = cooled;
        }
        public FuelType(ResourceLocation fluid, int rate, int gen, Fluid cooled) {
            this.fluid = fluid;
            this.rate = rate;
            this.gen = gen;
            this.cooled = cooled.getRegistryName();
        }
        public FuelType(ResourceLocation fluid, int rate, int gen, FuelType cooled) {
            this.fluid = fluid;
            this.rate = rate;
            this.gen = gen;
            this.cooled = cooled.fluid;
        }
        public Boolean is(Fluid fluid) {
            return ForgeRegistries.FLUIDS.getValue(this.fluid) != null && fluid.getRegistryName() == this.fluid;
        }
    }
    public static void a() {
    }
}
