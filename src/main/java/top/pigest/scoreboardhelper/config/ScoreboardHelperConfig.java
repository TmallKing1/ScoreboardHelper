package top.pigest.scoreboardhelper.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ScoreboardHelperConfig {

    private final File file;
    private final Map<String, Property<?>> propertyMap = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ScoreboardHelperConfig INSTANCE = new ScoreboardHelperConfig(FabricLoader.getInstance().getConfigDir().resolve("scoreboard-helper.json").toFile());

    public final Property.BooleanProperty scoreboardShown = addProperty(new Property.BooleanProperty("show_scoreboard", true));
    public final Property.BooleanProperty sidebarScoreShown = addProperty(new Property.BooleanProperty("show_sidebar_score", true));
    public final Property.IntegerProperty maxShowCount = addProperty(new Property.IntegerProperty("max_show_count", 15));
    public final Property.IntegerProperty sidebarYOffset = addProperty(new Property.IntegerProperty("sidebar_y_offset", 0));
    public final Property.DoubleProperty sidebarBackgroundOpacity = addProperty(new Property.DoubleProperty("sidebar_background_opacity", 0.3, 2));
    public final Property.DoubleProperty sidebarBackgroundTitleOpacity = addProperty(new Property.DoubleProperty("sidebar_background_title_opacity", 0.4, 2));
    public final Property.DoubleProperty sidebarTextOpacity = addProperty(new Property.DoubleProperty("sidebar_text_opacity", 1.0, 2));
    public final Property.DoubleProperty sidebarTitleTextOpacity = addProperty(new Property.DoubleProperty("sidebar_title_text_opacity", 1.0, 2));

    public final Property.BaseProperty<ScoreboardSidebarPosition> sidebarPosition = addProperty(new Property.BaseProperty<ScoreboardSidebarPosition>("sidebar_position", ScoreboardSidebarPosition.RIGHT) {
        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.getValue().toString());
        }

        @Override
        public void fromJson(JsonElement jsonElement) {
            if(jsonElement.isJsonPrimitive()) {
                ScoreboardSidebarPosition value = ScoreboardSidebarPosition.RIGHT;
                Optional<ScoreboardSidebarPosition> optional = Arrays.stream(ScoreboardSidebarPosition.values()).filter(val -> val.toString().equals(jsonElement.getAsString())).findFirst();
                if(optional.isPresent()) {
                    value = optional.get();
                }
                setValue(value);
            } else {
                throw new JsonParseException("Json must be a primitive.");
            }
        }
    });

    static {
        INSTANCE.load();
    }

    public ScoreboardHelperConfig(File file) {
        this.file = file;
        load();
    }

    public void load() {
        if(file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                fromJson(JsonParser.parseReader(fileReader));
            } catch (Exception e) {
                LOGGER.error("Couldn't load config from file '" + file.getAbsolutePath() + "'", e);
            }
        }
        save();
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            GSON.toJson(toJson(), fileWriter);
        } catch (Exception e) {
            LOGGER.error("Couldn't save config to file '" + file.getAbsolutePath() + "'", e);
        }
    }

    private void fromJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            for(Map.Entry<String, Property<?>> entry: propertyMap.entrySet()) {
                JsonElement element = obj.get(entry.getKey());
                if(element != null) {
                    try {
                        entry.getValue().fromJson(element);
                    } catch (JsonParseException e) {
                        LOGGER.error("Couldn't read property '" + entry.getKey() + "'", e);
                    }
                }
            }
        } else {
            throw new JsonParseException("Json must be an object");
        }
    }

    private JsonElement toJson() {
        JsonObject obj = new JsonObject();
        for(Map.Entry<String, Property<?>> entry: propertyMap.entrySet()) {
            obj.add(entry.getKey(), entry.getValue().toJson());
        }
        return obj;
    }

    private <T extends Property<?>> T addProperty(T property) {
        Property<?> re = propertyMap.put(property.getKey(), property);
        if(re != null) {
            LOGGER.warn("Property with key " + re.getKey() + " was overridden.");
        }
        return property;
    }

    public enum ScoreboardSidebarPosition {
        LEFT,
        LEFT_UPPER_CORNER,
        LEFT_LOWER_CORNER,
        RIGHT,
        RIGHT_UPPER_CORNER,
        RIGHT_LOWER_CORNER;

        @Override
        public String toString() {
            switch (this) {
                case LEFT -> {
                    return "left";
                }
                case LEFT_LOWER_CORNER -> {
                    return "left_lower_corner";
                }
                case LEFT_UPPER_CORNER -> {
                    return "left_upper_corner";
                }
                case RIGHT -> {
                    return "right";
                }
                case RIGHT_LOWER_CORNER -> {
                    return "right_lower_corner";
                }
                case RIGHT_UPPER_CORNER -> {
                    return "right_upper_corner";
                }
            }
            return super.toString();
        }
    }
}
