package com.strikey.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.core.GlobalPos;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public final class LecternBookHelper {
    private LecternBookHelper() {}

    public static Optional<EnchantmentChoice> getLecternEnchantment(Villager villager, TagKey<Enchantment> tradeable) {
        return getLecternEnchantmentInternal(villager, tradeable, true);
    }

    public static Optional<EnchantmentChoice> getLecternNonTradeableEnchantment(Villager villager, TagKey<Enchantment> tradeable) {
        return getLecternEnchantmentInternal(villager, tradeable, false);
    }

    private static Optional<EnchantmentChoice> getLecternEnchantmentInternal(Villager villager, TagKey<Enchantment> tradeable, boolean checkTag) {
        var brain = villager.getBrain();
        var jobSite = brain.getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return Optional.empty();
        GlobalPos pos = jobSite.get();
        if (!(villager.level() instanceof ServerLevel serverLevel)) return Optional.empty();
        if (!pos.dimension().equals(serverLevel.dimension())) return Optional.empty();
        BlockEntity be = serverLevel.getBlockEntity(pos.pos());
        if (!(be instanceof LecternBlockEntity lectern)) return Optional.empty();

        ItemStack book = lectern.getBook();
        if (book.isEmpty() || !book.is(Items.ENCHANTED_BOOK)) return Optional.empty();

        ItemEnchantments enchantments = book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) return Optional.empty();
        var entry = enchantments.entrySet().iterator().next();
        Holder<Enchantment> holder = entry.getKey();
        int level = entry.getIntValue();

        if (checkTag && !holder.is(tradeable)) return Optional.empty();
        if (level > holder.value().getMaxLevel()) return Optional.empty();
        int clamped = Math.min(level, holder.value().getMaxLevel());
        return Optional.of(new EnchantmentChoice(holder, clamped));
    }

    public record EnchantmentChoice(Holder<Enchantment> enchantment, int level) {}
}
