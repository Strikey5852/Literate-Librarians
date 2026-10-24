package com.strikey.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import com.strikey.util.EnchantedBookLoreHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void literate$placeEnchantedBook(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (!stack.is(Items.ENCHANTED_BOOK)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof LecternBlockEntity lectern)) return;
        if (!lectern.getBook().isEmpty()) return;
        if (!level.isClientSide) {
            ItemEnchantments ench = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            ItemStack toPlace = stack.copy();
            if (!ench.isEmpty()) {
                var entry = ench.entrySet().iterator().next();
                toPlace.set(DataComponents.WRITTEN_BOOK_CONTENT, EnchantedBookLoreHelper.create(entry.getKey(), entry.getIntValue()));
            }
            lectern.setBook(toPlace, player);
            level.setBlock(pos, state.setValue(LecternBlock.HAS_BOOK, true), 3);
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide));
        cir.cancel();
    }
}
