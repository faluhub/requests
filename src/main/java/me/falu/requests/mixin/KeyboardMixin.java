package me.falu.requests.mixin;

import me.falu.requests.Requests;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract void debugError(String string, Object... objects);

    @Unique
    @SuppressWarnings("SameParameterValue")
    private void debugAppendable(String string, MutableText append, String suffix) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD)).append(" ").append(new TranslatableText(string)).append(append).append(suffix));
    }

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    private void addCustomDebug(int key, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && key == GLFW.GLFW_KEY_M) {
            if (!Requests.LAST_REQUEST.equals("")) {
                Path dumpPath = Requests.dumpRequest();
                if (dumpPath != null) {
                    MutableText fileText = new LiteralText(FabricLoader.getInstance().getGameDir().toAbsolutePath().relativize(dumpPath).toString())
                            .formatted(Formatting.UNDERLINE)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, dumpPath.toFile().toString())));
                    this.debugAppendable("Dumped last request at ", fileText, ".");
                } else {
                    this.debugError("Unable to dump last request.");
                }
            } else {
                this.debugError("There is no request to dump.");
            }
            cir.setReturnValue(true);
        }
    }
}
