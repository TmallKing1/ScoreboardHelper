package top.pigest.scoreboardhelper.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class ScoreboardHelperInfoScreen extends Screen {
    private final Screen parent;
    private final Text errorMessage;

    public ScoreboardHelperInfoScreen(Screen parent, Text errorMessage) {
        this(parent, InfoType.INFO, errorMessage);
    }

    public ScoreboardHelperInfoScreen(Screen parent, InfoType type, Text errorMessage) {
        super(type.title);
        this.parent = parent;
        this.errorMessage = errorMessage;
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new ButtonWidget.Builder(Text.translatable("gui.back"), button -> close()).dimensions(width / 2 - 60, height / 2 + 40, 120, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 50, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, errorMessage, width / 2, height / 2 - 10, 0xFFFFFF);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        parent.renderBackground(context, mouseX, mouseY, delta);
    }

    public enum InfoType {
        ERROR(Text.translatable("hint.scoreboard-helper.error").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true))),
        INFO(Text.translatable("hint.scoreboard-helper.info").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withBold(true)));

        private final Text title;
        InfoType(Text title) {
            this.title = title;
        }
    }
}
