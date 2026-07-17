/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Type {

    public static Type type(String argName) {
        return new Type(argName);
    }

    @Getter private final String name;

    private Type(String argName) {
        name = argName;
    }
}
