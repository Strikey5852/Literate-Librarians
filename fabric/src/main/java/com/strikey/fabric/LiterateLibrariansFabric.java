package com.strikey.fabric;

import net.fabricmc.api.ModInitializer;

import com.strikey.LiterateLibrarians;

public final class LiterateLibrariansFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LiterateLibrarians.init();
    }
}