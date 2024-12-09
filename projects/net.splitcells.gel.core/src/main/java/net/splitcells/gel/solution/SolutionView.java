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
package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.lang.tree.XmlConfig;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.problem.ProblemI;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.problem.ProblemView;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping;

import java.nio.file.Path;

import static com.google.common.collect.Streams.concat;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.resource.Files.*;
import static net.splitcells.gel.rating.type.Cost.cost;

public interface SolutionView extends ProblemView {

    default List<List<Constraint>> demandsGroups() {
        return demandsGroups(constraint(), list());
    }

    default List<List<Constraint>> demandsGroups(Constraint constraint, List<Constraint> parentPath) {
        final var constraintPath = parentPath.shallowCopy().withAppended(constraint);
        final List<List<Constraint>> freeGroups = list();
        constraint.casted(ForAll.class)
                .ifPresent(forAllConstraints -> {
                    final var forAllAttributes = forAllAttributesOfGroups
                            (forAllConstraints.classification())
                            .withAppended(
                                    forAllConstraints.classification()
                                            .casted(RaterBasedOnGrouping.class)
                                            .map(e -> forAllAttributesOfGroups(e.classifier()))
                                            .orElseGet(() -> list())
                            );
                    if (forAllAttributes.stream()
                            .anyMatch(attribute -> demands().headerView().contains(attribute))
                    ) {
                        freeGroups.add(constraintPath);
                    }
                });
        constraint.childrenView().stream()
                .map(child -> demandsGroups(child, constraintPath))
                .forEach(freeGroups::addAll);
        return freeGroups;
    }

    private static List<Attribute<?>> forAllAttributesOfGroups(Rater classifier) {
        final List<Attribute<?>> forAllAttributesOfGroups = list();
        classifier.casted(ForAllAttributeValues.class)
                .ifPresent(e -> forAllAttributesOfGroups.add(e.attribute()));
        classifier.casted(ForAllValueCombinations.class)
                .ifPresent(e -> forAllAttributesOfGroups.addAll(e.attributes()));
        return forAllAttributesOfGroups;
    }

    /**
     * <p>TODO Create read only {@link History} and move mutable
     * {@link History} to {@link Solution}, because this interface is read only.</p>
     *
     * <p>TODO Make this method optionally,
     * so that {@link History} recording can be disabled,
     * in order to improve runtime performance.</p>
     *
     * @return
     */
    History history();

    /**
     * TODO IDA This is currently only an idea.
     *
     * @return Returns a {@link Solution}, that based on this.
     */
    default Solution branch() {
        throw notImplementedYet();
    }

    default boolean isComplete() {
        return demandsFree().size() == 0 || (suppliesFree().size() == 0 && demandsFree().size() > 0);
    }

    default boolean isOptimal() {
        return isComplete() && constraint().rating().equalz(cost(0));
    }

    default Path dataContainer() {
        final var standardDocumentFolder = environment().config().configValue(ProcessPath.class)
                .resolve("net/splitcells/gel/GelEnv");
        return standardDocumentFolder
                .resolve(
                        path()
                                .reduced((a, b) -> a + "." + b)
                                .orElse(getClass().getSimpleName()));
    }

    default Path dataContainer(int i) {
        final var standardDocumentFolder = environment().config().configValue(ProcessPath.class)
                .resolve("net/splitcells/gel/GelEnv/" + i);
        return standardDocumentFolder
                .resolve(
                        path()
                                .reduced((a, b) -> a + "." + b)
                                .orElse(getClass().getSimpleName()));
    }

    default void createStandardAnalysis(int i) {
        createAnalysis(dataContainer(i));
    }

    default void createStandardAnalysis() {
        createAnalysis(dataContainer());
    }

