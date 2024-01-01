package top.pigest.scoreboardhelper.gui.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import top.pigest.scoreboardhelper.ScoreboardHelper;
import top.pigest.scoreboardhelper.gui.widget.ScoreboardExportListWidget;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScoreboardExportScreen extends Screen {
    public static final ScoreboardExportScreen INSTANCE = new ScoreboardExportScreen(null);
    private Screen parent;
    private final List<RecordEntry> entries = new ArrayList<>();
    private ScoreboardExportListWidget scoreboardExportListWidget;

    public ScoreboardExportScreen(Screen parent) {
        super(Text.translatable(getTranslationKey("title")));
        this.parent = parent;
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.scoreboardExportListWidget = new ScoreboardExportListWidget(client, this);
        addSelectableChild(this.scoreboardExportListWidget);

        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("record")), button -> {
            boolean returnVal = record();
            if (!returnVal) {
                close();
            }
        }).size(200, 20).position(width / 2 - 10 - 200, height - 40 - 30).build());
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("direct")), button -> {
            tryExport();
            close();
        }).size(200, 20).position(width / 2 - 10 - 200, height - 40).build());
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("finish")), button -> {
            exportAll();
            close();
        }).size(200, 20).position(width / 2 + 10, height - 40 - 30).build());
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("close")), button -> close()).size(200, 20).position(width / 2 + 10, height - 40).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.scoreboardExportListWidget.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, TITLE_Y, 0xFFFFFF);
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    private static String getTranslationKey(String key) {
        return "options.scoreboard-helper.export." + key;
    }

    public void refresh() {
        this.clearAndInit();
    }

    private boolean record() {
        if (client != null && client.player != null) {
            Scoreboard scoreboard = client.player.getScoreboard();
            ScoreboardObjective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
            if(scoreboardObjective == null) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                return false;
            } else {
                List<ScoreboardPlayerScore> scores = new ArrayList<>(scoreboard.getAllPlayerScores(scoreboardObjective));
                RecordEntry entry = new RecordEntry(scoreboardObjective.getDisplayName());
                for(ScoreboardPlayerScore score: scores) {
                    entry.scores.add(new Pair<>(score.getPlayerName(), score.getScore()));
                }
                entries.removeIf(entry1 -> entry1.displayName.equals(scoreboardObjective.getDisplayName()));
                entries.add(entry);
                this.refresh();
                return true;
            }
        }
        return false;
    }

    private void tryExport() {
        if (client != null && client.player != null) {
            Scoreboard scoreboard = client.player.getScoreboard();
            ScoreboardObjective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
            if(scoreboardObjective == null) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(Formatting.RED)));
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
                    ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                }
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder p = new StringBuilder();
            p.append(Text.translatable(getTranslationKey("chart.player")).getString())
                    .append(",")
                    .append(Text.translatable(getTranslationKey("chart.score")).getString())
                    .append("\n");
            for(int i = scores.size() - 1; i >= 0; i--) {
                ScoreboardPlayerScore score = scores.get(i);
                p.append(score.getPlayerName()).append(",").append(score.getScore()).append("\n");
            }
            writer.write(String.valueOf(p));
            sendSuccessMessage(name, file);
        } catch (IOException e) {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
            }
        }
    }

    private void sendSuccessMessage(String name, File file) {
        Text text = Text.literal(name).setStyle(Style.EMPTY.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
        MutableText text1 = Text.translatable("hint.scoreboard-helper.export.success", text);
        if (client != null) {
            if (client.player != null) {
                client.player.sendMessage(text1);
            }
        }
    }

    private void exportAll() {
        if (client != null && client.player != null) {
            if (this.entries.isEmpty()) {
                client.player.sendMessage(Text.translatable("hint.scoreboard-player.export.fail.no_entry").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            } else {
                Set<String> playerNames = new TreeSet<>();
                for(RecordEntry entry: entries) {
                    for(Pair<String, Integer> pair: entry.scores) {
                        playerNames.add(pair.getLeft());
                    }
                }
                List<String> export = new ArrayList<>();
                StringBuilder head = new StringBuilder(Text.translatable(getTranslationKey("chart.player")).getString());
                for(RecordEntry entry: entries) {
                    head.append(",").append(entry.displayName.getString());
                }
                export.add(head.toString());
                for(String playerName: playerNames) {
                    StringBuilder s = new StringBuilder(playerName);
                    for(RecordEntry entry: entries) {
                        s.append(",");
                        Optional<Pair<String, Integer>> optional = entry.scores.stream().filter(pair -> pair.getLeft().equals(playerName)).findFirst();
                        optional.ifPresent(stringIntegerPair -> s.append(stringIntegerPair.getRight()));
                    }
                    export.add(s.toString());
                }
                String name = "EXPORT-" + UUID.randomUUID() + ".csv";
                Path path = FabricLoader.getInstance().getGameDir().resolve("scoreboard-exports");
                File file = path.resolve(name).toFile();
                if(!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                        ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                    }
                }
                try (FileWriter writer = new FileWriter(file)) {
                    for (String s: export){
                        writer.write(s + "\n");
                    }
                    sendSuccessMessage(name, file);
                } catch (IOException e) {
                    if (client != null && client.player != null) {
                        client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                        ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                    }
                }
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
    }

    public List<RecordEntry> getRecordEntries() {
        return entries;
    }

    public static class RecordEntry {
        private final Text displayName;
        public final List<Pair<String, Integer>> scores = new ArrayList<>();

        public RecordEntry(Text displayName) {
            this.displayName = displayName;
        }

        public Text getDisplayName() {
            return displayName;
        }
    }
}
