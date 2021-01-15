package net.splitcells.gel.constraint.type.framework;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.resource.host.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.constraint.intermediate.data.AllocationRating.lineRating;
import static net.splitcells.gel.constraint.Report.report;
import static net.splitcells.gel.constraint.intermediate.data.RoutingResult.routingResult;
import static net.splitcells.gel.rating.structure.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.constraint.intermediate.data.RoutingRating;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;

@Deprecated
public abstract class ConstraintAI implements Constraint {
    private final GroupId injectionGroup;
    protected final net.splitcells.dem.data.set.list.List<Constraint> children = list();
    protected Optional<Discoverable> mainContext = Optional.empty();
    private final List<Discoverable> contexts = list();
    protected final Database lines;
    protected final Database results = Databases.database("results", this, RESULTING_CONSTRAINT_GROUP, RATING, PROPAGATION_TO);
    protected final Allocations lineProcessing;
    protected final Map<GroupId, Rating> groupProcessing = map();

    protected ConstraintAI(GroupId injectionGroup) {
        this(injectionGroup, "");
    }

    protected ConstraintAI(GroupId injectionGroup, String name) {
        this.injectionGroup = injectionGroup;
        lines = Databases.database(name + ".lines", this, LINE, INCOMING_CONSTRAINT_GROUP);
        lineProcessing = allocations("linesProcessing", lines, results);
        lineProcessing.subscribe_to_afterAdditions(this::propagateAddition);
        lineProcessing.subscriber_to_beforeRemoval(this::propagateRemoval);
        lines.subscribe_to_afterAdditions(this::process_line_addtion);
    }

    protected ConstraintAI() {
        this(Constraint.standardGroup());
    }

    @Override
    public GroupId injectionGroup() {
        return injectionGroup;
    }

