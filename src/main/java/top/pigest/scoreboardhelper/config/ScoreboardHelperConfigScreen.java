package top.pigest.scoreboardhelper.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.gui.widget.PropertySliderWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

import java.util.Objects;

public class ScoreboardHelperConfigScreen extends Screen {
    private final Screen parent;
    private final ScoreboardHelperConfig config;
    private final int TITLE_Y = 30;

    public ScoreboardHelperConfigScreen(Screen parent, ScoreboardHelperConfig config) {
        super(Text.translatable(getTranslationKey("title", TranslationKeyType.NORMAL)));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        addDrawableChild(createBooleanPropertyButton(width / 2 - 10 - 200, TITLE_Y + 20, 200, 20, config.scoreboardShown));
        addDrawableChild(createBooleanPropertyButton(width / 2 - 10 - 200, TITLE_Y + 20 + 30, 200, 20, config.sidebarScoreShown));
        addDrawableChild(createEnumPropertyButton(width / 2 - 10 - 200, TITLE_Y + 20 + 30 * 2, 200, 20, config.sidebarPosition, ScoreboardHelperConfig.ScoreboardSidebarPosition.values()));
        addDrawableChild(createIntegerPropertySlider( width / 2 - 10 - 200, TITLE_Y + 20 + 30 * 3, 200, 20, config.maxShowCount, 0, 100));
        addDrawableChild(createIntegerPropertySlider( width / 2 - 10 - 200, TITLE_Y + 20 + 30 * 4, 200, 20, config.sidebarYOffset, -100, 100));
        addDrawableChild(createDoublePropertySlider(width / 2 + 10, TITLE_Y + 20, 200, 20, config.sidebarBackgroundOpacity, 0.0, 1.0));
        addDrawableChild(createDoublePropertySlider(width / 2 + 10, TITLE_Y + 20 + 30, 200, 20, config.sidebarBackgroundTitleOpacity, 0.0, 1.0));
        addDrawableChild(createDoublePropertySlider(width / 2 + 10, TITLE_Y + 20 + 30 * 2, 200, 20, config.sidebarTextOpacity, 0.1, 1.0));
        addDrawableChild(createDoublePropertySlider(width / 2 + 10, TITLE_Y + 20 + 30 * 3, 200, 20, config.sidebarTitleTextOpacity, 0.1, 1.0));
        addDrawableChild(createBooleanPropertyButton(width / 2 + 10, TITLE_Y + 20 + 30 * 4, 200, 20, config.defaultTeamChat));

        addDrawableChild(new ButtonWidget.Builder(Text.translatable(getTranslationKey("reset", TranslationKeyType.NORMAL)), button -> {
            ScoreboardHelperConfig.INSTANCE.resetDefault();
            this.clearAndInit();
        }).size(200, 20).position(width / 2 + 10, height - 40).build());
        addDrawableChild(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).size(200, 20).position(width / 2 - 10 - 200, height - 40).build());
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, TITLE_Y, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
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

    @SafeVarargs
    private <T extends Enum<?>> CyclingButtonWidget<T> createEnumPropertyButton(int x, int y, int width, int height, Property<T> property, T ... values) {
        Text text = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.NORMAL));
        Text tooltip = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.TOOLTIP));
        CyclingButtonWidget<T> positionCyclingButtonWidget = CyclingButtonWidget.<T>builder(value -> Text.translatable(getTranslationKey(property.getKey(),
                        TranslationKeyType.NORMAL) + ".value." + value.toString())).values(values).initially(property.getValue())
                .build(x, y, width, height, text, (button, value) -> property.setValue(value));
        positionCyclingButtonWidget.setTooltip(Tooltip.of(tooltip));
        return positionCyclingButtonWidget;
    }

    private PropertySliderWidget<Integer> createIntegerPropertySlider(int x, int y, int width, int height, Property<Integer> property, int min, int max) {
        Text text = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.NORMAL));
        Text tooltip = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.TOOLTIP));
        int propertyValue = property.getValue();
        double value = 1.0 * (propertyValue - min) / (max - min);
        PropertySliderWidget<Integer> propertySliderWidget = new PropertySliderWidget<>(x, y, width, height, text, value, property, PropertySliderWidget.ValueTextGetter.getDefaultTextGetter(), PropertySliderWidget.PropertyValueApplier.getDefaultIntegerPropertyValueApplier(min, max));
        propertySliderWidget.setTooltip(Tooltip.of(tooltip));
        return propertySliderWidget;
    }

    private PropertySliderWidget<Double> createDoublePropertySlider(int x, int y, int width, int height, Property<Double> property, double min, double max) {
        Text text = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.NORMAL));
        Text tooltip = Text.translatable(getTranslationKey(property.getKey(), TranslationKeyType.TOOLTIP));
        double propertyValue = property.getValue();
        double value = (propertyValue - min) / (max - min);
        PropertySliderWidget<Double> propertySliderWidget = new PropertySliderWidget<>(x, y, width, height, text, value, property, PropertySliderWidget.ValueTextGetter.getDefaultPercentTextGetter(), PropertySliderWidget.PropertyValueApplier.getDefaultDoublePropertyValueApplier(min, max));
        propertySliderWidget.setTooltip(Tooltip.of(tooltip));
        return propertySliderWidget;
    }

}
