package me.falu.requests.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.falu.requests.Requests;
import me.falu.requests.commands.arguments.JsonObjectArgumentType;
import me.falu.requests.commands.arguments.RequestMethodArgumentType;
import me.falu.requests.commands.arguments.URLArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestCommand {
    private static final Gson GSON = new GsonBuilder().create();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager
                        .literal("request")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager
                                .argument("method", RequestMethodArgumentType.create())
                                .then(CommandManager
                                        .argument("url", URLArgumentType.create())
                                        .then(CommandManager
                                                .argument("data", JsonObjectArgumentType.create())
                                                .executes(RequestCommand::execute)
                                        )
                                        .executes(RequestCommand::execute)
                                )
                        )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        new Thread(() -> {
            String method = RequestMethodArgumentType.getRequestMethod(context, "method");
            URL url = URLArgumentType.getURL(context, "url");
            JsonObject data = JsonObjectArgumentType.getJsonObject(context, "data");
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.connect();
                if (data != null) {
                    try (OutputStream out = connection.getOutputStream()) {
                        out.write(GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
                    }
                }
                List<String> lines = IOUtils.readLines(connection.getInputStream(), StandardCharsets.UTF_8);
                Requests.LAST_REQUEST = "";
                for (String line : lines) {
                    Requests.log(line);
                    Requests.LAST_REQUEST += line + "\n";
                    if (context.getSource().getMinecraftServer().getGameRules().getBoolean(Requests.REQUEST_FEEDBACK)) {
                        context.getSource().sendFeedback(new LiteralText(line), false);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Requests.LOGGER.error("Error while making request:", e);
            }
        }).start();
        return 1;
    }
}
