package com.vivi.vanilladebugrenderers.mixin;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mixin(Path.class)
public class PathMixin {

    @Shadow
    private Node[] openSet;

    @Shadow
    private Node[] closedSet;

    @Shadow
    private Set<Target> targetNodes;

    @Inject(method = "writeToStream", at = @At("HEAD"))
    public void vdr$writeToStream(CallbackInfo ci) {

        List<Node> openNodes = new ArrayList<>();
        List<Node> closedNodes = new ArrayList<>();

        Path path = (Path) (Object) this;
        for(int i = 0; i < path.getNodeCount(); i++) {
            Node node = path.getNode(i);
            if(node.closed) {
                closedNodes.add(node);
            }
            else {
                openNodes.add(node);
            }
        }
        openSet = openNodes.toArray(new Node[0]);
        closedSet = closedNodes.toArray(new Node[0]);

        //targetNodes doesn't seem to be used at all, but there needs to be a value here or else nothing is written to buffer.
        targetNodes = Collections.singleton(new Target(0, 0, 0));
    }
}
