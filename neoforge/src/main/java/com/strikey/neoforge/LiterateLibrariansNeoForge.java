package com.strikey.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

import com.strikey.LiterateLibrarians;

import java.nio.file.Path;

@Mod(LiterateLibrarians.MOD_ID)
public final class LiterateLibrariansNeoForge {
    public LiterateLibrariansNeoForge() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        Path configFile = configDir.resolve("literate_librarians.toml");
        LiterateLibrarians.init(configFile);
    }
}