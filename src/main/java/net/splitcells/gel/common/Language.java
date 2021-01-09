package net.splitcells.gel.common;

public enum Language {
    ARGUMENTATION("argumentation")
    , ALLOCATE("allocate")
    , PATH_ACCESS_SYMBOL(".")
    , DEMAND("demand")
    , SUPPLY("supply")
    , REMOVE("remove")
    , TEST("test")
    , LINE("line")
    , SUPPLIES("supplies")
    , DEMANDS("demands")
    , HISTORY("history")
    , ALLOCATION("allocation")
    , ALLOCATIONS("allocations")
    , META_DATA("metaData")
    , DATA("data")
    , VALUE("value")
    , KEY("key")
    , TIPS("tips")
    , DEMAND2("demand")
    , STEP_TYPE("stepType")
    , RESULT("result")
    , EVENT("event")
    , RATING("rating")
    , PROBLEM("problem")
    , WORDS("words")
    ;
    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
