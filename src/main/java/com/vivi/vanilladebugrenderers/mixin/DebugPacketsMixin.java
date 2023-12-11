package com.vivi.vanilladebugrenderers.mixin;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(DebugPackets.class)
public abstract class DebugPacketsMixin {

    private static void writeBoundingBox(FriendlyByteBuf buf, BoundingBox bb) {
        buf.writeInt(bb.minX());
        buf.writeInt(bb.minY());
        buf.writeInt(bb.minZ());
        buf.writeInt(bb.maxX());
        buf.writeInt(bb.maxY());
        buf.writeInt(bb.maxZ());
    }


    @Shadow private static void sendPacketToAllPlayers(ServerLevel pLevel, FriendlyByteBuf pBuffer, ResourceLocation pIdentifier) {
        throw new AssertionError();
    }

    @Shadow private static void writeBrain(LivingEntity pLivingEntity, FriendlyByteBuf pBuffer) {
        throw new AssertionError();
    }

    @Inject(method = "sendPathFindingPacket", at = @At("HEAD"))
    private static void vdr$sendPathFindingPacket(Level pLevel, Mob pMob, @Nullable Path pPath, float pMaxDistanceToWaypoint, CallbackInfo ci) {
        if(pPath == null || pLevel.isClientSide()) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(pMob.getId());
        buf.writeFloat(pMaxDistanceToWaypoint);
        pPath.writeToStream(buf);
        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET);
    }

    @Inject(method = "sendNeighborsUpdatePacket", at = @At("HEAD"))
    private static void vdr$sendNeighborsUpdatePacket(Level pLevel, BlockPos pPos, CallbackInfo ci) {
        if(pLevel.isClientSide()) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarLong(pLevel.getGameTime());
        buf.writeBlockPos(pPos);
        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET);
    }

    @Inject(method = "sendGoalSelector", at = @At("HEAD"))
    private static void vdr$sendGoalSelector(Level pLevel, Mob pMob, GoalSelector pGoalSelector, CallbackInfo ci) {
        if(pLevel.isClientSide()) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pMob.blockPosition());
        buf.writeInt(pMob.getId());
        Set<WrappedGoal> goals = pGoalSelector.getAvailableGoals();
        buf.writeInt(goals.size());
        for(WrappedGoal goal : goals) {
            buf.writeInt(goal.getPriority());
            buf.writeBoolean(goal.isRunning());
            buf.writeUtf(goal.getGoal().toString(), 255);
        }
        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR);
    }

    @Inject(method = "sendRaids", at = @At("HEAD"))
    private static void vdr$sendRaids(ServerLevel pLevel, Collection<Raid> pRaids, CallbackInfo ci) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(pRaids.size());
        for(Raid raid : pRaids) {
            buf.writeBlockPos(raid.getCenter());
        }
        sendPacketToAllPlayers(pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_RAIDS);
    }

    @Inject(method = "sendEntityBrain", at = @At("HEAD"))
    private static void vdr$sendEntityBrain(LivingEntity entity, CallbackInfo ci) {
        if(entity.level().isClientSide) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeUUID(entity.getUUID());
        buf.writeInt(entity.getId());
        buf.writeUtf(entity.getDisplayName().getString());
        if(entity instanceof Villager villager) {
            buf.writeUtf(villager.getVillagerData().getProfession().toString());
            buf.writeInt(villager.getVillagerXp());
        }
        else {
            buf.writeUtf("");
            buf.writeInt(0);
        }
        buf.writeFloat(entity.getHealth());
        buf.writeFloat(entity.getMaxHealth());
        writeBrain(entity, buf);

        sendPacketToAllPlayers((ServerLevel) entity.level(), buf, ClientboundCustomPayloadPacket.DEBUG_BRAIN);
    }

    @Inject(method = "sendBeeInfo", at = @At("HEAD"))
    private static void vdr$sendBeeInfo(Bee pBee, CallbackInfo ci) {
        if(pBee.level().isClientSide) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeDouble(pBee.getX());
        buf.writeDouble(pBee.getY());
        buf.writeDouble(pBee.getZ());
        buf.writeUUID(pBee.getUUID());
        buf.writeInt(pBee.getId());
        buf.writeNullable(pBee.getHivePos(), FriendlyByteBuf::writeBlockPos);
        buf.writeNullable(pBee.getSavedFlowerPos(), FriendlyByteBuf::writeBlockPos);
        buf.writeInt(pBee.getTravellingTicks());
        Path path = pBee.getNavigation().getPath();
        buf.writeNullable(path, (buf1, path1) -> {
            path1.writeToStream(buf1);
        });
        List<String> goals = pBee.getGoalSelector().getRunningGoals().map(goal -> goal.getGoal().toString()).toList();
        buf.writeVarInt(goals.size());
        for(String goal : goals) {
            buf.writeUtf(goal);
        }

        List<BlockPos> hives = pBee.getBlacklistedHives();
        buf.writeVarInt(hives.size());
        for(BlockPos hive : hives) {
            buf.writeBlockPos(hive);
        }

        sendPacketToAllPlayers((ServerLevel) pBee.level(), buf, ClientboundCustomPayloadPacket.DEBUG_BEE);
    }

    @Inject(method = "sendGameEventInfo", at = @At("HEAD"))
    private static void vdr$sendGameEventInfo(Level pLevel, GameEvent pGameEvent, Vec3 pPos, CallbackInfo ci) {
        if(pLevel.isClientSide) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(pGameEvent.getName());
        buf.writeDouble(pPos.x);
        buf.writeDouble(pPos.y);
        buf.writeDouble(pPos.z);

        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT);
    }

    @Inject(method = "sendGameEventListenerInfo", at = @At("HEAD"))
    private static void vdr$sendGameEventListenerInfo(Level pLevel, GameEventListener pGameEventListener, CallbackInfo ci) {
        if(pLevel.isClientSide) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PositionSource positionSource = pGameEventListener.getListenerSource();
        PositionSourceType.toNetwork(positionSource, buf);
        buf.writeVarInt(pGameEventListener.getListenerRadius());

        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT_LISTENER);
    }

    @Inject(method = "sendHiveInfo", at = @At("HEAD"))
    private static void vdr$sendHiveInfo(Level pLevel, BlockPos pPos, BlockState pBlockState, BeehiveBlockEntity pHiveBlockEntity, CallbackInfo ci) {
        if(pLevel.isClientSide) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pPos);
        //this is probably wrong but wth is hive "type"??
        buf.writeUtf(ForgeRegistries.BLOCKS.getKey(pBlockState.getBlock()).toString());
        buf.writeInt(pHiveBlockEntity.getOccupantCount());
        buf.writeInt(BeehiveBlockEntity.getHoneyLevel(pBlockState));
        buf.writeBoolean(pHiveBlockEntity.isSedated());
        sendPacketToAllPlayers((ServerLevel) pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_HIVE);
    }

    @Inject(method = "sendVillageSectionsPacket", at = @At("HEAD"))
    private static void vdr$sendVillageSectionsPacket(ServerLevel pLevel, BlockPos pPos, CallbackInfo ci) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        //todo: figure this one out
    }


    @Inject(method = "sendPoiAddedPacket", at = @At("HEAD"))
    private static void vdr$sendPoiAddedPacket(ServerLevel pLevel, BlockPos pPos, CallbackInfo ci) {

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        PoiManager manager = pLevel.getPoiManager();
        buf.writeBlockPos(pPos);

        Holder<PoiType> poiType = manager.getType(pPos).orElse(null);
        if(poiType != null) {
            buf.writeUtf(ForgeRegistries.POI_TYPES.getKey(poiType.get()).toString());
        }
        else {
            //todo: figure out what to write if no poi type present
            buf.writeUtf("");
        }
        buf.writeInt(manager.getFreeTickets(pPos));

        sendPacketToAllPlayers(pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET);
    }

    @Inject(method = "sendPoiRemovedPacket", at = @At("HEAD"))
    private static void vdr$sendPoiRemovedPacket(ServerLevel pLevel, BlockPos pPos, CallbackInfo ci) {

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pPos);
        sendPacketToAllPlayers(pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET);
    }

    @Inject(method = "sendPoiTicketCountPacket", at = @At("HEAD"))
    private static void vdr$sendPoiTicketCountPacket(ServerLevel pLevel, BlockPos pPos, CallbackInfo ci) {

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pPos);
        buf.writeInt(pLevel.getPoiManager().getFreeTickets(pPos));
        sendPacketToAllPlayers(pLevel, buf, ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET);
    }





}
