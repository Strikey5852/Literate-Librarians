package com.strikey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    public static final String KEY_ENABLE_NON_TRADEABLE = "enableNonTradeableEnchants";
    public static final boolean DEFAULT_ENABLE_NON_TRADEABLE = true;
    public static final String KEY_OVERRIDE_CHANCE = "overrideChance";
    public static final float DEFAULT_OVERRIDE_CHANCE = 1.0f;

    private static volatile Config instance;

    private final Path configPath;
    private boolean enableNonTradeableEnchants = DEFAULT_ENABLE_NON_TRADEABLE;
    private float overrideChance = DEFAULT_OVERRIDE_CHANCE;

    public static Config get() {
        return instance;
    }

    public static void init(Path configPath) {
        instance = new Config(configPath);
    }

    private Config(Path configPath) {
        this.configPath = configPath;
        load();
    }

    public boolean isEnableNonTradeableEnchants() {
        return enableNonTradeableEnchants;
    }

    public float getOverrideChance() {
        return overrideChance;
    }

    private void load() {
        try {
            if (Files.exists(configPath)) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    String content = new String(in.readAllBytes());
                    for (String line : content.split("\\r?\\n")) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                        String[] parts = trimmed.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim().replace("\"", "").replace("'", "").trim();
                            if (key.equals(KEY_ENABLE_NON_TRADEABLE)) {
                                enableNonTradeableEnchants = Boolean.parseBoolean(value);
                            } else if (key.equals(KEY_OVERRIDE_CHANCE)) {
                                try {
                                    overrideChance = Math.clamp(Float.parseFloat(value), 0.0f, 1.0f);
                                } catch (NumberFormatException e) {
                                    // Keep default if invalid
                                }
                            }
                        }
                    }
                }
            } else {
                createDefaultConfig();
            }
        } catch (IOException e) {
            // Config loading failed; fall back to default
        }
    }

    private void createDefaultConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream out = Files.newOutputStream(configPath)) {
                StringBuilder sb = new StringBuilder();
                sb.append("# Enable non-tradeable enchantments (e.g., Swift Sneak, Soul Speed)\n");
                sb.append("enableNonTradeableEnchants = ").append(DEFAULT_ENABLE_NON_TRADEABLE).append("\n");
                sb.append("# Chance (0.0 to 1.0) for the lectern book to appear as a trade\n");
                sb.append("overrideChance = ").append(DEFAULT_OVERRIDE_CHANCE).append("\n");
                out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // Config creation failed
        }
    }
}
