package net.splitcells.gel.constraint;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.common.Language.ARGUMENTATION;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.table.attribute.ListAttribute.listAttribute;
import static net.splitcells.gel.rating.structure.MetaRating.neutral;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.constraint.intermediate.data.AllocationSelector;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;

public interface Constraint extends AfterAdditionSubscriber, BeforeRemovalSubscriber, ConstraintWriter, Discoverable, PubliclyTyped<Constraint>, PubliclyConstructed<Domable>, Domable {
    Attribute<Line> LINE = attribute(Line.class, "line");
    Attribute<java.util.List<Constraint>> PROPAGATION_TO = listAttribute(Constraint.class, "propagation to");
    Attribute<GroupId> INCOMING_CONSTRAINT_GROUP = attribute(GroupId.class, "incoming constraint group");
    Attribute<GroupId> RESULTING_CONSTRAINT_GROUP = attribute(GroupId.class, "resulting constraint group");
    Attribute<Rating> RATING = attribute(Rating.class, "rating");

    static List<List<Constraint>> allocationGroups(List<Constraint> currentPath) {
        final var constraint = currentPath.lastValue().get();
        final List<List<Constraint>> allocationGroups = list();
        allocationGroups.add(currentPath);
        allocationGroups.addAll(
                constraint.childrenView().stream()
                        .map(child -> allocationGroups(listWithValuesOf(currentPath).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return allocationGroups;
    }

    static List<List<Constraint>> allocationGroups(Constraint constraint) {
        return allocationGroups(list(constraint));
    }

    GroupId injectionGroup();

    default MetaRating rating() {
        return rating(injectionGroup());
    }

    static GroupId standardGroup() {
        return GroupId.group("for-all");
    }

    default MetaRating rating(Line line) {
        return event(injectionGroup(), line);
    }

    MetaRating event(GroupId group, Line line);

    MetaRating rating(GroupId group);

    default Perspective naturalArgumentation() {
        return naturalArgumentation(injectionGroup());
    }

    Perspective naturalArgumentation(GroupId group);

    Optional<Discoverable> mainContext();

    default Optional<Perspective> naturalArgumentation(Line subject, GroupId group) {
        return naturalArgumentation(subject, group, AllocationSelector::selectLinesWithCost);
    }

    Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector);

    default MetaRating rating(Set<GroupId> groups) {
        return groups.stream().
                map(group -> rating(group)).
                reduce((a, b) -> a.combine(b)).
                orElseGet(() -> neutral());
    }

    default GroupId register(Line line) {
        final var injectionGroup = injectionGroup();
        register_additions(injectionGroup, line);
        return injectionGroup;
    }

    GroupId groupOf(Line line);

    void register_additions(GroupId group, Line line);

    default void register_addition(Line line) {
        register_additions(injectionGroup(), line);
    }

    void register_before_removal(GroupId group, Line line);

    default void register_before_removal(Line line) {
        register_before_removal(injectionGroup(), line);
    }

    List<Constraint> childrenView();

    Set<Line> complying(GroupId group);

    default Set<Line> complying() {
        return complying(injectionGroup());
    }

    Set<Line> defying(GroupId group);

    default Set<Line> defying() {
        return defying(injectionGroup());
    }

    Line addResult(LocalRating localRating);

    default Query query() {
        return QueryI.query(this);
    }

    Table lineProcessing();

    Element toDom();

    Element toDom(Set<GroupId> groups);

    default Set<Constraint> parentOf(Constraint constraint) {
        if (equals(constraint)) {
            return setOfUniques(this);
        }
        return childrenView().stream()
                .map(child -> child.parentOf(constraint))
                .reduce((a, b) -> Sets.merge(a, b))
                .orElseGet(() -> setOfUniques());
    }

    @Deprecated
    default Set<GroupId> childGroups(Line lines, Constraint subject) {
        final Set<GroupId> childGroups = setOfUniques();
        if (equals(subject)) {
            childGroups.add(groupOf(lines));
        } else {
            childrenView().forEach(child -> childGroups.addAll(child.childGroups(lines, subject)));
        }
        return childGroups;
    }

    static Set<GroupId> incomingGroupsOfConstraintPath(List<Constraint> constraintPath) {
        return incomingGroupsOfConstraintPath(constraintPath, setOfUniques(constraintPath.get(0).injectionGroup()));
    }

    static Set<GroupId> incomingGroupsOfConstraintPath(List<Constraint> constraintPath, Set<GroupId> injectionGroups) {
        if (constraintPath.isEmpty()) {
            throw new IllegalArgumentException("No path present.");
        }
        if (constraintPath.size() == 1) {
            return injectionGroups;
        }
        final var pathHead = constraintPath.remove(0);
        return incomingGroupsOfConstraintPath
                (constraintPath
                        , injectionGroups.stream()
                                .map(pathHead::childGroups)
                                .flatMap(Collection::stream)
                                .collect(toSetOfUniques()));
    }

    default Set<GroupId> childGroups(GroupId incomingGroup) {
        return lineProcessing().columnView(INCOMING_CONSTRAINT_GROUP)
                .lookup(incomingGroup)
                .getLines()
                .stream()
                .map(lineResult -> lineResult.value(RESULTING_CONSTRAINT_GROUP))
                .collect(toSetOfUniques());
    }

    default Element graph() {
        final var graph = Xml.rElement(GEL, type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> graph.appendChild(Xml.elementWithChildren(ARGUMENTATION.value(), arg.toDom())));
        }
        childrenView().forEach(child -> {
            graph.appendChild(child.graph());
        });
        return graph;
    }

    default void persistGraphState() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path().toString() + ".fods"), lineProcessing().toFods());
        childrenView().forEach(Constraint::persistGraphState);
    }

    @Override
    default Constraint withChildren(List<Function<Query, Query>> builder) {
        builder.forEach(this::withChildren);
        return this;
    }
}
