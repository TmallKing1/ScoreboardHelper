package top.pigest.scoreboardhelper.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;

public class ScoreboardHelperUtils {
    public static ScoreboardObjective getSidebarObjective(Scoreboard scoreboard, ClientPlayerEntity player) {
        Team team = scoreboard.getPlayerTeam(player.getEntityName());
        ScoreboardObjective objective = null;
        if (team != null && team.getColor().getColorIndex() >= 0) {
            objective = scoreboard.getObjectiveForSlot(3 + team.getColor().getColorIndex());
        }
        objective = objective != null ? objective : scoreboard.getObjectiveForSlot(1);
        return objective;
    }

}
