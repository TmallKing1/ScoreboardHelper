package top.pigest.scoreboardhelper.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;

public class ScoreboardHelperUtils {
    public static ScoreboardObjective getSidebarObjective(Scoreboard scoreboard, ClientPlayerEntity player) {
        Team team = scoreboard.getPlayerTeam(player.getEntityName());
        ScoreboardObjective objective = null;

        if (team != null && ScoreboardDisplaySlot.fromFormatting(team.getColor()) != null) {
            objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.fromFormatting(team.getColor()));
        }
        objective = objective != null ? objective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        return objective;
    }

}
