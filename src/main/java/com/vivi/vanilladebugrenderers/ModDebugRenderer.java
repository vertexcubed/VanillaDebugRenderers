package com.vivi.vanilladebugrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivi.vanilladebugrenderers.command.RendererType;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ModDebugRenderer {
    private static final ModDebugRenderer INSTANCE = new ModDebugRenderer();

    private final Map<RendererType, Boolean> renderers = new EnumMap<>(RendererType.class);


    private ModDebugRenderer() {
        for(int i = 0; i < RendererType.values().length; i++) {
            renderers.put(RendererType.values()[i], false);
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        renderers.forEach((renderer, enabled) -> {
            if(enabled) renderer.getRenderer().render(poseStack, bufferSource, camX, camY, camZ);
        });
    }

    public void toggleValue(RendererType type) {
        renderers.put(type, !renderers.get(type));
    }








    public static ModDebugRenderer getInstance() {
        return INSTANCE;
    }



}
