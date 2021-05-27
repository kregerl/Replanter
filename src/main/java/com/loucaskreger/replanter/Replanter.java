package com.loucaskreger.replanter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Replanter implements ModInitializer {
	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
				ItemStack heldStack = player.getMainHandStack();
				ItemStack offhandStack = player.getOffHandStack();

				if (hand == Hand.MAIN_HAND && !heldStack.isEmpty()) {
					this.replaceCrop(heldStack, pos, world);
				} else if (hand == Hand.OFF_HAND && !offhandStack.isEmpty()) {
					this.replaceCrop(offhandStack, pos, world);
				}
			}
			return ActionResult.PASS;
		});
	}

	private void replaceCrop(ItemStack stack, BlockPos pos, World world) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		ClientPlayerInteractionManager im = mc.interactionManager;
		if (stack.getItem() instanceof BlockItem) {
			Block itemBlock = ((BlockItem) stack.getItem()).getBlock();

			if (block instanceof CropBlock) {

				if (itemBlock instanceof CropBlock) {
					if (((CropBlock) block).isMature(state)) {
						im.attackBlock(pos, Direction.DOWN);
					}
				}

				// Exception for nether warts
			} else if (block instanceof NetherWartBlock && block == itemBlock) {
				NetherWartBlock wartBlock = (NetherWartBlock) block;
				// If nether wart block outline shape y value is max size then the block is
				// fully grown.
				if ((wartBlock.getOutlineShape(state, world, pos, ShapeContext.absent()).getBoundingBox().getYLength()
						* 16) == 14) {
					im.attackBlock(pos, Direction.DOWN);
				}
			} else if (block instanceof CocoaBlock) {

				CocoaBlock cocoaBlock = (CocoaBlock) block;
				if ((cocoaBlock.getOutlineShape(state, world, pos, ShapeContext.absent()).getBoundingBox()
						.getYLength() == 0.5625)) {
					im.attackBlock(pos, world.getBlockState(pos).get(CocoaBlock.FACING));
				}
			}
		}
	}
}
