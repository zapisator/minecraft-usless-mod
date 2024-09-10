package com.example.zonemod.mixin;

import com.example.zonemod.ZoneAccessEvent;
import com.example.zonemod.ZoneConfig;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMovementMixin {

    private final ZoneConfig zoneConfig = ZoneConfig.load();

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (ZoneConfig.Zone zone : zoneConfig.getZones()) {
            if (zone
                    .getBoundingBox()
                    .contains(player.getPos().x, player.getPos().y, player.getPos().z)
                    && !zone.getAllowedUuids().contains(player.getUuid())
            ) {

                ZoneAccessEvent event = new ZoneAccessEvent(player, zone);
                ZoneAccessEvent.EVENT.invoker().onZoneAccess(player, event.getZone(), event);

                if (!event.isAllowed()) {
                    ci.cancel();
                    break;
                }
            }
        }
    }
}