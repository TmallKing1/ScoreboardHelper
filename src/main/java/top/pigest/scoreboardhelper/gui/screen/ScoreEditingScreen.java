package top.pigest.scoreboardhelper.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import top.pigest.scoreboardhelper.config.ScoreSortingMethod;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.gui.widget.EditingScoreListWidget;
import top.pigest.scoreboardhelper.util.ScoreModification;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreEditingScreen extends Screen {
    private final Screen parent;
    private final List<SingleScore> scores;
    private SortMethod sortMethod;
    private boolean reversedSort;
    private EditingScoreListWidget widget;
    private ButtonWidget submitButton;
    private ButtonWidget reversedSortButton;
    public ButtonWidget addButton;
    public ScoreEditingScreen(Screen parent, @NotNull Scoreboard scoreboard, @NotNull ScoreboardObjective objective) {
        super(Text.translatable(getTranslationKey("title"), objective.getDisplayName()));
        this.parent = parent;
        this.scores = scoreboard.getScoreboardEntries(objective).stream().map(score -> new SingleScore(score.owner(), score.value())).collect(Collectors.toList());
        ScoreSortingMethod sorting = ScoreboardHelperConfig.INSTANCE.sortingMethod.getValue();
        reversedSort = switch (sorting) {
            case BY_SCORE_DESC, BY_NAME_DESC -> true;
            case BY_SCORE_ASC, BY_NAME_ASC -> false;
        };
        sortMethod = switch (sorting) {
            case BY_SCORE_ASC, BY_SCORE_DESC -> SortMethod.SCORE;
            case BY_NAME_DESC, BY_NAME_ASC -> SortMethod.NAME;
        };
    }

    @Override
    public void tick() {
        if (client != null && client.player != null) {
            if (!client.player.hasPermissionLevel(2)) {
                if (this.submitButton.active) {
                    this.submitButton.active = false;
                    this.submitButton.setTooltip(Tooltip.of(Text.translatable("hint.scoreboard-helper.edit_score.permission_denied")));
                }
            } else {
                if (!this.submitButton.active) {
                    this.submitButton.active = true;
                    this.submitButton.setTooltip(null);
                }
            }
        }
    }

    @Override
    protected void init() {
        CyclingButtonWidget<SortMethod> buttonWidget = CyclingButtonWidget.<SortMethod>builder(value -> Text.translatable("options.scoreboard-helper.edit_score.sort_method." + value.toString()))
                .values(SortMethod.values())
                .initially(sortMethod)
                .build(width / 2 - 10 - 200, height - 60, 170, 20, Text.translatable(getTranslationKey("sort_method")), ((button, value) -> {
                    this.sortMethod = value;
                    this.reversedSortButton.active = this.sortMethod != SortMethod.NONE;
                    this.widget.resort();
                }));
        this.widget = new EditingScoreListWidget(client, this);
        addSelectableChild(widget);
        addDrawableChild(buttonWidget);
        this.reversedSortButton = new ButtonWidget.Builder(this.reversedSort ? Text.literal("↑") : Text.literal("↓"), this::switchReversedSort).size(20, 20).position(width / 2 - 10 - 20, height - 60).build();
        addDrawableChild(this.reversedSortButton);
        this.addButton = new ButtonWidget.Builder(Text.translatable(getTranslationKey("add")), this::addEntry).size(200, 20).position(width / 2 + 10, height - 60).build();
        addDrawableChild(this.addButton);
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("close")), button -> close()).size(200, 20).position(width / 2 + 10, height - 30).build());
        this.submitButton = new ButtonWidget.Builder(Text.translatable(getTranslationKey("save")), button -> submit()).size(200, 20).position(width / 2 - 10 - 200, height - 30).build();
        addDrawableChild(this.submitButton);
    }

    public SortMethod getSortMethod() {
        return sortMethod;
    }

    public boolean isReversedSort() {
        return reversedSort;
    }

    private static String getTranslationKey(String key) {
        return "options.scoreboard-helper.edit_score." + key;
    }

    private void switchReversedSort(ButtonWidget buttonWidget) {
        this.reversedSort = !this.reversedSort;
        if (this.reversedSort) {
            buttonWidget.setMessage(Text.literal("↑"));
        } else {
            buttonWidget.setMessage(Text.literal("↓"));
        }
        this.widget.resort();
    }

    private void addEntry(ButtonWidget buttonWidget) {
        SingleScore entry = new SingleScore("", 0);
        this.scores.add(entry);
        this.widget.addSingleScore(entry);
        this.widget.setScrollAmount(this.widget.getMaxScroll());
        this.addButton.active = false;
    }

    private void submit() {
        if (client != null && client.world != null && client.player != null) {
            Scoreboard sb = client.world.getScoreboard();
            ScoreboardObjective objective = ScoreboardHelperUtils.getSidebarObjective(sb, client.player);
            List<ScoreModification> scoreModifications = getScoreModifications(sb, objective);
            for (ScoreModification m: scoreModifications) {
                client.player.networkHandler.sendChatCommand(m.getModificationCommand());
            }
            if (scoreModifications.isEmpty()) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.edit_score.fail.no_changes").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
            close();
        }
    }

    @NotNull
    private List<ScoreModification> getScoreModifications(Scoreboard sb, ScoreboardObjective objective) {
        List<ScoreModification> scoreModifications = new ArrayList<>();
        List<SingleScore> list = sb.getScoreboardEntries(objective).stream().map(score -> new SingleScore(score.owner(), score.value())).toList();
        Map<String, SingleScore> map = new HashMap<>();
        for (SingleScore score: scores) {
            map.put(score.name, score);
        }
        for (SingleScore score: list) {
            SingleScore singleScore = map.get(score.name);
            if (singleScore != null) {
                if (singleScore.score != score.score) {
                    scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.CHANGE, objective, singleScore.name, singleScore.score));
                }
                map.remove(score.name);
            } else {
                scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.REMOVE, objective, score.name, 0));
            }
        }
        for (Map.Entry<String, SingleScore> entry: map.entrySet()) {
            if (!entry.getValue().name.isEmpty()) {
                scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.ADD, objective, entry.getValue().name, entry.getValue().score));
            }
        }
        return scoreModifications;
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.widget.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, TITLE_Y, 0xFFFFFF);
    }

    public List<SingleScore> getScores() {
        return scores;
    }

    public enum SortMethod {
        NONE("none"),
        SCORE("score"),
        NAME("name");

        final String string;

        SortMethod(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static class SingleScore {
        private String name;
        private int score;
        public SingleScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
