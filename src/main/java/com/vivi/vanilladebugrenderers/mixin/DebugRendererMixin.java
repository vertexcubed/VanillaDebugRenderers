package com.vivi.vanilladebugrenderers.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.vivi.vanilladebugrenderers.ModDebugRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {



    @Inject(method = "render", at = @At("HEAD"))
    public void vdr$render(PoseStack pPoseStack, MultiBufferSource.BufferSource pBufferSource, double pCamX, double pCamY, double pCamZ, CallbackInfo ci) {
        ModDebugRenderer.getInstance().render(pPoseStack, pBufferSource, pCamX, pCamY, pCamZ);
    }
}
