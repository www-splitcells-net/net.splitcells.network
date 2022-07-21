package net.splitcells.gel.constraint.type.framework;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.constraint.intermediate.data.AllocationRating;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * TODO Make this aspect optional for {@link Constraint}s.
 */
public class ConstraintAspect implements Constraint {

    public static ConstraintAspect constraintAspect(Constraint constraint) {
        return new ConstraintAspect(constraint);
    }

    private final Map<GroupId, Rating> ratingCache = map();
    private final Constraint constraint;

    private ConstraintAspect(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public List<String> path() {
        return constraint.path();
    }

    @Override
    public net.splitcells.dem.data.set.Set<List<String>> paths() {
        return constraint.paths();
    }

    @Override
    public void addContext(Discoverable context) {
        constraint.addContext(context);
    }

    @Override
    public List<Domable> arguments() {
        return constraint.arguments();
    }

    @Override
    public Class<? extends Constraint> type() {
        return constraint.type();
    }

    @Override
    public GroupId injectionGroup() {
        return constraint.injectionGroup();
    }

    @Override
    public Rating rating(GroupId group, Line line) {
        return constraint.rating(group, line);
    }

    @Override
    public Rating rating(GroupId group) {
        return ratingCache.computeIfAbsent(group, g -> constraint.rating(group));
    }

    @Override
    public Optional<Perspective> naturalArgumentation(GroupId group) {
        return constraint.naturalArgumentation(group);
    }

    @Override
    public Optional<Discoverable> mainContext() {
        return constraint.mainContext();
    }

    @Override
    public Optional<Perspective> naturalArgumentation(Line line, GroupId group, Predicate<AllocationRating> allocationSelector) {
        return constraint.naturalArgumentation(line, group, allocationSelector);
    }

    @Override
    public GroupId groupOf(Line line) {
        return constraint.groupOf(line);
    }

    @Override
    public void registerAdditions(GroupId group, Line line) {
        ratingCache.remove(group);
        constraint.registerAdditions(group, line);
    }

    @Override
    public void registerBeforeRemoval(GroupId group, Line line) {
        ratingCache.remove(group);
        constraint.registerBeforeRemoval(group, line);
    }

    @Override
    public List<Constraint> childrenView() {
        ratingCache.clear();
        return constraint.childrenView();
    }

    @Override
    public Set<Line> complying(GroupId group) {
        return constraint.complying(group);
    }

    @Override
    public net.splitcells.dem.data.set.Set<Line> defying(GroupId group) {
        return constraint.defying(group);
    }

    @Override
    public Line addResult(LocalRating localRating) {
        return constraint.addResult(localRating);
    }

    @Override
    public Table lineProcessing() {
        return constraint.lineProcessing();
    }

    @Override
    public Element toDom() {
        return constraint.toDom();
    }

    @Override
    public Element toDom(Set<GroupId> groups) {
        return constraint.toDom(groups);
    }

    @Override
    public Table lines() {
        return constraint.lines();
    }

    @Override
    public Constraint withChildren(Constraint... constraints) {
        ratingCache.clear();
        constraint.withChildren(constraints);
        return this;
    }

    @Override
    public Constraint withChildren(Function<Query, Query> builder) {
        ratingCache.clear();
        constraint.withChildren(builder);
        return this;
    }
}
