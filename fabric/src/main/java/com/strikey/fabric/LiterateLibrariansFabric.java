package com.strikey.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.strikey.LiterateLibrarians;

import java.nio.file.Path;

public final class LiterateLibrariansFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve("literate_librarians.toml");
        LiterateLibrarians.init(configFile);
    }
}