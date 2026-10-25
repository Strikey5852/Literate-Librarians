package com.strikey.mixin;

import com.strikey.Config;
import com.strikey.util.LecternBookHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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

    @Inject(method = "getOffer", at = @At("HEAD"), cancellable = true)
    private void literate$preferLecternEnchantment(net.minecraft.world.entity.Entity entity, net.minecraft.util.RandomSource random, CallbackInfoReturnable<MerchantOffer> cir) {
        if (!(entity instanceof Villager villager)) return;
        Optional<LecternBookHelper.EnchantmentChoice> choiceOpt =
                LecternBookHelper.getLecternEnchantment(villager, tradeableEnchantments);
        if (choiceOpt.isEmpty() && Config.get().isEnableNonTradeableEnchants()) {
            choiceOpt = LecternBookHelper.getLecternNonTradeableEnchantment(villager, tradeableEnchantments);
        }
        if (choiceOpt.isEmpty()) return;
        var choice = choiceOpt.get();
        // Skip the override if the villager already has the lectern enchant as a trade
        if (villager.getOffers().stream()
                .filter(offer -> offer.getResult().is(Items.ENCHANTED_BOOK))
                .anyMatch(offer -> {
                    ItemEnchantments ench = offer.getResult().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                    return ench.getLevel(choice.enchantment()) > 0;
                })) {
            return;
        }
        if (random.nextFloat() >= Config.get().getOverrideChance()) {
            return;
        }
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(choice.enchantment(), choice.level()));
        int cost = 2 + random.nextInt(5 + choice.level() * 10) + 3 * choice.level();
        if (choice.enchantment().is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
            cost *= 2;
        }
        if (cost > 64) {
            cost = 64;
        }
        MerchantOffer offer = new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK, 1)), book, 12, villagerXp, 0.2F);
        cir.setReturnValue(offer);
        cir.cancel();
    }
}
