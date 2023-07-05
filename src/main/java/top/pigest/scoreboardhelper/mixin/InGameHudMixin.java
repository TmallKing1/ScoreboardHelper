package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"), cancellable = true)
    private void injected1(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"))
    private Collection<ScoreboardPlayerScore> injected2(Collection<ScoreboardPlayerScore> collection, MatrixStack matrices, ScoreboardObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection1 = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> list = collection1.stream().filter((score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
        Collection<ScoreboardPlayerScore> collection2;
        int p = collection1.size() - ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue() - Constants.PAGE;
        if (p < 0) {
            p = 0;
        }
        int q = p + ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue();
        if(q > collection1.size()) {
            q = collection1.size();
        }
        collection2 = list.subList(p, q);
        return collection2;
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Ljava/lang/Integer;toString(I)Ljava/lang/String;"))
    private String injected3(int buf) {
        if(ScoreboardHelperConfig.INSTANCE.sidebarScoreShown.getValue()) {
            return Integer.toString(buf);
        } else{
            return "";
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int injected4(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color) {
        if(ScoreboardHelperConfig.INSTANCE.sidebarScoreShown.getValue()) {
            return instance.draw(matrices, text, x, y, color);
        } else{
            return 0;
        }
    }

    @ModifyConstant(method = "renderScoreboardSidebar", constant = @Constant(stringValue = ": "))
    private String injected5(String constant) {
        if(ScoreboardHelperConfig.INSTANCE.sidebarScoreShown.getValue()) {
            return constant;
        } else{
            return "";
        }
    }

}
