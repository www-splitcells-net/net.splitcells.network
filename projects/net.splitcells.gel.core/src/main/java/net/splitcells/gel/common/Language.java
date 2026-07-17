/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.common;

public enum Language {
    ALLOCATE("allocate"),
    ALLOCATION("allocation"),
    ALLOCATIONS("allocations"),
    ARGUMENTS("arguments"),
    CONTENT("content"),
    DATA("data"),
    DATABASE_HISTORY("database-history"),
    DEMAND("demand"),
    @Deprecated
    DEMAND2("demand"),
    DEMANDS("demands"),
    EMPTY_STRING(""),
    EVENT("event"),
    EVENTS("events"),
    FOR_ALL("for-all"),
    GROUP("group"),
    HISTORY("history"),
    HISTORIC_VALUES("historic-values"),
    INDEX("index"),
    KEY("key"),
    LINE("line"),
    META_DATA("metaData"),
    NAME("name"),
    OPTIMIZATION("optimization"),
    PATH_ACCESS_SYMBOL("."),
    PROBLEM("problem"),
    PROPAGTION("propagation"),
    RATING("rating"),
    REMOVE("remove"),
    RESULT("result"),
    STEP_TYPE("stepType"),
    SUPPLIES("supplies"),
    SUPPLY("supply"),
    TEST("test"),
    TYPE("type"),
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
