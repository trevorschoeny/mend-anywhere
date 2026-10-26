package com.trevorschoeny.mendanywhere;

import com.trevorschoeny.mendanywhere.ip.MendSettingsTab;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Client init: the settings tab exists only with Inventory Plus, so the {@code ip} package
 * is touched nowhere else and IP's classes never load without IP.
 */
public final class MendAnywhereClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("inventoryplus")) MendSettingsTab.register();
    }
}
