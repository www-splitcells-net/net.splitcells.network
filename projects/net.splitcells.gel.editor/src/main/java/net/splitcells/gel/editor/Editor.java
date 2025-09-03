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
import lombok.experimental.Accessors;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.geal.lang.*;
import net.splitcells.gel.editor.geal.runners.FunctionCallMetaExecutor;
import net.splitcells.gel.editor.geal.runners.FunctionCallRun;
import net.splitcells.gel.editor.lang.SolutionDescription;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;
import net.splitcells.website.Format;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.gel.editor.geal.parser.SourceUnitParser.parseGealSourceUnit;
import static net.splitcells.gel.editor.geal.runners.FunctionCallMetaExecutor.functionCallMetaExecutor;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

/**
 * There is no distinction, between a things name and their variable name.
 * So something like `a = attribute(string, "Alpha")` is not supported,
 * as having multiple ids for one thing will cause problems.
 * Otherwise, error messages and debugging can get hard to understand.
 */
@Accessors(chain = true)
public class Editor implements Discoverable {
    public static Editor editor(String name, Discoverable parent) {
        return new Editor(name, parent);
    }

    @Getter private final String name;
    @Getter private final Discoverable parent;
    @Getter private final Map<String, Attribute<?>> attributes = map();
    @Getter private final Map<String, Rater> raters = map();
    @Getter private final Map<String, Table> tables = map();
    @Getter private final Map<String, Solution> solutions = map();
    @Getter private final Map<String, Constraint> constraints = map();
    @Getter private final Map<String, TableFormatting> tableFormatting = map();
    private final Map<String, EditorData> data = map();

    public String lookupTableLikeName(Table table) {
        final var tableMatches = tables.entrySet().stream().filter(entry -> entry.getValue().equals(table))
                .map(entry -> entry.getKey())
                .toList();
        if (!tableMatches.isEmpty()) {
            return tableMatches.getFirst();
        }
        final var solutionMatches = solutions.entrySet().stream().filter(entry -> entry.getValue().equals(table))
                .map(entry -> entry.getKey())
                .toList();
        return solutionMatches.getFirst();
    }

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
     * @deprecated Use {@link SourceUnit} instead.
     */
    @Deprecated
    public Result<SolutionEditor, Tree> solutionEditor(SolutionDescription solutionDescription) {
        return SolutionEditor.solutionEditor(this, solutionDescription).parse(solutionDescription);
    }

    public Object resolveRaw(NameDesc name) {
        if (attributes.hasKey(name.getValue())) {
            return attributes.get(name.getValue());
        } else if (tables.hasKey(name.getValue())) {
            return tables.get(name.getValue());
        } else if (solutions.hasKey(name.getValue())) {
            return solutions.get(name.getValue());
        } else if (raters.hasKey(name.getValue())) {
            return raters.get(name.getValue());
        } else {
            throw notImplementedYet();
        }
    }

    public EditorData loadData(Format format, String name) {
        return data.getOptionally(name).orElseGet(() -> {
            final var newValue = editorData(format, new byte[0]);
            data.put(name, newValue);
            return newValue;
        });
    }

    public EditorData loadData(String dataKey) {
        return data.get(dataKey);
    }

    public Set<String> dataKeys() {
        return data.keySet2();
    }

