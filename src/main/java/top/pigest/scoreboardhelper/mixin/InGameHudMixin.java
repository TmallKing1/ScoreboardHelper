package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.pigest.scoreboardhelper.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"), cancellable = true)
    private void ij(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if(!Constants.SHOW) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"))
    private Collection<ScoreboardPlayerScore> injected(Collection<ScoreboardPlayerScore> collection, DrawContext context, ScoreboardObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection1 = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> list = collection1.stream().filter((score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
        Collection<ScoreboardPlayerScore> collection2;
        int p = collection1.size() - Constants.MAX_DISPLAY_COUNT - Constants.PAGE;
        if (p < 0) {
            p = 0;
        }
        int q = p + Constants.MAX_DISPLAY_COUNT;
        if(q > collection1.size()) {
            q = collection1.size();
        }
        collection2 = list.subList(p, q);
        return collection2;
    }

}
