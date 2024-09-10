package com.example.zonemod;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ZoneAccessEventTest {

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void testIsAllowedDefault() {
        final PlayerEntity player = mock(PlayerEntity.class);
        final ZoneConfig.Zone zone = mock(ZoneConfig.Zone.class);
        final ZoneAccessEvent event = new ZoneAccessEvent(player, zone);

        assertTrue(event.isAllowed());
    }

    @Test
    void testSetAllowed() {
        final PlayerEntity player = mock(PlayerEntity.class);
        final ZoneConfig.Zone zone = mock(ZoneConfig.Zone.class);
        final ZoneAccessEvent event = new ZoneAccessEvent(player, zone);

        event.setAllowed(false);

        assertFalse(event.isAllowed());
    }
}