    default void createAnalysis(Path targetFolder) {
        createDirectory(targetFolder);
        writeToFile(targetFolder.resolve("index.xml"), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://splitcells.net/den.xsd\">"
                + "</project>");
        writeToFile(targetFolder.resolve("result.analysis.fods"), toFodsTableAnalysis());
        writeToFile(targetFolder.resolve("constraint.graph.xml"), constraint().graph());
        writeToFile(targetFolder.resolve("constraint.rating.xml"), constraint().rating().toTree());
        writeToFile(targetFolder.resolve("constraint.state.xml"), constraint().toTree());
        writeToFile(targetFolder.resolve("demands.fods"), demands().toFods());
        writeToFile(targetFolder.resolve("demands.free.fods"), demandsFree().toFods());
        writeToFile(targetFolder.resolve("demands.used.fods"), demandsUsed().toFods());
        writeToFile(targetFolder.resolve("supplies.fods"), supplies().toFods());
        writeToFile(targetFolder.resolve("supplies.free.fods"), suppliesFree().toFods());
        writeToFile(targetFolder.resolve("supplies.used.fods"), suppliesUsed().toFods());
        if (history().isRegisterEventIsEnabled()) {
            writeToFile(targetFolder.resolve("history.fods"), history().toAnalysisFods());
        }
        writeToFile(targetFolder.resolve("constraint.natural-argumentation.txt")
                , constraint()
                        .naturalArgumentation()
                        .map(Tree::toStringPathsDescription)
                        .orElse(""));
        writeToFile(targetFolder.resolve("results.fods"), toFods());
    }

    /**
     * Using a dedicated XML class might have been a better idea, instead of returning a {@link Tree},
     * as building an AST via {@link Tree} is kind of cumbersome.
     * On the other hand, duplicate String rendering methods can be avoided this way.
     *
     * @return Returns a perspective designed for {@link Tree#toXmlString(XmlConfig)}.
     */
    default Tree toFodsTableAnalysis() {
        final var fodsTableAnalysis = TreeI.tree("document", FODS_OFFICE)
                .withXmlAttribute("mimetype", "application/vnd.oasis.opendocument.spreadsheet", FODS_OFFICE)
                .withChild(fodsStyling());
        final var body = TreeI.tree("body", FODS_OFFICE);
        fodsTableAnalysis.withChild(body);
        final var spreadsheet = TreeI.tree("spreadsheet", FODS_OFFICE);
        body.withChild(spreadsheet);
        final var table = TreeI.tree("table", FODS_TABLE);
        spreadsheet.withChild(table);
        table.withProperty("name", FODS_TABLE, "values");
        table.withChild(attributesOfFodsAnalysis());
        unorderedLines().stream().
                map(this::toLinesFodsAnalysis)
                .forEach(table::withChild);
        return fodsTableAnalysis;
    }

    private static Tree fodsStyling() {
        final var automaticStyling = TreeI.tree("automatic-styles", FODS_OFFICE);
        automaticStyling.withChild(fodsStylingStyle(1, "#dee6ef"));
        automaticStyling.withChild(fodsStylingStyle(2, "#fff5ce"));
        automaticStyling.withChild(fodsStylingStyle(3, "#ffdbb6"));
        automaticStyling.withChild(fodsStylingStyle(4, "#ffd7d7"));
        automaticStyling.withChild(fodsStylingStyle(Integer.MAX_VALUE, "#e0c2cd"));
        return automaticStyling;
    }

    private static Tree fodsStylingStyle(int reasoningComplexity, String backgroundColor) {
        final var style = TreeI.tree("style", FODS_STYLE);
        style.withXmlAttribute("name", "reasoning-complexity-" + reasoningComplexity, FODS_STYLE);
        style.withXmlAttribute("attribute", "table-cell", FODS_STYLE);
        style.withXmlAttribute("parent-style-name", "Default", FODS_STYLE);
        style.withChild(TreeI.tree("table-cell-properties", FODS_STYLE)
                .withXmlAttribute("background-color", backgroundColor, FODS_FO));
        style.withChild(TreeI.tree("text-properties", FODS_STYLE)
                .withXmlAttribute("color", "#000000", FODS_FO));
        return style;
    }

    default Tree attributesOfFodsAnalysis() {
        final var attributes = TreeI.tree("table-row", FODS_TABLE);
        headerView().stream().map(Attribute::name).map(attName -> {
            final var tableElement = TreeI.tree("table-cell", FODS_TABLE);
            final var tableValue = TreeI.tree("p", FODS_TEXT);
            tableElement.withChild(tableValue);
            tableValue.withChild(tree(attName));
            return tableElement;
        }).forEach(attributes::withChild);
        {
            final var tableElement = TreeI.tree("table-cell", FODS_TABLE);
            final var tableValue = TreeI.tree("p", FODS_TEXT);
            tableElement.withChild(tableValue);
            tableValue.withChild(tree("cost"));
            attributes.withChild(tableElement);
        }
        {
            final var tableElement = TreeI.tree("table-cell", FODS_TABLE);
            final var tableValue = TreeI.tree("p", FODS_TEXT);
            tableElement.withChild(tableValue);
            tableValue.withChild(tree("allocation cost argumentation"));
            attributes.withChild(tableElement);
        }
        {
            final var rating = TreeI.tree("table-cell", FODS_TABLE);
            final var ratingValue = TreeI.tree("p", FODS_TEXT);
            rating.withChild(ratingValue);
            attributes.withChild(rating);
            ratingValue.withChild(constraint().rating().toTree());
        }
        return attributes;
    }

