package com.example.zonemod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ZoneConfig {
    private final String configFilePath;
    @Getter
    private final List<Zone> zones = new ArrayList<>();

    public static ZoneConfig load() {
        return load("config/zonemod.json");
    }

    public static ZoneConfig load(String filePath) {
        final ZoneConfig config = new ZoneConfig(filePath);
        config.loadConfig();
        return config;
    }

    private void loadConfig() {
        final Path configPath = Paths.get(configFilePath);
        if (Files.exists(configPath)) {
            try {
                final String json = Files.readString(configPath);
                final JsonElement jsonElement = JsonParser.parseString(json);

                if (jsonElement.isJsonObject()) {
                    final JsonObject jsonObject = jsonElement.getAsJsonObject();
                    // Извлекаем массив zones из поля "zones"
                    final JsonArray zonesArray = jsonObject.getAsJsonArray("zones");

                    for (JsonElement zoneElement : zonesArray) {
                        if (zoneElement.isJsonObject()) {
                            zones.add(new Zone(zoneElement.getAsJsonObject()));
                        }
                    }
                }
            } catch (IOException e) {
                ZoneMod.LOGGER.error("Failed to load ZoneMod config", e);
            }
        } else {
            try {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
            } catch (IOException e) {
                ZoneMod.LOGGER.error("Failed to create ZoneMod config", e);
            }
        }
    }

    public ZoneConfig saveConfig() {
        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        final Path configPath = Paths.get(configFilePath);
        try {
            // Сериализуем только список zones
            final String json = gson.toJson(zones);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            ZoneMod.LOGGER.error("Failed to save ZoneMod config", e);
        }
        return this;
    }

    public ZoneConfig addZone(Zone zone) {
        zones.add(zone);
        return this;
    }

    public ZoneConfig removeZone(Zone zone) {
        zones.remove(zone);
        return this;
    }

    @Getter
    @NoArgsConstructor
    public static class Zone {
        private BlockPos pos1;
        private BlockPos pos2;
        private transient Box boundingBox;
        private List<UUID> allowedUuids;

        public Zone(BlockPos pos1, BlockPos pos2, List<UUID> allowedUuids) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.boundingBox = new Box(pos1, pos2);
            this.allowedUuids = allowedUuids;
        }

        public Zone(JsonObject zoneObject) {
            this.pos1 = new Gson().fromJson(zoneObject.get("pos1"), BlockPos.class);
            this.pos2 = new Gson().fromJson(zoneObject.get("pos2"), BlockPos.class);
            this.boundingBox = new Box(pos1, pos2);
            this.allowedUuids = zoneObject.getAsJsonArray("allowedUuids")
                    .asList().stream()
                    .map(JsonElement::getAsString)
                    .map(UUID::fromString)
                    .toList();
        }
    }
}
