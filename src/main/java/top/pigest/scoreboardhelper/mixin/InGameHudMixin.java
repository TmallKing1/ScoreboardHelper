package top.pigest.scoreboardhelper.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private int j;

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected0(DrawContext context, ScoreboardObjective objective, CallbackInfo ci, Scoreboard scoreboard, Collection<ScoreboardPlayerScore> collection, List list, List<Pair<ScoreboardPlayerScore, MutableText>> list2, Text text, int i, int j) {
        this.j = j;
    }


    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"), cancellable = true)
    private void injected1(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"))
    private Collection<ScoreboardPlayerScore> injected2(Collection<ScoreboardPlayerScore> collection, DrawContext context, ScoreboardObjective objective) {
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

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    private int injected4(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
        if(ScoreboardHelperConfig.INSTANCE.sidebarScoreShown.getValue()) {
            return context.drawText(textRenderer, text, x, y, color, shadow);
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

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"), ordinal = 10)
    private int injected6(int s) {
        switch (ScoreboardHelperConfig.INSTANCE.sidebarPosition.getValue()) {
            case LEFT -> {
                return 3;
            }
            case RIGHT -> {
                return s;
            }
        }
        return s;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"), ordinal = 12)
    private int injected7(int u) {
        switch (ScoreboardHelperConfig.INSTANCE.sidebarPosition.getValue()) {
            case LEFT -> {
                return 3 + j + 2;
            }
            case RIGHT -> {
                return u;
            }
        }
        return u;
    }
}
