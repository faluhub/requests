package me.falu.requests.mixin.access;

import net.minecraft.world.GameRules;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanRule.class)
public interface BooleanRuleAccessor {
    @Invoker("create")
    static GameRules.Type<GameRules.BooleanRule> invokeCreate(boolean initialValue) {
        throw new NotImplementedException("");
    }
}
