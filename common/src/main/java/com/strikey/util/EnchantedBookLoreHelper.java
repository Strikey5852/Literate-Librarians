package com.strikey.util;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class EnchantedBookLoreHelper {
    private EnchantedBookLoreHelper() {}

    private static final ResourceLocation GALACTIC = ResourceLocation.parse("minecraft:alt");
    private static final Style GALACTIC_STYLE = Style.EMPTY.withFont(GALACTIC);

    private static String generateBlock(Random random) {
        String glyphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        int lines = 6 + random.nextInt(4);
        for (int l = 0; l < lines; l++) {
            int len = 18 + random.nextInt(14);
            for (int c = 0; c < len; c++) {
                sb.append(glyphs.charAt(random.nextInt(glyphs.length())));
            }
            if (l < lines - 1) sb.append('\n');
        }
        return sb.toString();
    }

    public static WrittenBookContent create(Holder<Enchantment> enchantment, int level) {
        String id = enchantment.unwrapKey().map(k -> k.location().toString()).orElse(enchantment.toString());
        String name = Enchantment.getFullname(enchantment, level).getString();

        int pages = Math.max(1, level);
        List<Filterable<Component>> pageList = new ArrayList<>();

        Random random = new Random((long) id.hashCode());

        String first = name + "\n\n" + generateBlock(random);
        pageList.add(Filterable.passThrough(Component.literal(first).withStyle(GALACTIC_STYLE)));

        for (int i = 1; i < pages; i++) {
            pageList.add(Filterable.passThrough(Component.literal(generateBlock(random)).withStyle(GALACTIC_STYLE)));
        }

        Filterable<String> title = Filterable.passThrough(name);
        return new WrittenBookContent(title, "LiterateLibrarians", 0, pageList, true);
    }
}
