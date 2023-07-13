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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledHeight;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Unique
    private int j;

    @Unique
    private Collection<ScoreboardPlayerScore> collection;

    @Unique
    private Text text;

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected0(DrawContext context, ScoreboardObjective objective, CallbackInfo ci, Scoreboard scoreboard, Collection<ScoreboardPlayerScore> collection, List list, List<Pair<ScoreboardPlayerScore, MutableText>> list2, Text text, int i, int j) {
        this.j = j;
        this.text = text;
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
        this.collection = collection2;
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
            int o = (int) (ScoreboardHelperConfig.INSTANCE.sidebarTextOpacity.getValue() * 256);
            return context.drawText(textRenderer, text, x, y, 0xFFFFFF + (o << 24), shadow);
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
            case LEFT, LEFT_LOWER_CORNER, LEFT_UPPER_CORNER -> {
                return 3;
            }
            case RIGHT, RIGHT_LOWER_CORNER, RIGHT_UPPER_CORNER -> {
                return s;
            }
        }
        return s;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"), ordinal = 12)
    private int injected7(int u) {
        switch (ScoreboardHelperConfig.INSTANCE.sidebarPosition.getValue()) {
            case LEFT, LEFT_LOWER_CORNER, LEFT_UPPER_CORNER -> {
                return 3 + j + 2;
            }
            case RIGHT, RIGHT_LOWER_CORNER, RIGHT_UPPER_CORNER -> {
                return u;
            }
        }
        return u;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"), ordinal = 4)
    private int injected8(int m) {
        int returnValue = m;
        switch (ScoreboardHelperConfig.INSTANCE.sidebarPosition.getValue()) {
            case RIGHT_LOWER_CORNER, LEFT_LOWER_CORNER -> returnValue = this.scaledHeight - 3;
            case RIGHT_UPPER_CORNER, LEFT_UPPER_CORNER -> returnValue = 3 + this.getTextRenderer().fontHeight * (collection.size() + 1);
        }
        return returnValue;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE"), ordinal = 11)
    private int injected9(int t) {
        return t + ScoreboardHelperConfig.INSTANCE.sidebarYOffset.getValue();
    }

    @ModifyConstant(method = "renderScoreboardSidebar", constant = @Constant(floatValue = 0.3f))
    private float injected10(float constant) {
        return ScoreboardHelperConfig.INSTANCE.sidebarBackgroundOpacity.getValue().floatValue();
    }

    @ModifyConstant(method = "renderScoreboardSidebar", constant = @Constant(floatValue = 0.4f))
    private float injected11(float constant) {
        return ScoreboardHelperConfig.INSTANCE.sidebarBackgroundTitleOpacity.getValue().floatValue();
    }

    @ModifyArgs(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private void injected12(Args args) {
        Text text = args.get(1);
        int o;
        if(text.equals(this.text)) {
            o = (int) (ScoreboardHelperConfig.INSTANCE.sidebarTitleTextOpacity.getValue() * 256);
        } else {
            o = (int) (ScoreboardHelperConfig.INSTANCE.sidebarTextOpacity.getValue() * 256);
        }
        args.set(4, 0xFFFFFF + (o << 24));
    }
}
