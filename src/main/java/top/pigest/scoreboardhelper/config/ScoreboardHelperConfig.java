package top.pigest.scoreboardhelper.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import top.pigest.scoreboardhelper.config.property.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ScoreboardHelperConfig {

    private final File file;
    private final List<Property<?>> properties = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ScoreboardHelperConfig INSTANCE = new ScoreboardHelperConfig(FabricLoader.getInstance().getConfigDir().resolve("scoreboard-helper.json").toFile());

    public final BooleanProperty scoreboardShown;
    public final BooleanProperty sidebarScoreShown;
    public final EnumProperty<ScoreSortingMethod> sortingMethod;
    public final IntegerProperty maxShowCount;
    public final EnumProperty<ScoreboardSidebarPosition> sidebarPosition;
    public final IntegerProperty sidebarYOffset;
    public final DoubleProperty sidebarBackgroundOpacity;
    public final DoubleProperty sidebarBackgroundTitleOpacity;
    public final DoubleProperty sidebarTextOpacity;
    public final DoubleProperty sidebarTitleTextOpacity;
    public final BooleanProperty defaultTeamChat;

    public ScoreboardHelperConfig(File file) {
        this.file = file;
        scoreboardShown = addProperty(new BooleanProperty("show_scoreboard", true));
        sidebarScoreShown = addProperty(new BooleanProperty("show_sidebar_score", true));
        sortingMethod = addProperty(new EnumProperty<>("score_sorting_method", ScoreSortingMethod.BY_SCORE_DESC));
        maxShowCount = addProperty(new IntegerProperty("max_show_count", 15, 0, 100));
        sidebarPosition = addProperty(new EnumProperty<>("sidebar_position", ScoreboardSidebarPosition.RIGHT));
        sidebarYOffset = addProperty(new IntegerProperty("sidebar_y_offset", 0, -100, 100));
        sidebarBackgroundOpacity = addProperty(new DoubleProperty("sidebar_background_opacity", 0.3, 2, 0.0, 1.0));
        sidebarBackgroundTitleOpacity = addProperty(new DoubleProperty("sidebar_background_title_opacity", 0.4, 2, 0.0, 1.0));
        sidebarTextOpacity = addProperty(new DoubleProperty("sidebar_text_opacity", 1.0, 2, 0.1, 1.0));
        sidebarTitleTextOpacity = addProperty(new DoubleProperty("sidebar_title_text_opacity", 1.0, 2, 0.1, 1.0));
        defaultTeamChat = addProperty(new BooleanProperty("default_team_chat", false));
        load();
    }

    public void load() {
        LOGGER.info("Loading Scoreboard Helper config from file '" + file.getAbsolutePath() + "'");
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
            for(Property<?> property: properties) {
                JsonElement element = obj.get(property.getKey());
                if(element != null) {
                    try {
                        property.fromJson(element);
                    } catch (JsonParseException e) {
                        LOGGER.error("Couldn't read property '" + property.getKey() + "'", e);
                    }
                }
            }
        } else {
            throw new JsonParseException("Json must be an object");
        }
    }

    private JsonElement toJson() {
        JsonObject obj = new JsonObject();
        for(Property<?> property: properties) {
            obj.add(property.getKey(), property.toJson());
        }
        return obj;
    }

    public void resetDefault() {
        properties.forEach(this::resetDefault);
    }

    private <T> void resetDefault(Property<T> property) {
        property.setValue(property.getDefaultValue());
    }

    private <T extends Property<?>> T addProperty(T property) {
        properties.add(property);
        return property;
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

}
