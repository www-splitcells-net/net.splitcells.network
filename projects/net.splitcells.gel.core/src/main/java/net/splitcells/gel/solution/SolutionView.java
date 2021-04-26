package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.type.ForAllI;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.problem.ProblemView;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static com.google.common.collect.Streams.concat;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.Xml.toPrettyWithoutHeaderString;
import static net.splitcells.dem.resource.host.Files.*;
import static net.splitcells.gel.rating.type.Cost.cost;

public interface SolutionView extends ProblemView {

    default List<List<Constraint>> demandsGroups() {
        return demandsGroups(constraint(), list());
    }

    default List<List<Constraint>> demandsGroups(Constraint constraint, List<Constraint> parentPath) {
        final var constraintPath = parentPath.shallowCopy().withAppended(constraint);
        final List<List<Constraint>> freeGroups = list();
        constraint.casted(ForAllI.class)
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
                .ifPresent(e -> forAllAttributesOfGroups.add(e.atttribute()));
        classifier.casted(ForAllValueCombinations.class)
                .ifPresent(e -> forAllAttributesOfGroups.addAll(e.attributes()));
        return forAllAttributesOfGroups;
    }

    History history();

    default Solution branch() {
        throw not_implemented_yet();
    }

    default boolean isComplete() {
        return demands_unused().size() == 0 || (supplies_free().size() == 0 && demands_unused().size() > 0);
    }

    default boolean isOptimal() {
        return isComplete() && constraint().rating().equalz(cost(0));
    }

    default Path dataContainer() {
        final var standardDocumentFolder = environment().config().configValue(ProcessPath.class);
        return standardDocumentFolder
                .resolve(
                        path()
                                .reduced((a, b) -> a + "." + b)
                                .orElse(getClass().getSimpleName()));
    }

    default void createStandardAnalysis() {
        createAnalysis(dataContainer());
    }

    default void createAnalysis(Path targetFolder) {
        createDirectory(targetFolder);
        writeToFile(targetFolder.resolve("result.analysis.fods"), toFodsTableAnalysis());
        writeToFile(targetFolder.resolve("constraint.graph.xml"), constraint().graph());
        writeToFile(targetFolder.resolve("constraint.rating.xml"), constraint().rating().toDom());
        writeToFile(targetFolder.resolve("constraint.state.xml"), constraint().toDom());
        writeToFile(targetFolder.resolve("history.fods"), history().toFods());
        writeToFile(targetFolder.resolve("constraint.natural-argumentation.txt"), constraint().naturalArgumentation().toStringPathsDescription());
        writeToFile(targetFolder.resolve("results.fods"), toFods());
    }

