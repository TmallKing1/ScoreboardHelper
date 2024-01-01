package top.pigest.scoreboardhelper.gui.widget;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardExportScreen;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ScoreboardExportListWidget extends ElementListWidget<ScoreboardExportListWidget.Entry> {
    private final ScoreboardExportScreen parent;

    public ScoreboardExportListWidget(MinecraftClient minecraftClient, ScoreboardExportScreen parent) {
        super(minecraftClient, parent.width + 20, parent.height, 32, parent.height - 80, 25);
        this.parent = parent;
        for(ScoreboardExportScreen.RecordEntry entry: parent.getRecordEntries()) {
            this.addEntry(new Entry(entry));
        }
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private final ScoreboardExportScreen.RecordEntry entry;
        private final Text displayName;
        private final ButtonWidget deleteButton;
        private final ButtonWidget forwardButton;
        private final ButtonWidget backwardButton;

        Entry(ScoreboardExportScreen.RecordEntry entry) {
            List<ScoreboardExportScreen.RecordEntry> recordEntries = ScoreboardExportListWidget.this.parent.getRecordEntries();
            this.entry = entry;
            this.displayName = entry.getDisplayName();
            this.deleteButton = ButtonWidget.builder(Text.translatable("options.scoreboard-helper.export.delete"), button -> {
                ScoreboardExportListWidget widget = ScoreboardExportListWidget.this;
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index == 0 && size > 1) {
                    widget.children().get(1).setForwardButtonActive(false);
                }
                if(index == 1 && size == 2) {
                    widget.children().get(0).setBackwardButtonActive(false);
                }
                if(index == size - 1 && size > 1) {
                    widget.children().get(size - 2).setBackwardButtonActive(false);
                }
                widget.parent.getRecordEntries().remove(entry);
                widget.removeEntry(this);
                if(widget.getScrollAmount() > widget.getMaxScroll()) {
                    widget.setScrollAmount(widget.getMaxScroll());
                }
            }).dimensions(0, 0, 60, 20).build();
            this.forwardButton = ButtonWidget.builder(Text.literal("↑"), button -> {
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index > 0) {
                    Entry entry1 = ScoreboardExportListWidget.this.children().get(index - 1);
                    ScoreboardExportListWidget.this.children().set(index - 1, this);
                    ScoreboardExportListWidget.this.children().set(index, entry1);
                    ScoreboardExportScreen.RecordEntry recordEntry = recordEntries.get(index);
                    ScoreboardExportScreen.RecordEntry recordEntry1 = recordEntries.get(index - 1);
                    recordEntries.set(index - 1, recordEntry);
                    recordEntries.set(index, recordEntry1);
                    if(index == 1) {
                        setForwardButtonActive(false);
                        entry1.setForwardButtonActive(true);
                    }
                    if(index == size - 1) {
                        entry1.setBackwardButtonActive(false);
                    }
                }
                setBackwardButtonActive(true);
            }).dimensions(0, 0, 20, 20).build();
            this.backwardButton = ButtonWidget.builder(Text.literal("↓"), button -> {
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index < size - 1) {
                    Entry entry1 = ScoreboardExportListWidget.this.children().get(index + 1);
                    ScoreboardExportListWidget.this.children().set(index + 1, this);
                    ScoreboardExportListWidget.this.children().set(index, entry1);
                    ScoreboardExportScreen.RecordEntry recordEntry = recordEntries.get(index);
                    ScoreboardExportScreen.RecordEntry recordEntry1 = recordEntries.get(index + 1);
                    recordEntries.set(index + 1, recordEntry);
                    recordEntries.set(index, recordEntry1);
                    if(index == size - 2) {
                        setBackwardButtonActive(false);
                        entry1.setBackwardButtonActive(true);
                    }
                    if(index == 0) {
                        entry1.setForwardButtonActive(false);
                    }
                }
                setForwardButtonActive(true);
            }).dimensions(0, 0, 20, 20).build();
            setForwardButtonActive(false);
            setBackwardButtonActive(false);
            int index = getIndex();
            if(index > 0) {
                Entry entry1 = ScoreboardExportListWidget.this.children().get(index - 1);
                entry1.setBackwardButtonActive(true);
                setForwardButtonActive(true);
            }
        }

        private void setForwardButtonActive(boolean active) {
            this.forwardButton.active = active;
        }

        private void setBackwardButtonActive(boolean active) {
            this.backwardButton.active = active;
        }


        private int getIndex() {
            ScoreboardExportListWidget widget = ScoreboardExportListWidget.this;
            return widget.parent.getRecordEntries().indexOf(entry);
        }


        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.deleteButton, this.forwardButton, this.backwardButton);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.deleteButton, this.forwardButton, this.backwardButton);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(ScoreboardExportListWidget.this.client.textRenderer, this.displayName, x - 40, y + entryHeight / 2 - ScoreboardExportListWidget.this.client.textRenderer.fontHeight / 2, 0xFFFFFF, false);
            this.deleteButton.setX(x + 90);
            this.deleteButton.setY(y);
            this.deleteButton.render(context, mouseX, mouseY, tickDelta);
            this.forwardButton.setX(x + 90 + 60 + 5);
            this.forwardButton.setY(y);
            this.forwardButton.render(context, mouseX, mouseY, tickDelta);
            this.backwardButton.setX(x + 90 + 60 + 5 + 20 + 5);
            this.backwardButton.setY(y);
            this.backwardButton.render(context, mouseX, mouseY, tickDelta);
        }
    }
}
