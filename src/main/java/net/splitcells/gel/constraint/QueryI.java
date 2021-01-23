package net.splitcells.gel.constraint;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.rating.structure.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import java.util.Collection;
import java.util.Optional;

import net.splitcells.dem.data.set.Sets;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class QueryI implements Query {
    public static Query query(Constraint constraint, Optional<Constraint> root) {
        return new QueryI(constraint, list(constraint.injectionGroup()), root);
    }

    public static Query query(Constraint constraint) {
        return new QueryI(constraint, list(constraint.injectionGroup()), Optional.of(constraint));
    }

    public static Query query(Constraint constraint, Collection<GroupId> groups, Constraint root) {
        return new QueryI(constraint, groups, Optional.of(root));
    }

    private final Optional<Constraint> root;
    private final Constraint constraint;
    private final Collection<GroupId> groups;

    public QueryI(Constraint constraint, Collection<GroupId> groups, Optional<Constraint> root) {
        this.constraint = constraint;
        this.groups = groups;
        this.root = root;
    }

    @Override
    public Query for_all(Rater rater) {
        var resultBase = constraint
                .childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> child.arguments().size() == 1)
                .filter(child -> child.arguments().get(0).getClass().equals(RaterBasedOnGrouping.class))
                .filter(child -> {
                    final var classification = (RaterBasedOnGrouping) child.arguments().get(0);
                    return classification.arguments().get(0).equals(rater);
                }).reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroups.addAll
                        (constraint
                                .lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(ForAlls.for_each(rater));
            constraint.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        }
        if (root.isEmpty()) {
            return query(resultBase.get(), resultingGroups, resultBase.get());
        } else {
            return query(resultBase.get(), resultingGroups, root.get());
        }
    }

    @Override
    public Query for_each(Attribute<?> attribute) {
        var resultBase
                = constraint.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    if (attributeClassification.arguments().size() != 1) {
                        return false;
                    }
                    return attribute.equals(attributeClassification.arguments().get(0));
                }).reduce(ensureSingle());
        final var resultingGroup = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroup.addAll(
                        constraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(ForAlls.for_each(attribute));
            constraint.withChildren(resultBase.get());
            resultingGroup.addAll(groups);
        }
        if (root.isEmpty()) {
            return query(resultBase.get(), resultingGroup, resultBase.get());
        } else {
            return query(resultBase.get(), resultingGroup, root.get());
        }
    }

    @Override
    public Query for_all() {
        final var resultBase
                = constraint.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    return attributeClassification.arguments().isEmpty();
                }).reduce(ensureSingle())
                .get();
        if (root.isEmpty()) {
            return query(resultBase, listWithValuesOf(groups), resultBase);
        } else {
            return query(resultBase, listWithValuesOf(groups), root.get());
        }
    }

    @Override
    public Query then() {
        throw not_implemented_yet();
    }

    @Override
    public Query then(Rater rater) {
        var resultBase
                = constraint.childrenView().stream()
                .filter(child -> Then.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> child.arguments().get(0).equals(rater))
                .reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroups.addAll(
                        constraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(Then.then(rater));
            constraint.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        }
        if (root.isEmpty()) {
            return query(resultBase.get(), resultingGroups, resultBase.get());
        } else {
            return query(resultBase.get(), resultingGroups, root.get());
        }
    }

    @Override
    public Query then(Rating rating) {
        return then(constantRater(rating));
    }

    @Override
    public Query for_all_combinations_of(Attribute<?>... attributes) {
        final Constraint resultBase
                = constraint.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    if (!attributeClassification.type().equals(ForAllValueCombinations.class)) {
                        return false;
                    }
                    if (attributes.length != attributeClassification.arguments().size()) {
                        return false;
                    }
                    return range(0, attributes.length)
                            .filter(index -> !attributes[index].equals(attributeClassification.arguments().get(index)))
                            .findAny()
                            .isEmpty();
                }).reduce(ensureSingle()).get();
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        for (GroupId groups : groups) {
            resultingGroups.addAll(
                    constraint.lineProcessing()
                            .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                            .lookup(groups)
                            .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                            .values());
        }
        if (root.isEmpty()) {
            return query(resultBase, resultingGroups, resultBase);
        } else {
            return query(resultBase, resultingGroups, root.get());
        }
    }

    @Override
    public Rating rating() {
        final var groupRating
                = groups.stream().map(group -> constraint.rating(group)).reduce((a, b) -> a.combine(b));
        if (groupRating.isPresent()) {
            return groupRating.get();
        }
        return metaRating();
    }

    @Override
    public Constraint constraint() {
        return constraint;
    }

    @Override
    public Optional<Constraint> root() {
        return root;
    }
}
