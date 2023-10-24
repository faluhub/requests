package me.falu.requests;

import com.google.gson.*;
import me.falu.requests.mixin.access.BooleanRuleAccessor;
import me.falu.requests.mixin.access.GameRulesAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.world.GameRules;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Random;

public class Requests implements ModInitializer {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("requests").orElseThrow(RuntimeException::new);
    public static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();
    public static final String MOD_VERSION = String.valueOf(MOD_CONTAINER.getMetadata().getVersion());
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser PARSER = new JsonParser();

    public static final GameRules.Key<GameRules.BooleanRule> REQUEST_FEEDBACK = GameRulesAccessor.invokeRegister("requestFeedback", GameRules.Category.CHAT, BooleanRuleAccessor.invokeCreate(true));
    public static String LAST_REQUEST = "";

    private static Path getRequestsPath() {
        Path path = FabricLoader.getInstance().getGameDir().resolve("requests");
        boolean ignored = path.toFile().mkdirs();
        return path;
    }

    public static Path dumpRequest() {
        boolean isJson = false;
        String string = LAST_REQUEST;
        try {
            string = GSON.toJson(PARSER.parse(LAST_REQUEST));
            isJson = true;
        } catch (JsonSyntaxException ignored) {}
        Path requestsPath = getRequestsPath();
        int id = Math.abs(new Random().nextInt());
        File requestFile = requestsPath.resolve("request-" + id + (isJson ? "-pretty" : "") + ".txt").toFile();
        try {
            if (requestFile.createNewFile()) {
                FileUtils.writeStringToFile(requestFile, string, StandardCharsets.UTF_8);
                return requestFile.toPath();
            }
        } catch (IOException e) {
            LOGGER.error("Unable to dump request:", e);
        }
        return null;
    }

    public static void log(Object msg) {
        LOGGER.log(Level.INFO, msg);
    }

    @Override
    public void onInitialize() {
        log("Using " + MOD_NAME + " v" + MOD_VERSION);
    }
}
