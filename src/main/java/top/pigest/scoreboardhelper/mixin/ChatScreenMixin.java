package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "sendMessage",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V"))
    private void injected1(ClientPlayNetworkHandler instance, String content) {
        if(ScoreboardHelperConfig.INSTANCE.defaultTeamChat.getValue()) {
            if(content.startsWith("#")) {
                if(content.length() == 1) {
                    instance.sendChatMessage(content);
                } else {
                    instance.sendChatMessage(content.substring(1));
                }
            } else {
                instance.sendChatCommand("teammsg "+ content);
            }
        } else {
            instance.sendChatMessage(content);
        }
    }


}