    @Override
    public void register_additions(GroupId injectionGroup, Line addition) {
        // TODO Move this to differnt project.
        if (TRACING) {
            domsole().append
                    (element("register-additions." + Constraint.class.getSimpleName()
                            , element("additions", addition.toDom())
                            , element("injectionGroup", textNode(injectionGroup.toString()))
                            )
                            , this
                            , DEBUG);
        }
        // TODO Move this to differnt project.
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(
                    lines
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(injectionGroup)
                            .columnView(LINE)
                            .lookup(addition)
                            .getLines()
            ).isEmpty();
        }
        lines.addTranslated(list(addition, injectionGroup));
    }

    @Override
    public void register_before_removal(GroupId ienākošaGrupaId, Line noņemšana) {
        // DARĪT Kustēt uz ārpuses projektu.
        if (ENFORCING_UNIT_CONSISTENCY) {
            Assertions.assertThat(noņemšana.isValid()).isTrue();
        }
        process_lines_beforeRemoval(ienākošaGrupaId, noņemšana);
        lineProcessing
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(ienākošaGrupaId)
                .columnView(LINE)
                .lookup(noņemšana)
                .getLines()
                .forEach(lineProcessing::remove);
        // JAUDA
        lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(line -> line.value(LINE).equals(noņemšana))
                .filter(line -> line.value(INCOMING_CONSTRAINT_GROUP).equals(ienākošaGrupaId))
                .forEach(lines::remove);
    }

    protected abstract void process_line_addtion(Line addition);

    protected void process_lines_beforeRemoval(GroupId injectionGroup, Line removal) {
        lineProcessing.supplies_free().rawLinesView().stream()
                .filter(e -> e != null)
                .forEach(freeSupply -> results.remove(freeSupply));
    }

    protected void propagateAddition(Line addition) {
        addition.value(PROPAGATION_TO).forEach(child ->
                child.register_additions
                        (addition.value(RESULTING_CONSTRAINT_GROUP)
                                , addition.value(LINE)));
    }

    protected void propagateRemoval(Line removal) {
        removal.value(PROPAGATION_TO).forEach(child ->
                child.register_before_removal
                        (removal.value(RESULTING_CONSTRAINT_GROUP)
                                , removal.value(LINE)));
    }

    @Override
    public Constraint withChildren(Constraint... children) {
        asList(children).forEach(child -> {
            this.children.add(child);
            child.addContext(this);
        });
        return this;
    }

    public Set<Line> complying(GroupId group, Set<Line> allocations) {
        final Set<Line> complying = setOfUniques();
        allocations.forEach(allocation -> {
            if (event(group, allocation.value(LINE)).equals(noCost())) {
                complying.add(lineProcessing.demand_of_allocation(allocation).value(LINE));
            }
        });
        return complying;
    }

    public Set<Line> defying(GroupId group, Set<Line> allocations) {
        final Set<Line> defying = setOfUniques();
        allocations.forEach(allocation -> {
            if (!event(group, allocation.value(LINE)).equals(noCost())) {
                defying.add(lineProcessing.demand_of_allocation(allocation).value(LINE));
            }
        });
        return defying;
    }

    @Override
    public MetaRating event(GroupId group, Line line) {
        final var routingRating
                = routingRating(group, processedLine -> processedLine.value(LINE).equalsTo(line));
        routingRating.children_to_groups().forEach((child, groups) ->
                groups.forEach(group2 -> routingRating.events().add(child.event(group2, line)))
        );
        if (routingRating.events().isEmpty()) {
            return metaRating(noCost());
        }
        return routingRating.events().stream()
                .reduce((a, b) -> a.combine(b))
                .get()
                .asMetaRating();
    }

    protected RoutingRating routingRating
            (GroupId group, Predicate<Line> lineSelector) {
        final var routingRating = RoutingRating.create();
        lineProcessing.rawLinesView().forEach(line -> {
            if (line != null
                    && lineSelector.test(line)
                    && group.equals(line.value(INCOMING_CONSTRAINT_GROUP))) {
                routingRating.events().add(line.value(RATING));
                line.value(Constraint.PROPAGATION_TO).forEach(child -> {
                    final Set<GroupId> groupsOfChild;
                    if (!routingRating.children_to_groups().containsKey(child)) {
                        groupsOfChild = setOfUniques();
                        routingRating.children_to_groups().put(child, groupsOfChild);
                    } else {
                        groupsOfChild = routingRating.children_to_groups().get(child);
                    }
                    groupsOfChild.add(line.value(RESULTING_CONSTRAINT_GROUP));
                });
            }
        });
        return routingRating;
    }

    @Override
    public MetaRating rating(GroupId group) {
        final var routingRating
                = routingRating(group, lineSelector -> true);
        routingRating.children_to_groups().forEach((children, groups) ->
                groups.forEach(group2 -> routingRating.events().add(children.rating(group2))));
        if (routingRating.events().isEmpty()) {
            return metaRating(noCost());
        }
        return routingRating.events().stream()
                .reduce((a, b) -> a.combine(b))
                .get()
                .asMetaRating();
    }

    /**
     * TODO PERFORMANCE
     */
    @Override
    public List<Constraint> childrenView() {
        return listWithValuesOf(children);
    }

    public Set<Line> allocationsOf(GroupId group) {
        final Set<Line> allocations = setOfUniques();
        lineProcessing.rawLinesView().forEach(line -> {
            if (line != null && line.value(INCOMING_CONSTRAINT_GROUP).equals(group)) {
                allocations.add(line);
            }
        });
        return allocations;
    }

    public Set<Line> complying(GroupId group) {
        return complying(group, allocationsOf(group));
    }

    public Set<Line> defying(GroupId group) {
        return defying(group, allocationsOf(group));
    }

    @Override
    public Allocations lineProcessing() {
        return lineProcessing;
    }

    public Table lines() {
        return lines;
    }

    public GroupId groupOf(Line line) {
        return lineProcessing()
                .rawLinesView()
                .get(line.index())
                .value(RESULTING_CONSTRAINT_GROUP);
    }

    @Override
    public Line addResult(LocalRating localRating) {
        return results.addTranslated
                (list
                        (localRating.resultingConstraintGroupId()
                                , localRating.rating()
                                , localRating.propagateTo()));
    }

    @Override
    public void addContext(Discoverable context) {
        if (mainContext.isEmpty()) {
            mainContext = Optional.of(context);
        }
        contexts.add(context);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        builder.apply(QueryI.query(this, Optional.empty()));
        return this;
    }

    @Override
    public Element toDom() {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.element("rating", rating().toDom()));
        {
            final var ratings = Xml.element("ratings");
            dom.appendChild(ratings);
            lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP)
                    .lookup(injectionGroup())
                    .getLines()
                    .forEach(line -> ratings.appendChild(line.toDom()));
        }
        childrenView().forEach(bērns ->
                dom.appendChild(
                        bērns.toDom(
                                setOfUniques(results
                                        .columnView(RESULTING_CONSTRAINT_GROUP)
                                        .values()))));
        return dom;
    }

    @Override
    public Element toDom(Set<GroupId> groups) {
        final var dom = Xml.element(type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> dom.appendChild(Xml.element(ARGUMENTATION.value(), arg.toDom())));
        }
        dom.appendChild(Xml.element("rating", rating(groups).toDom()));
        {
            final var ratings = Xml.element("ratings");
            dom.appendChild(ratings);
            groups.forEach(group ->
                    lineProcessing
                            .columnView(INCOMING_CONSTRAINT_GROUP)
                            .lookup(group)
                            .getLines().
                            forEach(line -> ratings.appendChild(line.toDom())));
        }
        childrenView().forEach(child ->
                dom.appendChild(
                        child.toDom(
                                groups.stream().
                                        map(group -> lineProcessing
                                                .columnView(INCOMING_CONSTRAINT_GROUP)
                                                .lookup(group)
                                                .getLines()
                                                .stream()
                                                .map(groupLines -> groupLines.value(RESULTING_CONSTRAINT_GROUP))
                                                .collect(toSet()))
                                        .flatMap(resultingGroupings -> resultingGroupings.stream())
                                        .collect(toSet()))));
        return dom;
    }

    protected abstract String localNaturalArgumentation(Report report);

    protected Optional<String> localNaturalArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        final var localNaturalArgumentation
                = lineProcessing
                .columnView(LINE)
                .lookup(line)
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .getLines()
                .stream()
                .filter(allocation -> allocationSelector
                        .test(lineRating
                                (allocation
                                        , event(allocation.value(INCOMING_CONSTRAINT_GROUP), line))))
                .map(allocation -> report
                        (allocation.value(LINE)
                                , allocation.value(INCOMING_CONSTRAINT_GROUP)
                                , allocation.value(RATING)))
                .map(report -> localNaturalArgumentation(report))
                .findFirst();
        return localNaturalArgumentation;
    }

    @Override
    public Perspective naturalArgumentation(GroupId group) {
        final var naturalArgumentation = lineProcessing
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .getLines()
                .stream()
                .map(allocation -> allocation.value(LINE))
                .map(line -> naturalArgumentation(line, group, AllocationSelector::selectLinesWithCost))
                .collect(toList());
        if (naturalArgumentation.size() == 1) {
            if (naturalArgumentation.get(0).isPresent()) {
                return naturalArgumentation.get(0).get();
            } else {
                return perspective(ARGUMENTATION.value(), GEL);
            }
        }
        final var localArgumentation = perspective(ARGUMENTATION.value(), GEL);
        naturalArgumentation
                .forEach(naturalReasoning -> naturalReasoning.ifPresent(localArgumentation::withChild));
        return localArgumentation;
    }

    @Override
    public Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        final var localArgumentation = localNaturalArgumentation(line, group, allocationSelector);
        final var childrenArgumentation = childrenArgumentation(line, group, allocationSelector);
        if (localArgumentation.isEmpty() && childrenArgumentation.isEmpty()) {
            return Optional.empty();
        } else if (!localArgumentation.isEmpty()) {
            return Optional.of(perspective(localArgumentation.get(), NameSpaces.STRING)
                    .withChildren(childrenArgumentation));
        } else {
            return Optional.of(perspective(ARGUMENTATION.value(), GEL)
                    .withChildren(childrenArgumentation));
        }
    }

    protected List<Perspective> childrenArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return lineProcessing
                .columnView(LINE)
                .lookup(line)
                .columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(group)
                .getLines()
                .stream()
                .filter(allocation
                        -> allocationSelector
                        .test(lineRating
                                (allocation
                                        , event(allocation.value(INCOMING_CONSTRAINT_GROUP), line))))
                .map(allocation -> allocation.value(PROPAGATION_TO)
                        .stream()
                        .map(propagatedTo ->
                                routingResult
                                        (allocation.value(RESULTING_CONSTRAINT_GROUP)
                                                , propagatedTo)))
                .flatMap(e -> e)
                .map(routingResult
                        -> routingResult
                        .propagation()
                        .naturalArgumentation(line, routingResult.group()))
                .filter(e -> e.isPresent())
                .map(e -> e.get())
                .collect(toList());
    }

    public Optional<Discoverable> mainContext() {
        return mainContext;
    }
}
