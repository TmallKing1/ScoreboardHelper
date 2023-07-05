package top.pigest.scoreboardhelper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;

public class SBHelperCommand {
    private static final SimpleCommandExceptionType INVALID_COUNT_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.sbhelper.invalidCount"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("sbhelper")
                .then(ClientCommandManager.literal("maxDisplayCount")
                        .then(ClientCommandManager.argument("count", IntegerArgumentType.integer())
                                .executes(context -> executeMaxDisplayCount(context.getSource(), IntegerArgumentType.getInteger(context, "count")))))
        );

    }

    private static int executeMaxDisplayCount(FabricClientCommandSource source, int count) throws CommandSyntaxException {
        if(count < 0) {
            throw INVALID_COUNT_EXCEPTION.create();
        }
        ScoreboardHelperConfig.INSTANCE.maxShowCount.setValue(count);
        source.sendFeedback(Text.translatable("commands.sbhelper.success.setMaxCount", count));
        return count;
    }

}
