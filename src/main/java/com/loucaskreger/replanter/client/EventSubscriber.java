package com.loucaskreger.replanter.client;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraftforge.fml.common.Mod;
import com.loucaskreger.replanter.Replanter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

@Mod.EventBusSubscriber(modid = Replanter.MOD_ID, value = Dist.CLIENT)
public class EventSubscriber {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onPlayerRightClick(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            BlockHitResult result = event.getHitVec();
            Level level = event.getWorld();
            BlockPos pos = result.getBlockPos();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            ItemStack stack = event.getItemStack();
            Item item = stack.getItem();
            MultiPlayerGameMode pc = mc.gameMode;

            if (item instanceof BlockItem) {
                Block itemBlock = ((BlockItem) item).getBlock();
                if (block instanceof CropBlock) {

                    if (isPlantable(itemBlock, block, level, pos)) {
                        CropBlock cropsBlock = ((CropBlock) block);

                        if (cropsBlock.isMaxAge(state)) {
                            pc.startDestroyBlock(pos, Direction.DOWN);
                        }
                    }
                } else if (block instanceof NetherWartBlock) {

                    if (isPlantable(itemBlock, block, level, pos)) {
                        NetherWartBlock wartBlock = (NetherWartBlock) block;
                        // If nether wart block outline shape y value is max size then the block is
                        // fully grown.
                        if ((wartBlock.getShape(state, level, pos, CollisionContext.empty()).bounds().getYsize()
                                * 16) == 14) {
                            pc.startDestroyBlock(pos, Direction.DOWN);
                        }
                    }
                } else if (block instanceof CocoaBlock) {

                    CocoaBlock cocoaBlock = (CocoaBlock) block;
                    if ((cocoaBlock.getShape(state, level, pos, CollisionContext.empty()).bounds()
                            .getYsize() == 0.5625)) {
                        pc.startDestroyBlock(pos, level.getBlockState(pos).getValue(CocoaBlock.FACING));
                    }
                }
            }
        }
    }

    private static boolean isPlantable(Block itemBlock, Block block, Level level, BlockPos pos) {
        if (itemBlock instanceof IPlantable) {
            PlantType itemType = ((IPlantable) itemBlock).getPlantType(level, pos);
            PlantType blockType = ((IPlantable) block).getPlantType(level, pos);
            if (itemType == blockType) {
                return true;
            }
        }
        return false;
    }

}