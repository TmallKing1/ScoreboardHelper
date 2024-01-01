package top.pigest.scoreboardhelper.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.config.property.Property;
import top.pigest.scoreboardhelper.gui.widget.PropertyListWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

import java.util.Objects;

public class ScoreboardHelperConfigScreen extends Screen {
    private final Screen parent;
    private final ScoreboardHelperConfig config;
    private PropertyListWidget propertyList;

    public ScoreboardHelperConfigScreen(Screen parent, ScoreboardHelperConfig config) {
        super(Text.translatable(Property.getTranslationKey("title", TranslationKeyType.NORMAL)));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        this.propertyList = new PropertyListWidget(client, this);
        addSelectableChild(propertyList);
        addDrawableChild(new ButtonWidget.Builder(Text.translatable(Property.getTranslationKey("reset", TranslationKeyType.NORMAL)), button -> {
            ScoreboardHelperConfig.INSTANCE.resetDefault();
            this.clearAndInit();
        }).size(200, 20).position(width / 2 + 10, height - 26).build());
        addDrawableChild(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).size(200, 20).position(width / 2 - 10 - 200, height - 26).build());
    }

    public ScoreboardHelperConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    @Override
    public void removed() {
        config.save();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.propertyList.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, TITLE_Y, 0xFFFFFF);
    }
}
