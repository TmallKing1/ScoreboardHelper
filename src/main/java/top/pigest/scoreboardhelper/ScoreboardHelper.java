package top.pigest.scoreboardhelper;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import top.pigest.scoreboardhelper.command.SBHelperCommand;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.gui.screen.ScoreEditingScreen;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardHelperConfigScreen;
import top.pigest.scoreboardhelper.util.Constants;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardExportScreen;
import top.pigest.scoreboardhelper.util.KeyBindings;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;

import java.util.Collection;

public class ScoreboardHelper implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        KeyBindings.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SBHelperCommand.register(dispatcher));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Constants.CD_SWITCH_DISPLAY = Constants.CD_SWITCH_DISPLAY > 0 ? Constants.CD_SWITCH_DISPLAY - 1: 0;
            Constants.CD_EXPORT = Constants.CD_EXPORT > 0 ? Constants.CD_EXPORT - 1: 0;
            Constants.CD_EDIT = Constants.CD_EDIT > 0 ? Constants.CD_EDIT - 1: 0;
            if (KeyBindings.KEY_BINDING_PAGE_DOWN.isPressed()) {
                Scoreboard scoreboard;
                if (client.world != null && client.player != null) {
                    scoreboard = client.world.getScoreboard();
                    ScoreboardObjective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
                    Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
                    if(collection.size() > Constants.PAGE + ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue()) {
                        Constants.PAGE++;
                    }
                }
            }
            if (KeyBindings.KEY_BINDING_PAGE_UP.isPressed()) {
                Constants.PAGE = Constants.PAGE > 0 ? Constants.PAGE - 1 : 0;
            }
            if (KeyBindings.KEY_BINDING_SWITCH_DISPLAY.isPressed() && Constants.CD_SWITCH_DISPLAY == 0) {
                ScoreboardHelperConfig.INSTANCE.scoreboardShown.setValue(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue());
                Constants.CD_SWITCH_DISPLAY = 5;
            }
            if (KeyBindings.KEY_BINDING_OPEN_CONFIG.isPressed()) {
                client.setScreen(new ScoreboardHelperConfigScreen(client.currentScreen, ScoreboardHelperConfig.INSTANCE));
            }
            if(KeyBindings.KEY_BINDING_EXPORT_SCOREBOARD.isPressed() && Constants.CD_EXPORT == 0) {
                ScoreboardExportScreen.INSTANCE.setParent(client.currentScreen);
                client.setScreen(ScoreboardExportScreen.INSTANCE);
                Constants.CD_EXPORT = 5;
            }
            if (KeyBindings.KEY_BINDING_EDIT_SCORE.isPressed() && Constants.CD_EDIT == 0) {
                if (client.world != null && client.player != null) {
                    Scoreboard scoreboard = client.world.getScoreboard();
                    ScoreboardObjective objective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
                    if(objective == null) {
                        client.player.sendMessage(Text.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    } else {
                        client.setScreen(new ScoreEditingScreen(client.currentScreen, scoreboard, objective));
                    }
                }
                Constants.CD_EDIT = 5;
            }
        });
    }
}
