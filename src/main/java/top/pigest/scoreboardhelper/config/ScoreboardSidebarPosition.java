package top.pigest.scoreboardhelper.config;

public enum ScoreboardSidebarPosition {
    LEFT("left"),
    LEFT_UPPER_CORNER("left_upper_corner"),
    LEFT_LOWER_CORNER("left_lower_corner"),
    RIGHT("right"),
    RIGHT_UPPER_CORNER("right_upper_corner"),
    RIGHT_LOWER_CORNER("right_lower_corner");
    private final String name;

    ScoreboardSidebarPosition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
