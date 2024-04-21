package com.keikun1215.createhbmsteam.block.entity;

import com.keikun1215.createhbmsteam.block.SteamTurbineBlock;
import com.keikun1215.createhbmsteam.block.entity.renderer.AddonPartialModels;
import com.keikun1215.createhbmsteam.util.FuelTypes;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SteamTurbineBlockEntity extends GeneratingKineticBlockEntity {
    SmartFluidTankBehaviour steamTank;
    SmartFluidTankBehaviour lpsTank;
    FuelTypes.FuelType type;
    protected ScrollValueBehaviour consumeSpeed;
    //public Boolean generating;
    public SteamTurbineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        //generating = false;
        capacity = 655360000;
        type = FuelTypes.STEAM;
    }
    public SteamTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(AddonBlockEntities.steamTurbineBlockEntity.get(), pos, state);
        //generating = false;
        capacity = 655360000;
        type = FuelTypes.STEAM;
    }
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        Direction myFacing = getBlockState().getValue(SteamTurbineBlock.FACING);
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == new LoopFacing(myFacing).prev()) {
            return lpsTank.getCapability().cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == new LoopFacing(myFacing).next()) {
            return steamTank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
    static class LoopFacing {
        private static final List<Direction> facings = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        private final int index;
        public LoopFacing(Direction face) {
            index = facings.indexOf(face);
        }
        public Direction next() {
            Direction result = null;
            switch (index) {
                case 3 -> result = Direction.NORTH;
                case 2 -> result = Direction.WEST;
                case 1 -> result = Direction.SOUTH;
                case 0 -> result = Direction.EAST;
            }
            return result;
        }
        public Direction prev() {
            Direction result = null;
            switch (index) {
                case 3 -> result = Direction.SOUTH;
                case 2 -> result = Direction.EAST;
                case 1 -> result = Direction.NORTH;
                case 0 -> result = Direction.WEST;
            }
            return result;
        }
    }
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return steamTank.getCapability().cast();
        return super.getCapability(cap);
    }
    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(Math.min(generateAmount(), 256), getBlockState().getValue(SteamTurbineBlock.FACING));
    }
    @Override
    public float calculateAddedStressCapacity() {
        return generateAmount();
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getSpeed() {
        return getGeneratedSpeed();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        addStressImpactStats(tooltip, stress);
        return containedFluidTooltip(tooltip, isPlayerSneaking, steamTank.getCapability().cast()) | containedFluidTooltip(tooltip, isPlayerSneaking, lpsTank.getCapability().cast());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            sequenceContext = null;
            SmartFluidTank handler = steamTank.getPrimaryHandler();
            if (isValidFuel(handler)) {
                int now = handler.getFluid().getAmount();
                if (now > consumeAmount()) {
                    handler.getFluid().setAmount(now - consumeAmount());
                    SmartFluidTank oph = lpsTank.getPrimaryHandler();
                    if (oph.getFluid().getAmount() == 0) {
                        oph.setFluid(new FluidStack(ForgeRegistries.FLUIDS.getValue(type.cooled), 0));
                    }
                    oph.getFluid().setAmount(oph.getFluid().getAmount() + consumeAmount());
                }
            }
            updateGeneratedRotation();
        }
    }
    public int consumeAmount() {
        return (type.rate * consumeSpeed.value) / 20;
    }
    public int generateAmount() {
        return type.gen * consumeSpeed.value;
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        steamTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 102400, true)
                .whenFluidUpdates(() -> {
                    if (FuelTypes.TYPES.get(steamTank.getPrimaryHandler().getFluid().getFluid()) != null) {
                        type = FuelTypes.TYPES.get(steamTank.getPrimaryHandler().getFluid().getFluid());
                        if (lpsTank.getPrimaryHandler().getFluid().getFluid().getRegistryName() != type.cooled && !lpsTank.isEmpty()) {
                            lpsTank.getPrimaryHandler().setFluid(new FluidStack(ForgeRegistries.FLUIDS.getValue(type.cooled), 0));
                        }
                    }
                });
        lpsTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 102400, true)
                .forbidInsertion();
        consumeSpeed = new ScrollValueBehaviour(new TextComponent("Steam Consume Multiplier"), this, new ValueBoxTransform.Sided() {
            @Override
            protected Vec3 getSouthLocation() {
                return VecHelper.voxelSpace(8, 8, 16);
            }
            @Override
            public Vec3 getLocalOffset(BlockState state) {
                return super.getLocalOffset(state).add(new Vec3(0, 0, 0));
            }
            @Override
            protected boolean isSideActive(BlockState state, Direction direction) {
                return direction.getAxis() == Direction.Axis.Y;
            }
        });
        consumeSpeed.between(1, 100);
        consumeSpeed.value = 1;
        consumeSpeed.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(consumeSpeed);
        behaviours.add(steamTank);
        behaviours.add(lpsTank);
        super.addBehaviours(behaviours);
    }
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        steamTank.write(compound, true);
        lpsTank.write(compound, true);
        consumeSpeed.write(compound, true);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        steamTank.read(compound, true);
        lpsTank.read(compound, true);
        consumeSpeed.read(compound, true);
    }
    @OnlyIn(Dist.CLIENT)
    public int prevSpeed;
    @OnlyIn(Dist.CLIENT)
    public SuperByteBuffer getOutside() {
        BlockState blockState = getBlockState();
        Direction facing = blockState.getOptionalValue(SteamTurbineBlock.FACING)
                .orElse(Direction.UP);
        return CachedBufferer.partialFacing(AddonPartialModels.STEAM_TURBINE_BLOCK, blockState, facing.getOpposite());
    }
    @OnlyIn(Dist.CLIENT)
    public SuperByteBuffer getBlades() {
        BlockState blockState = getBlockState();
        Direction facing = blockState.getOptionalValue(SteamTurbineBlock.FACING)
                .orElse(Direction.UP);
        return CachedBufferer.partialFacing(AddonPartialModels.STEAM_TURBINE_BLADE, blockState, facing.getOpposite());
    }
    public static boolean isValidFuel(SmartFluidTank tank) {
        return FuelTypes.TYPES.get(tank.getFluid().getFluid()) != null;
    }
    public static boolean isValid(SteamTurbineBlockEntity turbine) {
        return isValidFuel(turbine.steamTank.getPrimaryHandler()) && turbine.steamTank.getPrimaryHandler().getFluid().getAmount() > turbine.consumeSpeed.value;
    }
}
