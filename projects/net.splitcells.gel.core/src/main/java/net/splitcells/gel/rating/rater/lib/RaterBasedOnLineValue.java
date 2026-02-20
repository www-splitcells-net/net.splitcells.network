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
package net.splitcells.gel.rating.rater.lib;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Lambdas.describedFunction;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.framework.Rating;

/**
 * <p>This {@link Rater} makes it easy to rate every {@link Line} of a group independent of each other.</p>
 * <p>TODO Clean up, simplify and make more portable the API of the static factory functions.</p>
 */
public class RaterBasedOnLineValue implements Rater {
    public static Rater raterBasedOnLineValue(String description, Function<Line, Integer> classifier) {
        return raterBasedOnLineValue(new Function<>() {
            private final Map<Integer, GroupId> lineNumbering = map();

            @Override
            public GroupId apply(Line arg) {
                return lineNumbering.computeIfAbsent
                        (classifier.apply(arg.value(LINE))
                                , classification -> GroupId.group(arg.value(INCOMING_CONSTRAINT_GROUP)
                                        , description + ": " + classification));
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }

    public static Rater lineValueRater(Predicate<Line> classifier) {
        return lineValueRater(classifier, line -> {
            if (classifier.test(line)) {
                return noCost();
            } else {
                return cost(1);
            }
        }, "");
    }

    /**
     * @param classifier
     * @param description
     * @return
     * @deprecated {@link #lineValueRater(Predicate, String, String)}
     */
    @Deprecated
    public static Rater lineValueRater(Predicate<Line> classifier, String description) {
        return lineValueRater(classifier, description, classifier.toString());
    }

    public static Rater lineValueRater(Predicate<Line> classifier, String description, String argDescriptivePathName) {
        return lineValueRater(new Predicate<Line>() {
            @Override public boolean test(Line line) {
                return classifier.test(line);
            }

            @Override public String toString() {
                return description;
            }
        }, new Function<Line, Rating>() {
            @Override public Rating apply(Line line) {
                {
                    if (classifier.test(line)) {
                        return noCost();
                    } else {
                        return cost(1);
                    }
                }
            }

            @Override public String toString() {
                return description;
            }
        }, argDescriptivePathName);
    }

    public static Rater lineValueRater(Predicate<Line> classifier, Function<Line, Rating> rater, String argDescriptivePathName) {
        return new RaterBasedOnLineValue(rater
                , describedFunction
                (addition -> addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)
                        , classifier.toString())
                , (addition, children) -> {
            if (classifier.test(addition.value(LINE))) {
                return children;
            } else {
                return list();
            }
        }, argDescriptivePathName);
    }

    public static Rater lineValueSelector(Predicate<Line> classifier) {
        return lineValueRater(classifier, line -> noCost(), "");
    }

    public static Rater lineValueBasedOnRater(Function<Line, Rating> raterBasedOnLineValue) {
        return new RaterBasedOnLineValue(raterBasedOnLineValue
                , addition -> addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)
                , (addition, children) -> children
                , "");
    }

    public static Rater raterBasedOnLineValue(Function<Line, GroupId> classifierBasedOnLineValue) {
        return new RaterBasedOnLineValue(additionalLine -> noCost(), classifierBasedOnLineValue
                , (addition, children) -> children
                , "");
    }

    private final Function<Line, Rating> rater;
    private final Function<Line, GroupId> classifier;
    private final BiFunction<Line, List<Constraint>, List<Constraint>> propagation;
    private final List<Discoverable> contexts = list();
    private final String descriptivePathName;

    private RaterBasedOnLineValue(Function<Line, Rating> rater
            , Function<Line, GroupId> classifierBasedOnLineValue
            , BiFunction<Line, List<Constraint>, List<Constraint>> propagationBasedOnLineValue
            , String argDescriptivePathName) {
        this.rater = rater;
        this.classifier = classifierBasedOnLineValue;
        this.propagation = propagationBasedOnLineValue;
        descriptivePathName = argDescriptivePathName;
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children
            , View ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        rating.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(propagation.apply(addition, children))
                                .withResultingGroupId(classifier.apply(addition))
                                .withRating(rater.apply(addition.value(LINE))));
        return rating;
    }

    @Override
    public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children
            , View ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnLineValue.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(tree(getClass().getSimpleName())
                .withChild(tree(classifier.toString()
                        + " "
                        + rater.toString())));
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Set<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    @Override
    public Tree toTree() {
        return tree(rater.toString());
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return classifier.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + rater + ", " + classifier;
    }

    @Override public String descriptivePathName() {
        return descriptivePathName;
    }
}
