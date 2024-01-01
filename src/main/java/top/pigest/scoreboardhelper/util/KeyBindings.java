package top.pigest.scoreboardhelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBinding KEY_BINDING_PAGE_UP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.pageUp",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.scoreboard-helper"
    ));
    public static final KeyBinding KEY_BINDING_PAGE_DOWN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.pageDown",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyBinding KEY_BINDING_SWITCH_DISPLAY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.switchDisplay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyBinding KEY_BINDING_OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.openConfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyBinding KEY_BINDING_EXPORT_SCOREBOARD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.exportScoreboard",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_BACKSLASH,
            "category.scoreboard-helper"
    ));
    public static final KeyBinding KEY_BINDING_EDIT_SCORE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.scoreboard-helper.editScore",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "category.scoreboard-helper"
    ));

    public static void init() {

    }
}
