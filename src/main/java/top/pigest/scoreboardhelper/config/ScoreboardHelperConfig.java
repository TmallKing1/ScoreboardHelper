package top.pigest.scoreboardhelper.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardHelperConfig {

    private final File file;
    private final Map<String, Property<?>> propertyMap = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ScoreboardHelperConfig INSTANCE = new ScoreboardHelperConfig(FabricLoader.getInstance().getConfigDir().resolve("scoreboard-helper.json").toFile());

    public final Property.BooleanProperty scoreboardShown = addProperty(new Property.BooleanProperty("show_scoreboard", true));
    public final Property.BooleanProperty sidebarScoreShown = addProperty(new Property.BooleanProperty("show_sidebar_score", true));
    public final Property.IntegerProperty maxShowCount = addProperty(new Property.IntegerProperty("max_show_count", 15));

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
}
