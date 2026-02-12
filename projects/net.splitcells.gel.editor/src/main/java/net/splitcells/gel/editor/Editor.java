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
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.need.Need;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.TableFormatting;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.geal.FunctionCallRecord;
import net.splitcells.gel.editor.geal.lang.*;
import net.splitcells.gel.editor.geal.runners.FunctionCallMetaExecutor;
import net.splitcells.gel.editor.geal.runners.FunctionCallRun;
import net.splitcells.gel.editor.lang.SourceCodeQuotation;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;
import net.splitcells.website.Format;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.need.NeedsCheck.checkNeed;
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
    @Getter private final Map<String, Integer> integerVariables = map();
    private final List<FunctionCallRecord> functionCallRecords = list();
    @Getter @Setter private boolean isRecording = false;
    private final Map<String, EditorData> data = map();

    /**
     * @return Every {@link List} start has a final {@link Solution}.
     * All following {@link Solution} in the {@link List} are the respective level of {@link Solution#demands()} or
     * {@link Solution#supplies()} of the final {@link Solution}.
     * @see #finalSolutions()
     */
    public List<List<Solution>> solutionPaths() {
        val solutionPaths = Lists.<List<Solution>>list();
        finalSolutions().forEach(fs -> solutionPaths.addAll(fs.solutionPaths()));
        return solutionPaths;
    }

    /**
     *
     * @return Final {@link Solution} are not {@link Solution#demands()} or {@link Solution#supplies()}
     * of other {@link Solution}.
     */
    public List<Solution> finalSolutions() {
        val processed = Sets.<Solution>setOfUniques();
        val finalSolutions = Sets.<Solution>setOfUniques();
        solutions.values().forEach(s -> {
            processed.add(s);
            val sHasParents = solutions.values().stream().map(otherS ->
                            otherS.demands().lookupAsSolution().map(otherSD -> otherSD.equals(s)).orElse(false)
                                    && otherS.supplies().lookupAsSolution().map(otherSS -> otherSS.equals(s)).orElse(false))
                    .reduce((a, b) -> a || b)
                    .orElse(false);
            if (!sHasParents) {
                finalSolutions.add(s);
            }
        });
        return listWithValuesOf(finalSolutions);
    }

    public Optional<String> lookupTableLikeName(Table table) {
        val tableMatches = tables.entrySet().stream().filter(entry -> entry.getValue().equals(table))
                .map(entry -> entry.getKey())
                .toList();
        if (!tableMatches.isEmpty()) {
            return Optional.of(tableMatches.getFirst());
        }
        val solutionMatch = solutions.entrySet().stream().filter(entry -> entry.getValue().equals(table))
                .map(entry -> entry.getKey())
                .findFirst();
        if (solutionMatch.isEmpty()) {
            throw execException(tree("Could not find table name.")
                    .withProperty("table", table.toSimplifiedCSV()));
        }
        return Optional.of(solutionMatch.get());
    }

    private Editor(String argName, Discoverable argParent) {
        name = argName;
        parent = argParent;
    }

    @Override
    public List<String> path() {
        return parent.path().withAppended(name);
    }

    public Optional<Object> resolveRaw(NameDesc name) {
        if (attributes.hasKey(name.getValue())) {
            return Optional.of(attributes.get(name.getValue()));
        } else if (tables.hasKey(name.getValue())) {
            return Optional.of(tables.get(name.getValue()));
        } else if (solutions.hasKey(name.getValue())) {
            return Optional.of(solutions.get(name.getValue()));
        } else if (raters.hasKey(name.getValue())) {
            return Optional.of(raters.get(name.getValue()));
        } else if (integerVariables.hasKey(name.getValue())) {
            return Optional.of(integerVariables.get(name.getValue()));
        }
        return Optional.empty();
    }

    public EditorData loadData(Format format, String name) {
        return data.getOptionally(name).orElseGet(() -> {
            val newValue = editorData(format, new byte[0]);
            data.put(name, newValue);
            return newValue;
        });
    }

    public EditorData loadData(String dataKey, Need<Map<String, EditorData>> need) {
        checkNeed(need, data);
        return loadData(dataKey);
    }

    public EditorData loadData(String dataKey) {
        return data.get(dataKey);
    }

    public Set<String> dataKeys() {
        return data.keySet2();
    }

    @ReturnsThis
    public Editor saveData(String name, EditorData content) {
        if (data.hasKey(name)) {
            throw execException(tree("The data to be saved is already present.")
                    .withProperty("Save name", name)
                    .withProperty("Existing data", data.toString()));
        }
        data.put(name, content);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(NameDesc name, SourceCodeQuotation context) {
        if (attributes.hasKey(name.getValue())) {
            return (T) attributes.get(name.getValue());
        } else if (tables.hasKey(name.getValue())) {
            return (T) tables.get(name.getValue());
        } else if (solutions.hasKey(name.getValue())) {
            return (T) solutions.get(name.getValue());
        } else if (raters.hasKey(name.getValue())) {
            return (T) raters.get(name.getValue());
        } else {
            throw execException(tree("Could not resolve variable by name `" + name.getValue() + "`.")
                    .withProperty("Context", context.getSourceCodeQuote().userReferenceTree()));
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
        val varName = variableDefinition.getName().getValue();
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
        val parsedObject = parse(variableDefinition.getFunctionCallChain());
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
        } else if (parsedObject instanceof Integer integer) {
            integerVariables.put(varName, integer);
        } else {
            throw execException(tree(parsedObject.getClass().getName() + " is not supported for variables.")
                    .withChild(tree("Affected variable definition")
                            .withChildren(variableDefinition.getSourceCodeQuote().userReferenceTrees())));
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
        val functionCallExecutor = functionCallMetaExecutor();
        FunctionCallRun chainLinkRun;
        if (functionCallChain.getExpression() instanceof FunctionCallDesc functionCall) {
            chainLinkRun = functionCallExecutor.execute(functionCall, Optional.empty(), this);
            parsedObject = chainLinkRun.getResult().orElseThrow();
        } else if (functionCallChain.getExpression() instanceof NameDesc reference) {
            val lookup = resolveRaw(reference);
            if (lookup.isEmpty()) {
                throw execException(tree("Could not find object by name.")
                        .withProperty("name", reference.getValue())
                        .withChild(functionCallChain.getExpression().getSourceCodeQuote().userReferenceTree()));
            }
            parsedObject = lookup.get();
            chainLinkRun = functionCallRun(lookup, Optional.of(this));
        } else if (functionCallChain.getExpression() instanceof IntegerDesc integer) {
            parsedObject = integer.getValue();
            chainLinkRun = functionCallRun(Optional.of(parsedObject), Optional.of(this));
        } else if (functionCallChain.getExpression() instanceof StringDesc string) {
            parsedObject = string.getValue();
            chainLinkRun = functionCallRun(Optional.of(parsedObject), Optional.of(this));
        } else {
            throw execException(tree(functionCallChain.getExpression().getClass().getName() + " is not supported as the base of expressions.")
                    .withChild(tree("Affected function call chain")
                            .withChildren(functionCallChain.getSourceCodeQuote().userReferenceTrees())));
        }
        Optional<Object> subject = Optional.of(parsedObject);
        for (var nextCall : functionCallChain.getFunctionCalls()) {
            chainLinkRun = FunctionCallMetaExecutor.child(chainLinkRun).execute(nextCall, subject, this);
            subject = chainLinkRun.getResult();
        }
        return subject.orElseThrow();
    }

    public Editor addRecord(FunctionCallRecord argRecord) {
        if (isRecording) {
            val matches = functionCallRecords.stream()
                    .filter(fcr -> fcr.getName().equals(argRecord.getName()) && fcr.getVariation() == argRecord.getVariation())
                    .toList();
            if (matches.isEmpty()) {
                functionCallRecords.add(argRecord);
            }
        }
        return this;
    }

    public FunctionCallRecord functionCallRecord(Optional<Object> subject, FunctionCallDesc functionCall, String name, int variation) {
        return FunctionCallRecord.functionCallRecord(subject, functionCall, this, name, variation);
    }

    public FunctionCallRecord functionCallRecord(Optional<Object> subject, FunctionCallDesc functionCall, String name, int variation, boolean isRecording) {
        return FunctionCallRecord.functionCallRecord(subject, functionCall, this, name, variation, isRecording);
    }
}
