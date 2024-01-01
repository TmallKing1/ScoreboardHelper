package top.pigest.scoreboardhelper.util;

import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreModification {
    private final ModificationType type;
    private final ScoreboardObjective objective;
    private final String name;
    private final int score;

    public ScoreModification(ModificationType type, ScoreboardObjective objective, String name, int score) {
        this.type = type;
        this.name = name;
        this.objective = objective;
        this.score = score;
    }

    public String getModificationCommand() {
        String s = "scoreboard players " + this.type.operation +
                " " +
                this.name +
                " " +
                this.objective.getName();
        if (this.type != ModificationType.REMOVE) {
            s += " " + this.score;
        }
        return s;
    }
    public enum ModificationType {
        ADD("set"),
        CHANGE("set"),
        REMOVE("reset");

        final String operation;

        ModificationType(String operation) {
            this.operation = operation;
        }
    }
}
