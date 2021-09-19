package Andrew6rant.tiered.block;

import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.ModifierUtils;
import Andrew6rant.tiered.api.PotentialAttribute;
import Andrew6rant.tiered.mixin.ServerPlayerEntityMixin;
import Andrew6rant.tiered.mixin.TooltipFadeAccessor;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.UnaryOperator;

public class ReforgingStation extends Block {

    public ReforgingStation(Settings settings) {
        super(settings.nonOpaque());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }
    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = blockState.get(Properties.HORIZONTAL_FACING);
        switch (dir) {
            case NORTH, SOUTH, DOWN -> {
                VoxelShape shape = VoxelShapes.empty();
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.0625, 0, 0, 1.0625, 0.0625, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.0625, 0.0625, 0.875, 1.0625, 0.9375), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.25, 0.8125, 0, 1.25, 1.25, 1), BooleanBiFunction.OR);
                return shape;
            }
            case EAST, WEST, UP -> {
                VoxelShape shape = VoxelShapes.empty();
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, -0.0625, 1, 0.0625, 1.0625), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0.0625, 0.125, 0.9375, 1.0625, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.8125, -0.25, 1, 1.25, 1.25), BooleanBiFunction.OR);
                return shape;
            }
        };
        return null;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        ItemStack stack = player.getStackInHand(hand);
        if(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && stack.getCooldown() == 0 ){
            stack.removeSubNbt(Tiered.NBT_SUBTAG_KEY);
            player.getItemCooldownManager().set(stack.getItem(), 20);
            Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
            if(potentialAttributeID != null) {
                stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
            }
            player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            //((TooltipFadeAccessor) this).setHeldItemTooltipFade(40);
            //((TooltipFadeAccessor) MinecraftClient.getInstance()).setHeldItemTooltipFade(40);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}