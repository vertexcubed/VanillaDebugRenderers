package com.vivi.vanilladebugrenderers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.vivi.vanilladebugrenderers.ModDebugRenderer;
import com.vivi.vanilladebugrenderers.VanillaDebugRenderers;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ClientCommandSourceStack;

public class VDRCommand {

    public static <T> void register(CommandDispatcher<T> dispatcher, CommandBuildContext buildContext) {

        dispatcher.register(LiteralArgumentBuilder.<T>literal("vdr")
                .then(LiteralArgumentBuilder.<T>literal("toggle")
                        .then(RequiredArgumentBuilder.<T, RendererType>argument("renderer", RendererArgument.rendererArgument())
                                .executes(ctx -> {
                                    return toggleRenderer(ctx, ctx.getArgument("renderer", RendererType.class));
                                })

                        )
                )

        );

    }

    private static <T> int toggleRenderer(CommandContext<T> ctx, RendererType type) {
        ModDebugRenderer.getInstance().toggleValue(type);
        return Command.SINGLE_SUCCESS;
    }
}
