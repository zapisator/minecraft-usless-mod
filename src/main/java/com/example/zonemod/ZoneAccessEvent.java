package com.example.zonemod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

@Getter
@RequiredArgsConstructor
public class ZoneAccessEvent {
    public static final Event<ZoneAccessHandler> EVENT = EventFactory.createArrayBacked(ZoneAccessHandler.class,
            listeners -> (player, zone, event) -> {
                for (ZoneAccessHandler listener : listeners) {
                    listener.onZoneAccess(player, zone, event);
                }
            }
    );

    private final PlayerEntity player;
    private final ZoneConfig.Zone zone;
    @Setter
    private boolean allowed = true;

    @FunctionalInterface
    public interface ZoneAccessHandler {
        void onZoneAccess(PlayerEntity player, ZoneConfig.Zone zone, ZoneAccessEvent event);
    }
}
