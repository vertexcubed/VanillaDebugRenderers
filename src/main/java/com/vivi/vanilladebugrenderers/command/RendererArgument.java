package com.vivi.vanilladebugrenderers.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RendererArgument implements ArgumentType<RendererType> {
    private static final Collection<String> EXAMPLES = Stream.of(RendererType.PATHFINDING, RendererType.BRAIN).map(RendererType::getSerializedName).collect(Collectors.toList());
    private static final RendererType[] VALUES = RendererType.values();
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_260119_) -> Component.translatable("argument.renderer.invalid", p_260119_));



    @Override
    public RendererType parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        RendererType type = RendererType.byName(s, null);
        if(type == null) {
            throw ERROR_INVALID.createWithContext(reader, s);
        }
        return type;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        return pContext.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(RendererType::getSerializedName), pBuilder) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static RendererArgument rendererArgument() {
        return new RendererArgument();
    }
}
