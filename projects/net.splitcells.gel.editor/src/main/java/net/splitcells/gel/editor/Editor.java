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
package net.splitcells.gel.editor;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.SolutionDescription;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * There is no distinction, between a things name and their variable name.
 * So something like `a = attribute(string, "Alpha")` is not supported,
 * as having multiple ids for one thing will cause problems.
 * Otherwise, error messages and debugging can get hard to understand.
 */
public class Editor implements Discoverable {
    public static Editor editor(String name, Discoverable parent) {
        return new Editor(name, parent);
    }

    private @Getter final String name;
    private final Discoverable parent;
    private @Getter final Map<String, Attribute<?>> attributes = map();
    private @Getter final Map<String, Table> tables = map();
    private @Getter final Map<String, Solution> solution = map();

    private Editor(String argName, Discoverable argParent) {
        name = argName;
        parent = argParent;
    }

    @Override
    public List<String> path() {
        return parent.path().withAppended(name);
    }

    /**
     *
     * @param solutionDescription
     * @return
     * @deprecated Use {@link net.splitcells.gel.editor.lang.geal.SourceUnit} instead.
     */
    @Deprecated
    public Result<SolutionEditor, Tree> solutionEditor(SolutionDescription solutionDescription) {
        return SolutionEditor.solutionEditor(this, solutionDescription).parse(solutionDescription);
    }
}
