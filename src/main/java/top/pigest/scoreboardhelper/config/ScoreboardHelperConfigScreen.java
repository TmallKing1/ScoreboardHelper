package top.pigest.scoreboardhelper.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoreboardHelperConfigScreen extends Screen {
    private final Screen parent;
    private final ScoreboardHelperConfig config;
    private final List<TextWithShadow> list = new ArrayList<>();

    public ScoreboardHelperConfigScreen(Screen parent, ScoreboardHelperConfig config) {
        super(Text.translatable(getTranslationKey("title", TranslationKeyType.NORMAL)));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        addDrawableChild(createBooleanPropertyButton(width / 2 - 100, height / 2 - 10 - 30, 200, 20, config.scoreboardShown));
        addDrawableChild(createBooleanPropertyButton(width / 2 - 100, height / 2 - 10, 200, 20, config.sidebarScoreShown));
        addDrawableChild(createIntegerPropertyTextField( width / 2 - 100, height / 2 - 10 + 40, 200, 20, config.maxShowCount));

        addDrawableChild(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).size(200, 20).position(width / 2 - 100, height - 40).build());
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        for(TextWithShadow text: list) {
            context.drawTextWithShadow(textRenderer, text.text, text.x, text.y, text.color);
        }
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 30, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private static String getTranslationKey(String key, TranslationKeyType keyType) {
        switch (keyType) {
            case NORMAL -> {
                return "options.scoreboard-helper." + key;
            }
            case TOOLTIP -> {
                return "options.scoreboard-helper." + key + ".tooltip";
            }
        }
        return "options.scoreboard-helper." + key;
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

    private TextFieldWidget createIntegerPropertyTextField(int x, int y, int width, int height, Property<Integer> property) {
        Text text = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.NORMAL));
        list.add(new TextWithShadow(x, y - 15, text, 0xFFFFFF));
        Text toolTip = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.TOOLTIP));
        TextFieldWidget widget = new TextFieldWidget(textRenderer, x, y, width, height, Text.literal(property.getKey()));
        widget.setTooltip(Tooltip.of(toolTip));
        widget.setText(String.valueOf(property.getValue()));
        widget.setChangedListener(s -> {
            int k;
            try {
                k = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                k = property.getDefaultValue();
            }
            property.setValue(k);
        });
        return widget;
    }

    private record TextWithShadow(int x, int y, Text text, int color) {
    }

    private enum TranslationKeyType {
        NORMAL,
        TOOLTIP
    }
}
