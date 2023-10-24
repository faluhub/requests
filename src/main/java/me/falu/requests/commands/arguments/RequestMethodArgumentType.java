package me.falu.requests.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.LiteralText;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RequestMethodArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("GET", "POST");
    private static final String[] METHODS = new String[] {
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS",
            "HEAD",
            "TRACE"
    };

    public static RequestMethodArgumentType create() {
        return new RequestMethodArgumentType();
    }

    public static String getRequestMethod(CommandContext<?> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String result = reader.readUnquotedString().toUpperCase();
        if (Arrays.stream(METHODS).noneMatch(s -> s.equals(result))) {
            reader.setCursor(start);
            throw new SimpleCommandExceptionType(new LiteralText("Invalid request method.")).createWithContext(reader);
        }
        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String method : METHODS) {
            builder = builder.suggest(method);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
