package me.falu.requests.commands.arguments;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.Collections;

public class JsonObjectArgumentType implements ArgumentType<JsonObject> {
    private static final Collection<String> EXAMPLES = Collections.singletonList("{\"foo\": \"bar\"}");
    private static final JsonParser PARSER = new JsonParser();

    public static JsonObjectArgumentType create() {
        return new JsonObjectArgumentType();
    }

    public static JsonObject getJsonObject(CommandContext<?> context, String name) {
        try {
            return context.getArgument(name, JsonObject.class);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public JsonObject parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String message;
        try {
            return PARSER.parse(reader.readString()).getAsJsonObject();
        } catch (JsonParseException ignored) {
            message = "Invalid JSON data";
        } catch (IllegalStateException ignored) {
            message = "JSON data must be an object";
        }
        reader.setCursor(start);
        throw new SimpleCommandExceptionType(new LiteralText(message)).createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
