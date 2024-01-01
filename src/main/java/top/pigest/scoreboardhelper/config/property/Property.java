package top.pigest.scoreboardhelper.config.property;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public interface Property<T> {
    static String getTranslationKey(String key, TranslationKeyType keyType) {
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

    String getKey();
    T getValue();
    T getDefaultValue();
    void setValue(T value);
    JsonElement toJson();
    void fromJson(JsonElement jsonElement);
    ClickableWidget createWidget(int x, int y, int width);
}