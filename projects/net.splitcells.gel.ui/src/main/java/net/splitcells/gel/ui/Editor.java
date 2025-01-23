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

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class Editor {
    public static Editor editor() {
        return new Editor();
    }

    private final Map<String, Attribute<? extends Object>> attributeVars = map();
    private final Map<String, Table> databaseVars = map();
    private Optional<Assignments> assignments = Optional.empty();

    private Editor() {

    }

    public Editor withAssignments(Optional<Assignments> arg) {
        assignments = arg;
        return this;
    }

    public Optional<Assignments> assignments() {
        return assignments;
    }

    public Editor withAttributeVar(String name, Attribute<? extends Object> value) {
        requireFreeVarName(name);
        attributeVars.put(name, value);
        return this;
    }

    public Attribute<? extends Object> attributeByVarName(String varName) {
        return attributeVars.get(varName);
    }

    public boolean hasAttributeVar(String varName) {
        return attributeVars.containsKey(varName);
    }

    public Table databaseByVarName(String varName) {
        return databaseVars.get(varName);
    }

    public Editor withDatabaseVar(String name, Table value) {
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
    public Map<String, Table> databaseVars() {
        return databaseVars.shallowCopy();
    }

    private void requireFreeVarName(String name) {
        if (attributeVars.containsKey(name)) {
            throw ExecutionException.execException(tree("Attribute variable with name already present.")
                    .withProperty("New attribute variable name", name)
                    .withProperty("Attribute variables", attributeVars.toString()));
        }
        if (databaseVars.containsKey(name)) {
            throw ExecutionException.execException(tree("Database variable with name already present.")
                    .withProperty("New attribute variable name", name)
                    .withProperty("Attribute variables", databaseVars.toString()));
        }
    }
}
