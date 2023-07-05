package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.pigest.scoreboardhelper.util.Constants;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onScoreboardObjectiveUpdate", at = @At(value = "HEAD"))
    private void injectedUpdate(ScoreboardObjectiveUpdateS2CPacket packet, CallbackInfo ci) {
        Constants.PAGE = 0;
    }

    @Inject(method = "onScoreboardDisplay", at = @At(value = "HEAD"))
    private void injectedDisplay(ScoreboardDisplayS2CPacket packet, CallbackInfo ci) {
        Constants.PAGE = 0;
    }
}