    default Tree toLinesFodsAnalysis(Line allocation) {
        final var tableLine = TreeI.tree("table-row", FODS_TABLE);
        {
            headerView().stream().map(attribute -> allocation.value(attribute)).map(value -> {
                final var tableElement = TreeI.tree("table-cell", FODS_TABLE);
                final var tableValue = TreeI.tree("p", FODS_TEXT);
                tableElement.withChild(tableValue);
                tableValue.withChild(tree(value.toString()));
                return tableElement;
            }).forEach(tableCell -> tableLine.withChild(tableCell));
        }
        {
            final var allocationCost = TreeI.tree("table-cell", FODS_TABLE);
            tableLine.withChild(allocationCost);
            final var allocation_cost_value = TreeI.tree("p", FODS_TEXT);
            allocationCost.withChild(allocation_cost_value);
            allocation_cost_value.withChild(
                    tree(""
                            + constraint()
                            .rating(allocation)
                            .asMetaRating()
                            .getContentValue(Cost.class)
                            .value()));
        }
        {
            final var allocationArgumentation = TreeI.tree("table-cell", FODS_TABLE);
            final var allocationArgumentationValue = TreeI.tree("p", FODS_TEXT);
            tableLine.withChild(allocationArgumentation);
            allocationArgumentation.withChild(allocationArgumentationValue);
            constraint().naturalArgumentation(allocation, constraint().injectionGroup())
                    .ifPresent(argumentation -> {
                        final var argumentationPaths = argumentation.toStringPaths();
                        if (!argumentationPaths.isEmpty()) {
                            final int reasoningComplexity;
                            if (argumentationPaths.size() > 4) {
                                reasoningComplexity = Integer.MAX_VALUE;
                            } else {
                                reasoningComplexity = argumentationPaths.size();
                            }
                            allocationArgumentation.withProperty("style-name", FODS_TABLE, "reasoning-complexity-" + reasoningComplexity);
                        }
                        allocationArgumentationValue.withChild(tree(Tree.toStringPathsDescription(argumentationPaths)));
                    });
        }
        return tableLine;
    }

    /**
     * <p>TODO Use generic method to determine the table data to rendered and
     * use this in {@link #toHtmlTable()} and {@link #toFodsTableAnalysis()} as well,
     * in order to standardize table rendering content.</p>
     * <p>TODO This code is a duplication of {@link ProblemI#discoverableRenderer()}</p>
     *
     * @return
     */
    @Override
    default Tree toHtmlTable() {
        final var htmlTable = tree("table", HTML);
        htmlTable.withProperty("class", HTML_ATTRIBUTE, "net-splitcells-website-visually-replaceable");
        final var header = tree("tr", HTML);
        header.withChild(tree("th", HTML).withText("index"));
        headerView().forEach(attribute -> header.withChild(tree("th", HTML).withText(attribute.name())));
        header.withChild(header.withChild(tree("th", HTML).withText("Reasoning")));
        htmlTable.withChild(header);
        unorderedLines().forEach(line -> {
            final var row = tree("tr", HTML);
            row.withChild(tree("td", HTML).withText(line.index() + ""));
            headerView().forEach(attribute -> row.withChild(tree("td", HTML)
                    .withText(line.value(attribute).toString())));
            final var argumentation = constraint().naturalArgumentation(line, constraint().injectionGroup());
            row.withChild(tree("td", HTML).withText(
                    argumentation.map(arg ->
                            Tree.toMultilineStringPathsDescription(arg.toStringPaths())
                    ).orElse("")));
            htmlTable.withChild(row);
        });
        return htmlTable;
    }

    Rating rating(List<OptimizationEvent> events);
}

