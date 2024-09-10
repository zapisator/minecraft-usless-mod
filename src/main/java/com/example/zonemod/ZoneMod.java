package com.example.zonemod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneMod implements ModInitializer {
    public static final String MOD_ID = "zonemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ZoneMod initialized!");
        ZoneAccessEvent.EVENT.register(((player, zone, event) -> {
                    if (!zone.getAllowedUuids().contains(player.getUuid())) {
                        event.setAllowed(false);
                        // Телепортируем игрока на 10 блоков назад по оси Z
                        player.teleport(player.getX(), player.getY(), player.getZ() - 10);
                    }
                })
        );
    }
}
