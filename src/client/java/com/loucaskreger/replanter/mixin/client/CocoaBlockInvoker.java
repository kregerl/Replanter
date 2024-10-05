package com.loucaskreger.replanter.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CocoaBlock.class)
public interface CocoaBlockInvoker {
    @Invoker("getOutlineShape")
    public VoxelShape invokeGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
}
