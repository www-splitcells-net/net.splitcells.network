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
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.SolutionDescription;
import net.splitcells.gel.editor.lang.geal.FunctionCallChainDesc;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.SourceUnit;
import net.splitcells.gel.editor.lang.geal.VariableDefinitionDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.throwNotImplementedYet;
import static net.splitcells.gel.editor.executors.FunctionCallMetaExecutor.functionCallExecutor;

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

    @Getter private final String name;
    @Getter private final Discoverable parent;
    @Getter private final Map<String, Attribute<?>> attributes = map();
    @Getter private final Map<String, Table> tables = map();
    @Getter private final Map<String, Solution> solutions = map();
    @Getter private final Map<String, Constraint> constraints = map();
    @Getter private final Map<Table, TableFormatting> tableFormatting = map();

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

    @ReturnsThis
    public Editor parse(SourceUnit sourceUnit) {
        sourceUnit.getStatements().forEach(s -> {
            switch (s) {
                case VariableDefinitionDesc vd -> parse(vd);
                case FunctionCallChainDesc fcc -> parse(fcc);
            }
        });
        return this;
    }

    @ReturnsThis
    public Editor parse(VariableDefinitionDesc variableDefinition) {
        final var functionCallExecutor = functionCallExecutor();
        functionCallExecutor.setContext(Optional.of(this));
        if (variableDefinition.getFunctionCallChain().getExpression() instanceof FunctionCallDesc functionCall) {
            final var name = variableDefinition.getName().getValue();
            if (attributes.hasKey(name)) {
                throw execException(tree("The attribute variable \" + name + \" with the same name is already defined.").withProperty("name", name)
                        .withProperty("attribute variables", attributes.toString()));
            }
            if (tables.hasKey(name)) {
                throw execException(tree("The table variable " + name + " with the same name is already defined.")
                        .withProperty("name", name)
                        .withProperty("table variables", tables.toString()));
            }
            if (solutions.hasKey(name)) {
                throw execException(tree("The solution variable " + name + " with the same name is already defined.")
                        .withProperty("name", name)
                        .withProperty("solutions variables", solutions.toString()));
            }
            if (constraints.hasKey(name)) {
                throw execException(tree("The constraint variable " + name + " with the same name is already defined.")
                        .withProperty("name", name)
                        .withProperty("table variables", constraints.toString()));
            }
            if (functionCallExecutor.supports(functionCall)) {
                final var newObject = functionCallExecutor.execute(functionCall).getSubject().orElseThrow();
                if (newObject instanceof Attribute<?> attribute) {
                    attributes.put(name, attribute);
                } else if (newObject instanceof Solution solution) {
                    solutions.put(name, solution);
                } else if (newObject instanceof Table table) {
                    tables.put(name, table);
                } else if (newObject instanceof Constraint constraint) {
                    constraints.put(name, constraint);
                } else {
                    throwNotImplementedYet();
                }
            } else {
                throwNotImplementedYet();
            }
        }
        return this;
    }

    @ReturnsThis
    public Editor parse(FunctionCallChainDesc functionCallChain) {
        return this;
    }
}
