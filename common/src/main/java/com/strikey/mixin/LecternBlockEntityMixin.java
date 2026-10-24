package com.strikey.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin {
    @Shadow public abstract ItemStack getBook();

    @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
    private void literate$hasBook(CallbackInfoReturnable<Boolean> cir) {
        ItemStack book = getBook();
        if (book.is(Items.ENCHANTED_BOOK)) {
            var content = book.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, null);
            if (content != null) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getPageCount(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private static void literate$pageCount(ItemStack book, CallbackInfoReturnable<Integer> cir) {
        if (book.is(Items.ENCHANTED_BOOK)) {
            var content = book.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, null);
            if (content != null) {
                cir.setReturnValue(content.pages().size());
                cir.cancel();
            }
        }
    }
}