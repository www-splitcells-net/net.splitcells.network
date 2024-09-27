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
package net.splitcells.gel.constraint;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.environment.config.StaticFlags.WARNING;
import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.rating.framework.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.FOR_ALL_VALUE_COMBINATIONS_NAME;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.forAllValueCombinations;

import java.util.Optional;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;

public class QueryI implements Query, QueryEditor {
    public static Query query(Constraint constraint, Optional<Constraint> root) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), root);
    }

    public static Query query(Constraint constraint) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), Optional.of(constraint));
    }

    /**
     * @param constraint
     * @param isBuilderArg If true, then this builder will not add new {@link Constraint#childrenView()}.
     *                     In this case, the {@link Query} is a read only selector for {@link Constraint}
     *                     based on a fluent API.
     * @return
     */
    public static Query query(Constraint constraint, boolean isBuilderArg) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), Optional.of(constraint), isBuilderArg);
    }

    public static Query query(Constraint constraint, Assignments subject) {
        return new QueryI(constraint, setOfUniques(constraint.injectionGroup()), Optional.of(constraint), subject);
    }

    public static Query query(Constraint constraint, Set<GroupId> groups, Constraint root) {
        return new QueryI(constraint, groups, Optional.of(root));
    }

    public QueryEditor nextQueryPathElement(Set<GroupId> nextGroups, Constraint nextConstraint) {
        if (subject().isPresent()) {
            return new QueryI(nextConstraint
                    , nextGroups
                    , root()
                    , constraintPath().shallowCopy().withAppended(nextConstraint)
                    , subject().orElseThrow());
        }
        return new QueryI(nextConstraint
                , nextGroups
                , root()
                , constraintPath().shallowCopy().withAppended(nextConstraint));
    }

    /**
     * <p>The starting point of the {@link Query}.</p>
     * <p>TODO Why is this optional?</p>
     */
    private final Optional<Constraint> root;
    /**
     * This is the {@link Constraint}, that is reached by the current {@link Query} state.
     */
    private final Constraint currentConstraint;

    /**
     * These are the {@link Constraint}s traversed, in order to reach {@link #currentConstraint}.
     */
    private final List<Constraint> constraintPath;
    private final Set<GroupId> groups;
    private final Optional<Assignments> subject;
    /**
     * If true, then this builder will not add new {@link Constraint#childrenView()}.
     * In this case, the {@link Query} is a read only selector for {@link Constraint}
     * based on a fluent API.
     */
    private final boolean isBuilder;

    private QueryI(Constraint currentInjectionGroup, Set<GroupId> groups, Optional<Constraint> root) {
        this(currentInjectionGroup, groups, root, true);
    }

    private QueryI(Constraint currentInjectionGroup, Set<GroupId> groups, Optional<Constraint> root, boolean isBuilderArg) {
        this.currentConstraint = currentInjectionGroup;
        this.groups = groups;
        this.root = root;
        constraintPath = list();
        if (root.isPresent()) {
            constraintPath.add(root.get());
        }
        subject = Optional.empty();
        isBuilder = isBuilderArg;
    }

    private QueryI(Constraint currentInjectionGroup, Set<GroupId> groups, Optional<Constraint> root, Assignments subject) {
        this.currentConstraint = currentInjectionGroup;
        this.groups = groups;
        this.root = root;
        constraintPath = list();
        if (root.isPresent()) {
            constraintPath.add(root.get());
        }
        this.subject = Optional.of(subject);
        isBuilder = true;
    }

    private QueryI(Constraint currentInjectionGroup, Set<GroupId> groups, Optional<Constraint> root, List<Constraint> constraintPath) {
        this.currentConstraint = currentInjectionGroup;
        this.groups = groups;
        this.root = root;
        this.constraintPath = constraintPath;
        subject = Optional.empty();
        isBuilder = true;
    }

    private QueryI(Constraint currentInjectionGroup, Set<GroupId> groups, Optional<Constraint> root, List<Constraint> constraintPath, Assignments subject) {
        this.currentConstraint = currentInjectionGroup;
        this.groups = groups;
        this.root = root;
        this.constraintPath = constraintPath;
        this.subject = Optional.of(subject);
        isBuilder = true;
    }

    @Override
    public Query forAll(Rater classifier) {
        var resultBase = currentConstraint
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
                        (currentConstraint
                                .lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else if (isBuilder) {
            resultBase = Optional.of(ForAlls.forEach(classifier
                    , Optional.of(discoverable(currentConstraint.path()))));
            currentConstraint.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        } else {
            throw executionException(perspective("Could not find forAll child constraint with given classifier.")
                    .withProperty("classifier", classifier.toTree())
                    .withProperty("current constraint", currentConstraint.toTree()));
        }
        return nextQueryPathElement(resultingGroups, resultBase.get());
    }

    @Override
    public Optional<Assignments> subject() {
        return subject;
    }

    @Override
    public Query forAll(Attribute<?> attribute) {
        var resultBase
                = currentConstraint.childrenView().stream()
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
                        currentConstraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else if (isBuilder) {
            resultBase = Optional.of(ForAlls.forEach(attribute
                    , Optional.of(discoverable(currentConstraint.path()))));
            currentConstraint.withChildren(resultBase.get());
            resultingGroup.addAll(groups);
        } else {
            throw executionException(perspective("Could not find forAll child constraint with given attribute.")
                    .withProperty("classifier", attribute.toTree())
                    .withProperty("current constraint", currentConstraint.toTree()));
        }
        return nextQueryPathElement(resultingGroup, resultBase.get());
    }

    /**
     * TODO TOFIX This does not work if {@link #root} is empty.
     *
     * @return The Successive State
     */
    @Override
    public Query forAll() {
        final var resultBase
                = currentConstraint.childrenView().stream()
                .filter(child -> ForAll.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> {
                    final var classification = (Rater) child.arguments().get(0);
                    final var attributeClassification = (Rater) classification.arguments().get(0);
                    return attributeClassification.arguments().isEmpty();
                }).reduce(ensureSingle());
        if (resultBase.isPresent()) {
            return nextQueryPathElement(setOfUniques(groups), resultBase.get());
        }
        if (!isBuilder) {
            throw executionException(perspective("Could not find forAll child constraint.")
                    .withProperty("current constraint", currentConstraint.toTree()));
        }
        final var forAll = ForAlls.forAll(Optional.of(discoverable(currentConstraint.path())));
        currentConstraint.withChildren(forAll);
        return nextQueryPathElement(setOfUniques(groups), forAll);
    }

    @Override
    public Query then(Rater rater) {
        var resultBase
                = currentConstraint.childrenView().stream()
                .filter(child -> Then.class.equals(child.type()))
                .filter(child -> !child.arguments().isEmpty())
                .filter(child -> child.arguments().get(0).equals(rater))
                .reduce(ensureSingle());
        final var resultingGroups = Sets.<GroupId>setOfUniques();
        if (resultBase.isPresent()) {
            for (GroupId group : groups) {
                resultingGroups.addAll(
                        currentConstraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(group)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else if (isBuilder) {
            resultBase = Optional.of(Then.then(rater, Optional.of(discoverable(currentConstraint.path()))));
            currentConstraint.withChildren(resultBase.get());
            resultingGroups.addAll(groups);
        } else {
            throw executionException(perspective("Could not find then child constraint with given rater.")
                    .withProperty("classifier", rater.toTree())
                    .withProperty("current constraint", currentConstraint.toTree()));
        }
        return nextQueryPathElement(resultingGroups, resultBase.get());
    }

    @Override
    public Query then(Rating rating) {
        return then(constantRater(rating));
    }

    @Override
    public Query forAllCombinationsOf(List<Attribute<? extends Object>> attributes) {
        var resultBase
                = currentConstraint.childrenView().stream()
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
                        currentConstraint.lineProcessing()
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(groups)
                                .columnView(Constraint.RESULTING_CONSTRAINT_GROUP)
                                .values());
            }
        } else if (isBuilder) {
            resultBase = Optional.of(ForAlls.forEach(forAllValueCombinations(attributes)
                    , Optional.of(discoverable(currentConstraint.path()))));
            currentConstraint.withChildren(resultBase.get());
            root.ifPresent(Constraint::recalculateProcessing);
            resultingGroups.addAll(groups);
        } else {
            throw executionException(perspective("Could not find given forAllCombinationsOf of attributes child constraint.")
                    .withProperty("classifier", attributes.toString())
                    .withProperty("current constraint", currentConstraint.toTree()));
        }
        return nextQueryPathElement(resultingGroups, resultBase.get());
    }

    @Override
    public Rating rating() {
        final var groupRating
                = groups.stream().map(group -> currentConstraint.rating(group)).reduce((a, b) -> a.combine(b));
        if (groupRating.isPresent()) {
            return groupRating.get();
        }
        return metaRating();
    }

    @Override
    public Constraint currentConstraint() {
        return currentConstraint;
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

    @Override
    public Query forAll(List<Rater> classifiers) {
        if (WARNING) logs().append("Groups are not supported yet: " + groups.toString(), LogLevel.WARNING);
        final var forAllCatcher = ForAlls.forAll(Optional.of(discoverable(currentConstraint.path()
                .withAppended("" + currentConstraint.childrenView().size()))));
        classifiers.forEach(c -> {
            final var f = ForAlls.forEach(c, Optional.of(discoverable(currentConstraint.path()
                    .withAppended("" + currentConstraint.childrenView().size()))));
            f.withChildren(forAllCatcher);
            if (!isBuilder) {
                throw executionException(perspective("Could not find forAll child constraint with given classifiers.")
                        .withProperty("classifier", classifiers.toString())
                        .withProperty("current constraint", currentConstraint.toTree()));
            }
            currentConstraint.withChildren(f);
        });
        return nextQueryPathElement(setOfUniques(), forAllCatcher);
    }

    @Override
    public Query parseConstraint(String constraintType, List<Rater> raters, List<Attribute<? extends Object>> attributes) {
        final var constraint = constraintResult(constraintType, raters, attributes);
        if (constraint.errorMessages().hasElements()) {
            throw executionException(perspective("Could not construct constraints.").withChildren(constraint.errorMessages()));
        }
        return constraint.value().orElseThrow();
    }

    @Override
    public Result<Query, Tree> constraintResult(String constraintType, List<Rater> raters, List<Attribute<? extends Object>> attributes) {
        final Result<Query, Tree> constraint = result();
        if (constraintType.equals(FOR_ALL_VALUE_COMBINATIONS_NAME)) {
            if (raters.hasElements()) {
                return constraint.withErrorMessage(perspective("No raters are allowed for parsing of `" + FOR_ALL_VALUE_COMBINATIONS_NAME + "` constraint.")
                        .withProperty("constraint type", constraintType)
                        .withProperty("raters", raters.toString())
                        .withProperty("attributes", attributes.toString()));
            }
            return constraint.withValue(forAllCombinationsOf(attributes));
        } else if (constraintType.equals(FOR_ALL_NAME)) {
            if (attributes.size() == 1 && raters.isEmpty()) {
                return constraint.withValue(forAll(attributes.get(0)));
            }
            if (attributes.hasElements()) {
                return constraint.withErrorMessage(perspective("No attributes are allowed for parsing of `" + FOR_ALL_NAME + "` constraint.")
                        .withProperty("constraint type", constraintType)
                        .withProperty("raters", raters.toString())
                        .withProperty("attributes", attributes.toString()));
            }
            return constraint.withValue(forAll(raters));
        } else if (constraintType.equals(THEN_NAME)) {
            if (raters.size() != 1) {
                return constraint.withErrorMessage(perspective("Invalid number of raters given for parsing a `" + THEN_NAME + "` constraint. A `" + THEN_NAME + "` constraint requires exactly one rater.")
                        .withProperty("constraint type", constraintType)
                        .withProperty("raters", raters.toString())
                        .withProperty("attributes", attributes.toString()));
            }
            if (attributes.hasElements()) {
                return constraint.withErrorMessage(perspective("No attributes are not allowed for parsing of `" + THEN_NAME + "` constraint.")
                        .withProperty("constraint type", constraintType)
                        .withProperty("raters", raters.toString())
                        .withProperty("attributes", attributes.toString()));
            }
            return constraint.withValue(then(raters.get(0)));
        } else {
            return constraint.withErrorMessage(perspective("Unknown constraint type given for constraint parsing.")
                    .withProperty("constraint type", constraintType)
                    .withProperty("raters", raters.toString())
                    .withProperty("attributes", attributes.toString()));
        }
    }
}