    @ReturnsThis
    public Editor saveData(String name, EditorData content) {
        data.put(name, content);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(NameDesc name) {
        if (attributes.hasKey(name.getValue())) {
            return (T) attributes.get(name.getValue());
        } else if (tables.hasKey(name.getValue())) {
            return (T) tables.get(name.getValue());
        } else if (solutions.hasKey(name.getValue())) {
            return (T) solutions.get(name.getValue());
        } else if (raters.hasKey(name.getValue())) {
            return (T) raters.get(name.getValue());
        } else {
            throw notImplementedYet();
        }
    }

    @ReturnsThis
    public Editor interpret(String sourceUnit) {
        interpret(parseGealSourceUnit(sourceUnit));
        return this;
    }

    @ReturnsThis
    public Editor interpret(SourceUnit sourceUnit) {
        sourceUnit.getStatements().forEach(s -> {
            switch (s) {
                case VariableDefinitionDesc vd -> interpret(vd);
                case FunctionCallChainDesc fcc -> interpret(fcc);
            }
        });
        return this;
    }

    @ReturnsThis
    public Editor interpret(VariableDefinitionDesc variableDefinition) {
        final var varName = variableDefinition.getName().getValue();
        if (attributes.hasKey(varName)) {
            throw execException(tree("The attribute variable \" + name + \" with the same name is already defined.").withProperty("name", varName)
                    .withProperty("attribute variables", attributes.toString()));
        }
        if (tables.hasKey(varName)) {
            throw execException(tree("The table variable " + varName + " with the same name is already defined.")
                    .withProperty("name", varName)
                    .withProperty("table variables", tables.toString()));
        }
        if (solutions.hasKey(varName)) {
            throw execException(tree("The solution variable " + varName + " with the same name is already defined.")
                    .withProperty("name", varName)
                    .withProperty("solutions variables", solutions.toString()));
        }
        if (constraints.hasKey(varName)) {
            throw execException(tree("The constraint variable " + varName + " with the same name is already defined.")
                    .withProperty("name", varName)
                    .withProperty("table variables", constraints.toString()));
        }
        if (raters.hasKey(varName)) {
            throw execException(tree("The rater variable " + varName + " with the same name is already defined.")
                    .withProperty("name", varName)
                    .withProperty("table variables", raters.toString()));
        }
        final var parsedObject = parse(variableDefinition.getFunctionCallChain());
        if (parsedObject instanceof Attribute<?> attribute) {
            attributes.put(varName, attribute);
        } else if (parsedObject instanceof Solution solution) {
            solutions.put(varName, solution);
        } else if (parsedObject instanceof Table table) {
            tables.put(varName, table);
        } else if (parsedObject instanceof Constraint constraint) {
            constraints.put(varName, constraint);
        } else if (parsedObject instanceof Rater rater) {
            raters.put(varName, rater);
        } else {
            throw notImplementedYet(parsedObject.getClass().getName() + " is not supported for variables yet.");
        }
        return this;
    }

    @ReturnsThis
    public Editor interpret(FunctionCallChainDesc functionCallChain) {
        parse(functionCallChain);
        return this;
    }

    public Object parse(FunctionCallChainDesc functionCallChain) {
        final Object parsedObject;
        final var functionCallExecutor = functionCallMetaExecutor();
        FunctionCallRun chainLinkRun;
        if (functionCallChain.getExpression() instanceof FunctionCallDesc functionCall) {
            chainLinkRun = functionCallExecutor.execute(functionCall, Optional.empty(), this);
            parsedObject = chainLinkRun.getResult().orElseThrow();
        } else if (functionCallChain.getExpression() instanceof NameDesc reference) {
            parsedObject = resolveRaw(reference);
            chainLinkRun = functionCallRun(Optional.of(parsedObject), Optional.of(this));
        } else if (functionCallChain.getExpression() instanceof IntegerDesc integer) {
            parsedObject = integer.getValue();
            chainLinkRun = functionCallRun(Optional.of(parsedObject), Optional.of(this));
        } else if (functionCallChain.getExpression() instanceof StringDesc string) {
            parsedObject = string.getValue();
            chainLinkRun = functionCallRun(Optional.of(parsedObject), Optional.of(this));
        } else {
            throw notImplementedYet();
        }
        Optional<Object> subject = Optional.of(parsedObject);
        for (var nextCall : functionCallChain.getFunctionCalls()) {
            chainLinkRun = FunctionCallMetaExecutor.child(chainLinkRun).execute(nextCall, subject, this);
            subject = chainLinkRun.getResult();
        }
        return parsedObject;
    }
}
