package top.pigest.scoreboardhelper.config;

public enum ScoreSortingMethod {
    BY_SCORE_DESC("by_score_descending"),
    BY_SCORE_ASC("by_score_ascending"),
    BY_NAME_DESC("by_name_descending"),
    BY_NAME_ASC("by_name_ascending");

    final String name;

    ScoreSortingMethod(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
