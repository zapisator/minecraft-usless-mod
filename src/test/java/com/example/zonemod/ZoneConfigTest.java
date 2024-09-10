package com.example.zonemod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZoneConfigTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void testLoadConfigWithPos1AndPos2(@TempDir File tempDir) throws IOException {
        // Given
        final File configFile = new File(tempDir, "zonemod.json");
        final String json = """
                {
                  "zones": [
                    {
                      "pos1": {
                        "x": 0,
                        "y": 60,
                        "z": 0
                      },
                      "pos2": {
                        "x": 10,
                        "y": 70,
                        "z": 10
                      },
                      "allowedUuids": [
                        "f78a4d8d-d513-4b5c-956a-47b88f99537d"
                      ]
                    },
                    {
                      "pos1": {
                        "x": -20,
                        "y": 50,
                        "z": -20
                      },
                      "pos2": {
                        "x": -10,
                        "y": 60,
                        "z": -10
                      },
                      "allowedUuids": []
                    }
                  ]
                }
                """;
        Files.writeString(configFile.toPath(), json);
        final int expectedZonesCount = 2;
        final Box expectedBoundingBox1 = new Box(0, 60, 0, 10, 70, 10);
        final UUID expectedUUID1 = UUID.fromString("f78a4d8d-d513-4b5c-956a-47b88f99537d");
        final Box expectedBoundingBox2 = new Box(-20, 50, -20, -10, 60, -10);

        // When
        final ZoneConfig zoneConfig = ZoneConfig.load(configFile.getAbsolutePath());
        final List<ZoneConfig.Zone> actualZones = zoneConfig.getZones();
        final ZoneConfig.Zone actualZone1 = actualZones.get(0);
        final ZoneConfig.Zone actualZone2 = actualZones.get(1);

        // Then
        assertAll(
                () -> assertEquals(expectedZonesCount, actualZones.size()),
                () -> assertEquals(expectedBoundingBox1, actualZone1.getBoundingBox()),
                () -> assertEquals(expectedUUID1, actualZone1.getAllowedUuids().get(0)),
                () -> assertEquals(expectedBoundingBox2, actualZone2.getBoundingBox()),
                () -> assertTrue(actualZone2.getAllowedUuids().isEmpty())
        );
    }

    @Test
    void testSaveConfig(@TempDir File tempDir) throws IOException {
        final String configFilePath = new File(tempDir, "zonemod.json").getAbsolutePath();
        final UUID uuid = UUID.fromString("f78a4d8d-d513-4b5c-956a-47b88f99537d");
        final ZoneConfig.Zone zone1 = new ZoneConfig.Zone(
                new BlockPos(0, 60, 0),
                new BlockPos(10, 70, 10),
                List.of(uuid)
        );
        final ZoneConfig.Zone zone2 = new ZoneConfig.Zone(
                new BlockPos(-20, 50, -20),
                new BlockPos(-10, 60, -10),
                List.of()
        );

        // Сохраняем зоны в файл
        ZoneConfig zoneConfig = ZoneConfig.load(configFilePath)
                .addZone(zone1)
                .addZone(zone2)
                .saveConfig();

        // Сериализуем список зон
        final String actualJson = Files.readString(Path.of(configFilePath));

        final String expectedJson = """
            [
              {
                "pos1": {
                  "x": 0,
                  "y": 60,
                  "z": 0
                },
                "pos2": {
                  "x": 10,
                  "y": 70,
                  "z": 10
                },
                "allowedUuids": [
                  "f78a4d8d-d513-4b5c-956a-47b88f99537d"
                ]
              },
              {
                "pos1": {
                  "x": -20,
                  "y": 50,
                  "z": -20
                },
                "pos2": {
                  "x": -10,
                  "y": 60,
                  "z": -10
                },
                "allowedUuids": []
              }
            ]
            """;

        assertEquals(expectedJson.trim(), actualJson.trim());
    }
}