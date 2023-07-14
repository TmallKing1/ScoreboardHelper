package top.pigest.scoreboardhelper.util.export;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.pigest.scoreboardhelper.config.Property;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoreboardExportScreen extends Screen {
    private final Screen parent;
    private final ExportSettings settings = new ExportSettings();
    private final int TITLE_Y = 30;
    public ScoreboardExportScreen(Screen parent) {
        super(Text.translatable(getTranslationKey("title", TranslationKeyType.NORMAL)));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("start", TranslationKeyType.NORMAL)), button -> {
            tryExport();
            close();
        }).size(200, 20).position(width / 2 - 10 - 200, height - 40).build());
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("cancel", TranslationKeyType.NORMAL)), button -> close()).size(200, 20).position(width / 2 + 10, height - 40).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, TITLE_Y, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    private static String getTranslationKey(String key, TranslationKeyType keyType) {
        switch (keyType) {
            case NORMAL -> {
                return "options.scoreboard-helper.export." + key;
            }
            case TOOLTIP -> {
                return "options.scoreboard-helper.export." + key + ".tooltip";
            }
        }
        return "options.scoreboard-helper.export." + key;
    }

    private ButtonWidget createBooleanPropertyButton(int x, int y, int width, int height, Property<Boolean> property) {
        Text text = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.NORMAL));
        Text toolTip = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.TOOLTIP));
        return ButtonWidget.builder(ScreenTexts.composeToggleText(text, property.getValue()), button -> {
            boolean newValue = !property.getValue();
            button.setMessage(ScreenTexts.composeToggleText(text, newValue));
            property.setValue(newValue);
        }).tooltip(Tooltip.of(toolTip)).size(width, height).position(x, y).build();
    }

    private void tryExport() {
        if (client != null && client.player != null) {
            Scoreboard scoreboard = client.player.getScoreboard();
            ScoreboardObjective scoreboardObjective = null;
            Team team = scoreboard.getPlayerTeam(client.player.getEntityName());
            if (team != null) {
                if (team.getColor().getColorIndex() >= 0) {
                    scoreboardObjective = scoreboard.getObjectiveForSlot(3 + team.getColor().getColorIndex());
                }
            }
            scoreboardObjective = scoreboardObjective == null ? scoreboard.getObjectiveForSlot(1) : scoreboardObjective;
            if(scoreboardObjective == null) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.inactive"));
            } else {
                export(scoreboardObjective, scoreboard);
            }
        }
    }

    private void export(ScoreboardObjective scoreboardObjective, Scoreboard scoreboard) {
        List<ScoreboardPlayerScore> scores = new ArrayList<>(scoreboard.getAllPlayerScores(scoreboardObjective));
        String name = scoreboardObjective.getName() + ".csv";
        Path path = FabricLoader.getInstance().getGameDir().resolve("scoreboard-exports");
        File file = path.resolve(name).toFile();
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                if (client != null && client.player != null) {
                    client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    e.printStackTrace();
                }
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder p = new StringBuilder();
            p.append("Player Name,Score\n");
            for(int i = scores.size() - 1; i >= 0; i--) {
                ScoreboardPlayerScore score = scores.get(i);
                p.append(score.getPlayerName()).append(",").append(score.getScore()).append("\n");
            }
            writer.write(String.valueOf(p));
            Text text = Text.literal(name).setStyle(Style.EMPTY.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
            MutableText text1 = Text.translatable("hint.scoreboard-helper.export.success", text);
            if (client != null) {
                if (client.player != null) {
                    client.player.sendMessage(text1);
                }
            }
        } catch (IOException e) {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                e.printStackTrace();
            }
        }
    }
}
