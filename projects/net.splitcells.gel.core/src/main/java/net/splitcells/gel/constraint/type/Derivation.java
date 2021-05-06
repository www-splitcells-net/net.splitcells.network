package net.splitcells.gel.constraint.type;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;

public final class Derivation implements Constraint {

    public static Derivation derivation
            (Constraint derivationTarget, Function<MetaRating, MetaRating> derivationFunction) {
        return new Derivation(derivationTarget, derivationFunction);
    }

    private final Constraint derivationTarget;
    private final Function<MetaRating, MetaRating> derivationFunction;

    private Derivation(Constraint derivationTarget, Function<MetaRating, MetaRating> derivationFunction) {
        this.derivationTarget = derivationTarget;
        this.derivationFunction = derivationFunction;
    }

    @Override
    public GroupId injectionGroup() {
        return derivationTarget.injectionGroup();
    }

    @Override
    public MetaRating event(GroupId group, Line line) {
        return derivationFunction.apply(derivationTarget.event(group, line));
    }

    @Override
    public MetaRating rating(GroupId group) {
        return derivationFunction.apply(derivationTarget.rating(group));
    }

    @Override
    public Perspective naturalArgumentation(GroupId group) {
        throw notImplementedYet();
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return derivationTarget.mainContext();
    }

    @Override
    public Optional<Perspective> naturalArgumentation
            (Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        throw notImplementedYet();
    }

    @Override
    public GroupId groupOf(Line line) {
        return derivationTarget.groupOf(line);
    }

    @Override
    public void register_additions(GroupId group, Line line) {
        throw notImplementedYet();
    }

    @Override
    public void register_before_removal(GroupId group, Line line) {
        throw notImplementedYet();
    }

    @Override
    public List<Constraint> childrenView() {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> complying(GroupId group) {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> defying(GroupId group) {
        throw notImplementedYet();
    }

    @Override
    public Line addResult(LocalRating localRating) {
        throw notImplementedYet();
    }

    @Override
    public Allocations lineProcessing() {
        throw notImplementedYet();
    }

    @Override
    public Element toDom() {
        throw notImplementedYet();
    }

    @Override
    public Element toDom(Set<GroupId> groups) {
        throw notImplementedYet();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final var path = derivationTarget.path();
        path.add(getClass().getSimpleName());
        return path;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public Class<? extends Constraint> type() {
        throw notImplementedYet();
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        throw notImplementedYet();
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        throw notImplementedYet();
    }
}
