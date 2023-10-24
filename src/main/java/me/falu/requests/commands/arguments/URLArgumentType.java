package me.falu.requests.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.LiteralText;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class URLArgumentType implements ArgumentType<URL> {
    private static final Collection<String> EXAMPLES = Collections.singletonList("https://example.com");

    public static URLArgumentType create() {
        return new URLArgumentType();
    }

    public static URL getURL(CommandContext<?> context, String name) {
        return context.getArgument(name, URL.class);
    }

    @Override
    public URL parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        try {
            return new URL(reader.readQuotedString());
        } catch (MalformedURLException e) {
            reader.setCursor(start);
            throw new SimpleCommandExceptionType(new LiteralText("Invalid URL: " + e.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
