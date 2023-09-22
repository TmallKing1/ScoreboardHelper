package top.pigest.scoreboardhelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.scoreboard.*;
import org.lwjgl.glfw.GLFW;
import top.pigest.scoreboardhelper.command.SBHelperCommand;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfigScreen;
import top.pigest.scoreboardhelper.util.Constants;
import top.pigest.scoreboardhelper.util.export.ScoreboardExportScreen;

import java.util.Collection;

public class ScoreboardHelper implements ClientModInitializer {

    private static final KeyBinding keyBindingPageUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.pageUp",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.scoreboard-helper"
    ));

    private static final KeyBinding keyBindingPageDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.pageDown",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.scoreboard-helper"
    ));

    private static final KeyBinding keyBindingSwitchDisplay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.switchDisplay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    private static final KeyBinding keyBindingOpenConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.openConfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    private static final KeyBinding keyBindingExportScoreboard = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.exportScoreboard",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_BACKSLASH,
            "category.scoreboard-helper"
    ));
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SBHelperCommand.register(dispatcher));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Constants.CD_SWITCH_DISPLAY = Constants.CD_SWITCH_DISPLAY > 0 ? Constants.CD_SWITCH_DISPLAY - 1: 0;
            Constants.CD_EXPORT = Constants.CD_EXPORT > 0 ? Constants.CD_EXPORT - 1: 0;
            if (keyBindingPageDown.isPressed()) {
                Scoreboard scoreboard;
                if (client.world != null && client.player != null) {
                    scoreboard = client.world.getScoreboard();
                    ScoreboardObjective scoreboardObjective = null;
                    Team team = scoreboard.getPlayerTeam(client.player.getEntityName());
                    if (team != null) {
                        if (team.getColor().getColorIndex() >= 0) {
                            scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.fromFormatting(team.getColor()));
                        }
                    }
                    scoreboardObjective = scoreboardObjective == null ? scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) : scoreboardObjective;
                    Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
                    if(collection.size() > Constants.PAGE + ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue()) {
                        Constants.PAGE++;
                    }
                }
            }
            if (keyBindingPageUp.isPressed()) {
                Constants.PAGE = Constants.PAGE > 0 ? Constants.PAGE - 1 : 0;
            }
            if (keyBindingSwitchDisplay.isPressed() && Constants.CD_SWITCH_DISPLAY == 0) {
                ScoreboardHelperConfig.INSTANCE.scoreboardShown.setValue(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue());
                Constants.CD_SWITCH_DISPLAY = 5;
            }
            if (keyBindingOpenConfig.isPressed()) {
                client.setScreen(new ScoreboardHelperConfigScreen(client.currentScreen, ScoreboardHelperConfig.INSTANCE));
            }
            if(keyBindingExportScoreboard.isPressed() && Constants.CD_EXPORT == 0) {
                ScoreboardExportScreen.INSTANCE.setParent(client.currentScreen);
                client.setScreen(ScoreboardExportScreen.INSTANCE);
                Constants.CD_EXPORT = 5;
            }
        });
    }
}
