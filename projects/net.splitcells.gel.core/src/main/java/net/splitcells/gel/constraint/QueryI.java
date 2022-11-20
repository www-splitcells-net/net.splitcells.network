/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.constraint;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.rating.framework.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.rater.classification.ForAllValueCombinations.forAllValueCombinations;

import java.util.Optional;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;

public class QueryI implements Query {
    public static Query query(Constraint constraint, Optional<Constraint> root) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), root);
    }

    public static Query query(Constraint constraint) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), Optional.of(constraint));
    }

    public static Query query(Constraint constraint, Set<GroupId> groups, Constraint root) {
        return new QueryI(constraint, groups, Optional.of(root));
    }

    public static Query nextQueryPathElement(Query query, Set<GroupId> nextGroups, Constraint nextConstraint) {
        return new QueryI(nextConstraint
                , nextGroups
                , query.root()
                , query.constraintPath().shallowCopy().withAppended(nextConstraint));
    }

    /**
     * <p>The starting point of the {@link Query}.</p>
     * <p>TODO Why is this optional?</p>
     */
    private final Optional<Constraint> root;
    /**
     * This is the {@link Constraint}, that is reached by the current {@link Query} state.
     */
    private final Constraint currentInjectionGroups;

    /**
     * These are the {@link Constraint}s traversed, in order to reach {@link #currentInjectionGroups}.
     */
    private final List<Constraint> constraintPath;
    private final Set<GroupId> groups;

    private QueryI(Constraint currentInjectionGroups, Set<GroupId> groups, Optional<Constraint> root) {
        this.currentInjectionGroups = currentInjectionGroups;
        this.groups = groups;
        this.root = root;
        constraintPath = list();
        if (root.isPresent()) {
            constraintPath.add(root.get());
        }
    }

    private QueryI(Constraint currentInjectionGroups, Set<GroupId> groups, Optional<Constraint> root, List<Constraint> constraintPath) {
        this.currentInjectionGroups = currentInjectionGroups;
        this.groups = groups;
        this.root = root;
        this.constraintPath = constraintPath;
    }

    @Override
    public Query forAll(Rater classifier) {
        var resultBase = currentInjectionGroups
                .childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> child.arguments().size() == 1)
                .filter(child -> child.arguments().get(0).getClass().equals(RaterBasedOnGrouping.class))
                .filter(child -> {
                    final var classification = (RaterBasedOnGrouping) child.arguments().get(0);
                    return classification.arguments().get(0).equals(classifier);
                }).reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroups.addAll
                        (currentInjectionGroups
                                .lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(ForAlls.forEach(classifier
                    , Optional.of(discoverable(root.map(c -> c.path()).orElseThrow()))));
            currentInjectionGroups.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        }
        return nextQueryPathElement(this, resultingGroups, resultBase.get());
    }

    @Override
    public Query forAll(Attribute<?> attribute) {
        var resultBase
                = currentInjectionGroups.childrenView().stream()
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
                        currentInjectionGroups.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(ForAlls.forEach(attribute));
            currentInjectionGroups.withChildren(resultBase.get());
            resultingGroup.addAll(groups);
        }
        return nextQueryPathElement(this, resultingGroup, resultBase.get());
    }

    /**
     * TODO TOFIX This does not work if {@link #root} is empty.
     *
     * @return The Successive State
     */
    @Override
    public Query forAll() {
        final var resultBase
                = currentInjectionGroups.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    return attributeClassification.arguments().isEmpty();
                }).reduce(ensureSingle())
                .get();
        return nextQueryPathElement(this, setOfUniques(groups), resultBase);
    }

    @Override
    public Query then(Rater rater) {
        var resultBase
                = currentInjectionGroups.childrenView().stream()
                .filter(child -> Then.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> child.arguments().get(0).equals(rater))
                .reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroups.addAll(
                        currentInjectionGroups.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(Then.then(rater));
            currentInjectionGroups.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        }
        return nextQueryPathElement(this, resultingGroups, resultBase.get());
    }

    @Override
    public Query then(Rating rating) {
        return then(constantRater(rating));
    }

    @Override
    public Query forAllCombinationsOf(List<Attribute<? extends Object>> attributes) {
        var resultBase
                = currentInjectionGroups.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    if (!attributeClassification.type().equals(ForAllValueCombinations.class)) {
                        return false;
                    }
                    if (attributes.size() != attributeClassification.arguments().size()) {
                        return false;
                    }
                    return range(0, attributes.size())
                            .filter(index -> !attributes.get(index).equals(attributeClassification.arguments().get(index)))
                            .findAny()
                            .isEmpty();
                }).reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId groups : groups) {
                resultingGroups.addAll(
                        currentInjectionGroups.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(groups)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else {
            resultBase = Optional.of(ForAlls.forEach(forAllValueCombinations(attributes)));
            currentInjectionGroups.withChildren(resultBase.get());
            root.ifPresent(Constraint::recalculateProcessing);
            resultingGroups.addAll(groups);
        }
        return nextQueryPathElement(this, resultingGroups, resultBase.get());
    }

    @Override
    public Rating rating() {
        final var groupRating
                = groups.stream().map(group -> currentInjectionGroups.rating(group)).reduce((a, b) -> a.combine(b));
        if (groupRating.isPresent()) {
            return groupRating.get();
        }
        return metaRating();
    }

    @Override
    public Constraint currentConstraint() {
        return currentInjectionGroups;
    }

    @Override
    public Optional<Constraint> root() {
        return root;
    }

    @Override
    public List<Constraint> constraintPath() {
        return constraintPath;
    }

    @Override
    public Set<GroupId> currentInjectionGroups() {
        return groups;
    }
}
