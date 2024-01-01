package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.Constants;
import top.pigest.scoreboardhelper.util.SidebarEntry;

import java.util.Collection;
import java.util.Comparator;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledHeight;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledWidth;

    /**
     * @author xiaozhu_zhizui
     * @reason 重写侧边栏渲染方法，以适用配置
     */
    @Overwrite
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        ScoreboardHelperConfig config = ScoreboardHelperConfig.INSTANCE;
        if (!config.scoreboardShown.getValue()) {
            return;
        }
        int i;
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);
        Comparator<ScoreboardEntry> comparatorString = Comparator.comparing(entry -> entry.name().getLiteralString(), String.CASE_INSENSITIVE_ORDER);
        Comparator<ScoreboardEntry> comparator = switch (config.sortingMethod.getValue()) {
            case BY_SCORE_DESC -> Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_SCORE_ASC -> Comparator.comparing(ScoreboardEntry::value).thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_NAME_DESC -> comparatorString.reversed().thenComparing(ScoreboardEntry::value);
            case BY_NAME_ASC -> comparatorString.thenComparing(ScoreboardEntry::value);
        };
        Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(objective);
        SidebarEntry[] sidebarEntries = entries.stream().filter(score -> !score.hidden()).sorted(comparator).skip(Constants.PAGE + config.maxShowCount.getValue() > entries.size() ? Math.max(entries.size() - config.maxShowCount.getValue(), 0) : Constants.PAGE).limit(config.maxShowCount.getValue()).map(scoreboardEntry -> {
            Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.owner());
            Text text = scoreboardEntry.name();
            MutableText text2 = Team.decorateName(team, text);
            MutableText text3 = scoreboardEntry.formatted(numberFormat);
            int width = this.getTextRenderer().getWidth(text3);
            return new SidebarEntry(text2, text3, width);
        }).toArray(SidebarEntry[]::new);
        Text text = objective.getDisplayName();
        int j = i = this.getTextRenderer().getWidth(text);
        int joinerWidth = this.getTextRenderer().getWidth(": ");
        for (SidebarEntry sidebarEntry : sidebarEntries) {
            j = Math.max(j, this.getTextRenderer().getWidth(sidebarEntry.name()) + (sidebarEntry.scoreWidth() > 0 && config.sidebarScoreShown.getValue() ? joinerWidth + sidebarEntry.scoreWidth() : 0));
        }
        int finalJ = j;
        context.draw(() -> {
            int length = sidebarEntries.length;
            int m = switch(config.sidebarPosition.getValue()) {
                case LEFT, RIGHT -> this.scaledHeight / 2 + length * 3;
                case LEFT_UPPER_CORNER, RIGHT_UPPER_CORNER -> (length + 1) * this.getTextRenderer().fontHeight + 2;
                case LEFT_LOWER_CORNER, RIGHT_LOWER_CORNER -> this.scaledHeight - 2;
            };
            m += config.sidebarYOffset.getValue();
            int o = switch (config.sidebarPosition.getValue()) {
                case LEFT, LEFT_LOWER_CORNER, LEFT_UPPER_CORNER -> 5;
                case RIGHT, RIGHT_LOWER_CORNER, RIGHT_UPPER_CORNER -> this.scaledWidth - finalJ - 3;
            };
            int p = o + finalJ + 2;
            int backgroundOpacity = this.client.options.getTextBackgroundColor(config.sidebarBackgroundOpacity.getValue().floatValue());
            int titleBackgroundOpacity = this.client.options.getTextBackgroundColor(config.sidebarBackgroundTitleOpacity.getValue().floatValue());
            int splitLine = m - length * this.getTextRenderer().fontHeight;
            context.fill(o - 2, splitLine - this.getTextRenderer().fontHeight - 1, p, splitLine - 1, titleBackgroundOpacity);
            context.fill(o - 2, splitLine - 1, p, m, backgroundOpacity);
            int titleColor = 0xFFFFFF + ((int) (ScoreboardHelperConfig.INSTANCE.sidebarTitleTextOpacity.getValue() * 256) << 24);
            int textColor = 0xFFFFFF + ((int) (ScoreboardHelperConfig.INSTANCE.sidebarTextOpacity.getValue() * 256) << 24);
            context.drawText(this.getTextRenderer(), text, o + finalJ / 2 - i / 2, splitLine - this.getTextRenderer().fontHeight, titleColor, false);
            for (int t = 0; t < length; ++t) {
                SidebarEntry sidebarEntry = sidebarEntries[t];
                int u = m - (length - t) * this.getTextRenderer().fontHeight;
                context.drawText(this.getTextRenderer(), sidebarEntry.name(), o, u, textColor, false);
                if (config.sidebarScoreShown.getValue()) {
                    context.drawText(this.getTextRenderer(), sidebarEntry.score(), p - sidebarEntry.scoreWidth(), u, textColor, false);
                }
            }
        });
    }

}
