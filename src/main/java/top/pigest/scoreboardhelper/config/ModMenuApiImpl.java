package top.pigest.scoreboardhelper.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ScoreboardHelperConfigScreen(parent, ScoreboardHelperConfig.INSTANCE);
    }

}
