package top.pigest.scoreboardhelper.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public class BooleanProperty extends BaseProperty<Boolean> {

    public BooleanProperty(String key, Boolean defValue) {
        super(key, defValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setValue(jsonElement.getAsBoolean());
        } else {
            throw new JsonParseException("Json must be a primitive.");
        }
    }

    @Override
    public ClickableWidget createWidget(int x, int y, int width) {
        Text text = Text.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.NORMAL));
        Text tooltip = Text.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.TOOLTIP));
        Boolean[] values = new Boolean[] {true, false};
        CyclingButtonWidget<Boolean> positionCyclingButtonWidget = CyclingButtonWidget.<Boolean>builder(value -> value ? ScreenTexts.ON : ScreenTexts.OFF).values(values).initially(this.getValue())
                .build(x, y, width, 20, text, (button, value) -> this.setValue(value));
        positionCyclingButtonWidget.setTooltip(Tooltip.of(tooltip));
        return positionCyclingButtonWidget;
    }
}
