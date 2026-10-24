package com.strikey.mixin;

import com.strikey.util.LecternBookHelper;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public class EnchantBookForEmeraldsMixin {
    @Shadow private int villagerXp;
    @Shadow private net.minecraft.tags.TagKey<net.minecraft.world.item.enchantment.Enchantment> tradeableEnchantments;
    @Shadow private int minLevel;
    @Shadow private int maxLevel;

    @Inject(method = "getOffer", at = @At("HEAD"), cancellable = true)
    private void literate$preferLecternEnchantment(net.minecraft.world.entity.Entity entity, net.minecraft.util.RandomSource random, CallbackInfoReturnable<MerchantOffer> cir) {
        if (!(entity instanceof Villager villager)) return;
        Optional<LecternBookHelper.EnchantmentChoice> choiceOpt =
                LecternBookHelper.getLecternEnchantment(villager, tradeableEnchantments, minLevel, maxLevel);
        if (choiceOpt.isEmpty()) return;
        var choice = choiceOpt.get();
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(choice.enchantment(), choice.level()));
        int cost = 5 + 3 * choice.level();
        MerchantOffer offer = new MerchantOffer(new ItemCost(Items.EMERALD, cost), book, 12, villagerXp, 0.2F);
        cir.setReturnValue(offer);
        cir.cancel();
    }
}
