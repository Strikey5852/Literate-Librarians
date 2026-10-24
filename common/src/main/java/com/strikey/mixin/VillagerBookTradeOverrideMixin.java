package com.strikey.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.strikey.util.VillagerBookTradeState;

@Mixin(Villager.class)
public class VillagerBookTradeOverrideMixin {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void literate$readBookTradeOverride(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("LiterateLibrarians")) {
            boolean overridden = tag.getCompound("LiterateLibrarians").getBoolean("book_trade_overridden");
            VillagerBookTradeState.setBookTradeOverridden(((Villager) (Object) this), overridden);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void literate$writeBookTradeOverride(CompoundTag tag, CallbackInfo ci) {
        CompoundTag literateTag = new CompoundTag();
        literateTag.putBoolean("book_trade_overridden", VillagerBookTradeState.isBookTradeOverridden(((Villager) (Object) this)));
        tag.put("LiterateLibrarians", literateTag);
    }
}
