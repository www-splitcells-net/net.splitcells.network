/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Editor {
    public static Editor editor() {
        return new Editor();
    }

    private final Map<String, Attribute<? extends Object>> attributeVars = map();
    private final Map<String, Database> databaseVars = map();

    private Editor() {

    }

    public Editor withAttributeVar(String name, Attribute<? extends Object> value) {
        requireFreeVarName(name);
        attributeVars.put(name, value);
        return this;
    }

    public Attribute<? extends Object> attribute(String varName) {
        return attributeVars.get(varName);
    }

    public boolean hasAttributeVar(String varName) {
        return attributeVars.containsKey(varName);
    }

    public Database database(String varName) {
        return databaseVars.get(varName);
    }

    public Editor withDatabaseVar(String name, Database value) {
        requireFreeVarName(name);
        databaseVars.put(name, value);
        return this;
    }

    /**
     * @return Write access is denied via shallow copies.
     */
    public Map<String, Attribute<? extends Object>> attributeVars() {
        return attributeVars.shallowCopy();
    }

    /**
     * @return Write access is denied via shallow copies.
     */
    public Map<String, Database> databaseVars() {
        return databaseVars.shallowCopy();
    }

    private void requireFreeVarName(String name) {
        if (attributeVars.containsKey(name)) {
            throw executionException(perspective("Attribute variable with name already present.")
                    .withProperty("New attribute variable name", name)
                    .withProperty("Attribute variables", attributeVars.toString()));
        }
        if (databaseVars.containsKey(name)) {
            throw executionException(perspective("Database variable with name already present.")
                    .withProperty("New attribute variable name", name)
                    .withProperty("Attribute variables", databaseVars.toString()));
        }
    }
}
