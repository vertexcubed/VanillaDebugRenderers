package com.vivi.vanilladebugrenderers.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public enum RendererType implements StringRepresentable {



    PATHFINDING("pathfinding", Minecraft.getInstance().debugRenderer.pathfindingRenderer),
    WATER("water", Minecraft.getInstance().debugRenderer.waterDebugRenderer),
    CHUNK_BORDER("chunk_border", Minecraft.getInstance().debugRenderer.chunkBorderRenderer),
    HEIGHT_MAP("height_map", Minecraft.getInstance().debugRenderer.heightMapRenderer),
    COLLISION_BOX("collision_box", Minecraft.getInstance().debugRenderer.collisionBoxRenderer),
    SUPPORT_BLOCK("support_block", Minecraft.getInstance().debugRenderer.supportBlockRenderer),
    NEIGHBORS_UPDATE("neighbors_update", Minecraft.getInstance().debugRenderer.neighborsUpdateRenderer),
    STRUCTURE("structure", Minecraft.getInstance().debugRenderer.structureRenderer),
    LIGHT("light", Minecraft.getInstance().debugRenderer.lightDebugRenderer),
//    WORLD_GEN_ATTEMPT("world_gen_attempt", Minecraft.getInstance().debugRenderer.worldGenAttemptRenderer),
    SOLID_FACE("solid_face", Minecraft.getInstance().debugRenderer.solidFaceRenderer),
    CHUNK("chunk", Minecraft.getInstance().debugRenderer.chunkRenderer),
    BRAIN("brain", Minecraft.getInstance().debugRenderer.brainDebugRenderer),
//    VILLAGE_SECTIONS("village_sections", Minecraft.getInstance().debugRenderer.villageSectionsDebugRenderer),
    BEE("bee", Minecraft.getInstance().debugRenderer.beeDebugRenderer),
    RAID("raid", Minecraft.getInstance().debugRenderer.raidDebugRenderer),
    GOAL_SELECTOR("goal_selector", Minecraft.getInstance().debugRenderer.goalSelectorRenderer),
    GAME_TEST("game_test", Minecraft.getInstance().debugRenderer.gameTestDebugRenderer),
    GAME_EVENT_LISTENER("game_event_listener", Minecraft.getInstance().debugRenderer.gameEventListenerRenderer),
    SKY_LIGHT_SECTION("sky_light_section", Minecraft.getInstance().debugRenderer.skyLightSectionDebugRenderer);

    public static final StringRepresentable.EnumCodec<RendererType> CODEC = StringRepresentable.fromEnum(RendererType::values);

    private final String name;
    private final DebugRenderer.SimpleDebugRenderer renderer;

    RendererType(String name, DebugRenderer.SimpleDebugRenderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public DebugRenderer.SimpleDebugRenderer getRenderer() {
        return renderer;
    }

    @Nullable
    @Contract("_,!null->!null;_,null->_")
    public static RendererType byName(String pTargetName, @Nullable RendererType pFallback) {
        RendererType rendererType = CODEC.byName(pTargetName);
        return rendererType != null ? rendererType : pFallback;
    }
}