    default Element toFodsTableAnalysis() {
        final var fodsTableAnalysis = rElement(FODS_OFFICE, "document");
        fodsTableAnalysis.setAttribute("xmlns:fo", FODS_FO.uri());
        fodsTableAnalysis.setAttribute("xmlns:style", FODS_STYLE.uri());
        fodsTableAnalysis.appendChild(fodsStyling());
        final var analysisContent = elementWithChildren(FODS_OFFICE, "body");
        fodsTableAnalysis.setAttributeNode(attribute(FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fodsTableAnalysis.appendChild(analysisContent);
        {
            final var spreadsheet = elementWithChildren(FODS_OFFICE, "spreadsheet");
            analysisContent.appendChild(spreadsheet);
            final var table = rElement(NameSpaces.FODS_TABLE, "table");
            spreadsheet.appendChild(table);
            table.setAttributeNode(attribute(NameSpaces.FODS_TABLE, "name", "values"));
            {
                table.appendChild(attributesOfFodsAnalysis());
                getLines().stream().
                        map(this::toLinesFodsAnalysis)
                        .forEach(e -> table.appendChild(e));
            }
        }
        return fodsTableAnalysis;
    }

    private static Element fodsStyling() {
        final var automaticStyling = rElement(FODS_OFFICE, "automatic-styles");
        automaticStyling.appendChild(fodsStyling_style(1, "#dee6ef"));
        automaticStyling.appendChild(fodsStyling_style(2, "#fff5ce"));
        automaticStyling.appendChild(fodsStyling_style(3, "#ffdbb6"));
        automaticStyling.appendChild(fodsStyling_style(4, "#ffd7d7"));
        automaticStyling.appendChild(fodsStyling_style(Integer.MAX_VALUE, "#e0c2cd"));
        return automaticStyling;
    }

    private static Element fodsStyling_style(int reasoningComplexity, String backgroundColor) {
        final var style = rElement(FODS_STYLE, "style");
        style.setAttributeNodeNS(attribute(FODS_STYLE, "name", "reasoning-complexity-" + reasoningComplexity));
        style.setAttributeNodeNS(attribute(FODS_STYLE, "family", "table-cell"));
        style.setAttributeNodeNS(attribute(FODS_STYLE, "parent-style-name", "Default"));
        {
            final var table_cell_properties = elementWithChildren(FODS_STYLE, "table-cell-properties");
            table_cell_properties.setAttributeNodeNS(attribute(FODS_FO, "background-color", backgroundColor));
            style.appendChild(table_cell_properties);
        }
        {
            final var textProperties = elementWithChildren(FODS_STYLE, "text-properties");
            textProperties.setAttributeNodeNS(attribute(FODS_FO, "color", "#000000"));
            style.appendChild(textProperties);
        }
        return style;
    }

    default Element attributesOfFodsAnalysis() {
        final var attributes = elementWithChildren(NameSpaces.FODS_TABLE, "table-row");
        headerView().stream().map(att -> att.name()).map(attName -> {
            final var tableElement = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableElement.appendChild(tableValue);
            tableValue.appendChild(Xml.textNode(attName));
            return tableElement;
        }).forEach(attributeDescription -> attributes.appendChild(attributeDescription));
        {
            final var tableElement = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableElement.appendChild(tableValue);
            tableValue.appendChild(Xml.textNode("cost"));
            attributes.appendChild(tableElement);
        }
        {
            final var tableElement = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableElement.appendChild(tableValue);
            tableValue.appendChild(Xml.textNode("allocation cost argumentation"));
            attributes.appendChild(tableElement);
        }
        {
            final var rating = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            final var ratingValue = rElement(NameSpaces.FODS_TEXT, "p");
            rating.appendChild(ratingValue);
            attributes.appendChild(rating);
            ratingValue.appendChild(
                    Xml.textNode(
                            toPrettyWithoutHeaderString(
                                    constraint()
                                            .rating()
                                            .toDom()
                            )));
        }
        return attributes;
    }

    default Element toLinesFodsAnalysis(Line allocation) {
        final var tableLine = elementWithChildren(NameSpaces.FODS_TABLE, "table-row");
        {
            headerView().stream().map(attribute -> allocation.value(attribute)).map(value -> {
                final var tableElement = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
                final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
                tableElement.appendChild(tableValue);
                tableValue.appendChild(Xml.textNode(value.toString()));
                return tableElement;
            }).forEach(tableCell -> tableLine.appendChild(tableCell));
        }
        {
            final var allocationCost = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            tableLine.appendChild(allocationCost);
            final var allocation_cost_value = rElement(NameSpaces.FODS_TEXT, "p");
            allocationCost.appendChild(allocation_cost_value);
            allocation_cost_value.appendChild(
                    textNode(""
                            + constraint()
                            .rating(allocation)
                            .getContentValue(Cost.class)
                            .value()));
        }
        {
            final var allocationArgumentation = elementWithChildren(NameSpaces.FODS_TABLE, "table-cell");
            final var allocationArgumentationValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableLine.appendChild(allocationArgumentation);
            allocationArgumentation.appendChild(allocationArgumentationValue);
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
                            allocationArgumentation.setAttributeNodeNS(attribute(FODS_TABLE, "style-name", "reasoning-complexity-" + reasoningComplexity));
                        }
                        allocationArgumentationValue.appendChild
                                (Xml.textNode
                                        (Perspective.toStringPathsDescription(argumentationPaths)));
                    });
        }
        return tableLine;
    }

    Rating rating(List<OptimizationEvent> events);
}

