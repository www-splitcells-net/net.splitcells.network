/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.tree;

public class JsonConfig {
    public static JsonConfig jsonConfig() {
        return new JsonConfig();
    }

    private boolean isTopElement = true;
    /**
     * This is true, if the current {@link Tree} is a direct child of an {@link Tree},
     * that represents an array.
     */
    private boolean isArrayElement = false;

    private JsonConfig() {

    }

    public boolean isTopElement() {
        return isTopElement;
    }

    public JsonConfig withIsTopElement(boolean isTopElementArg) {
        isTopElement = isTopElementArg;
        return this;
    }

    public boolean isArrayElement() {
        return isArrayElement;
    }

    public JsonConfig withIsArrayElement(boolean arg) {
        isArrayElement = arg;
        return this;
    }
}
