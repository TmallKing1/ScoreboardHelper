package top.pigest.scoreboardhelper.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.gui.widget.PropertySliderWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public class DoubleProperty extends BaseProperty<Double> {
    private final int digits;
    private final double min;
    private final double max;

    public DoubleProperty(String key, Double defValue, int digits, double min, double max) {
        super(key, defValue);
        this.digits = digits;
        this.min = min;
        this.max = max;
    }

    @Override
    public void setValue(Double value) {
        double scale = Math.pow(10, digits);
        super.setValue(Math.round(value * scale) / scale);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setValue(jsonElement.getAsDouble());
        } else {
            throw new JsonParseException("Json must be a primitive.");
        }
    }

    @Override
    public ClickableWidget createWidget(int x, int y, int width) {
        Text text = Text.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.NORMAL));
        Text tooltip = Text.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.TOOLTIP));
        double propertyValue = this.getValue();
        double value = (propertyValue - min) / (max - min);
        PropertySliderWidget<Double> propertySliderWidget = new PropertySliderWidget<>(x, y, width, 20, text, value, this, PropertySliderWidget.ValueTextGetter.getDefaultPercentTextGetter(), PropertySliderWidget.PropertyValueApplier.getDefaultDoublePropertyValueApplier(min, max));
        propertySliderWidget.setTooltip(Tooltip.of(tooltip));
        return propertySliderWidget;
    }
}
