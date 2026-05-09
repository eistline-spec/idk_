package com.example.spawnerhighlight;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnerHighlight implements ModInitializer {
    public static final String MOD_ID = "spawnerhighlight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[SpawnerHighlight] Mod initialized.");
    }
}
