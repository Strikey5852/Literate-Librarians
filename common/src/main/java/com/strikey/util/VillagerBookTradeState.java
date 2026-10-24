package com.strikey.util;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.npc.Villager;

import java.util.Map;
import java.util.UUID;

public class VillagerBookTradeState {
    private static final Map<UUID, Boolean> overriddenVillagers = Maps.newHashMap();

    public static boolean isBookTradeOverridden(Villager villager) {
        return overriddenVillagers.getOrDefault(villager.getUUID(), false);
    }

    public static void setBookTradeOverridden(Villager villager, boolean overridden) {
        overriddenVillagers.put(villager.getUUID(), overridden);
    }
}