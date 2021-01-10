package net.splitcells.gel.common;

public enum Language {
    ALLOCATE("allocate"),
    ALLOCATION("allocation"),
    ALLOCATIONS("allocations"),
    ARGUMENTATION("argumentation"),
    DATA("data"),
    DEMAND("demand"),
    @Deprecated
    DEMAND2("demand"),
    DEMANDS("demands"),
    EVENT("event"),
    GROUP("group"),
    HISTORY("history"),
    KEY("key"),
    LINE("line"),
    META_DATA("metaData"),
    PATH_ACCESS_SYMBOL("."),
    PROBLEM("problem"),
    RATING("rating"),
    REMOVE("remove"),
    RESULT("result"),
    STEP_TYPE("stepType"),
    SUPPLIES("supplies"),
    SUPPLY("supply"),
    TEST("test"),
    TIPS("tips"),
    VALUE("value"),
    WORDS("words");
    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
