package top.pigest.scoreboardhelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import org.lwjgl.glfw.GLFW;
import top.pigest.scoreboardhelper.command.SBHelperCommand;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfigScreen;
import top.pigest.scoreboardhelper.util.Constants;

import java.util.Collection;

public class ScoreboardHelper implements ClientModInitializer {

    private static final KeyBinding keyBindingPageUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboardHelper.pageUp",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.scoreboardHelper"
    ));

    private static final KeyBinding keyBindingPageDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboardHelper.pageDown",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.scoreboardHelper"
    ));

    private static final KeyBinding keyBindingSwitchDisplay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboardHelper.switchDisplay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboardHelper"
    ));
    private static final KeyBinding keyBindingOpenConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboardHelper.openConfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboardHelper"
    ));
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SBHelperCommand.register(dispatcher));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Constants.CD = Constants.CD > 0 ? Constants.CD - 1: 0;
            if (keyBindingPageDown.isPressed()) {
                Scoreboard scoreboard;
                if (client.world != null && client.player != null) {
                    scoreboard = client.world.getScoreboard();
                    ScoreboardObjective scoreboardObjective = null;
                    Team team = scoreboard.getPlayerTeam(client.player.getEntityName());
                    if (team != null) {
                        if (team.getColor().getColorIndex() >= 0) {
                            scoreboardObjective = scoreboard.getObjectiveForSlot(3 + team.getColor().getColorIndex());
                        }
                    }
                    scoreboardObjective = scoreboardObjective == null ? scoreboard.getObjectiveForSlot(1) : scoreboardObjective;
                    Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
                    if(collection.size() > Constants.PAGE + ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue()) {
                        Constants.PAGE++;
                    }
                }
            }
            if (keyBindingPageUp.isPressed()) {
                Constants.PAGE = Constants.PAGE > 0 ? Constants.PAGE - 1 : 0;
            }
            if (keyBindingSwitchDisplay.isPressed()) {
                if(Constants.CD == 0) {
                    ScoreboardHelperConfig.INSTANCE.scoreboardShown.setValue(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue());
                    Constants.CD = 5;
                }
            }
            if (keyBindingOpenConfig.isPressed()) {
                client.setScreen(new ScoreboardHelperConfigScreen(client.currentScreen, ScoreboardHelperConfig.INSTANCE));
            }
        });
    }
}
