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
package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import org.assertj.core.api.Assertions;

/**
 * This event describes how {@link Line}s should be updated in a {@link Constraint} node.
 */
public interface RatingEvent {

    Map<Line, LocalRating> additions();

    Map<Line, List<LocalRating>> complexAdditions();

    default List<LocalRating> allAdditions() {
        final List<LocalRating> allAdditions = listWithValuesOf(additions().values());
        complexAdditions().values().forEach(ca -> ca.forEach(allAdditions::add));
        return allAdditions;
    }

    @ReturnsThis
    default RatingEvent extendComplexRating(Line line, LocalRating rating) {
        final List<LocalRating> localRatings;
        if (complexAdditions().containsKey(line)) {
            localRatings = complexAdditions().get(line);
        } else {
            localRatings = list();
            complexAdditions().put(line, localRatings);
        }
        localRatings.add(rating);
        return this;
    }

    Set<Line> removal();

    default void addRating_viaAddition(Line subject
            , Rating additionalRating
            , List<Constraint> children
            , Optional<Rating> ratingBeforeAddition) {
        final Rating currentRating;
        if (additions().containsKey(subject)) {
            currentRating = additions().get(subject).rating();
        } else {
            currentRating = ratingBeforeAddition.orElse(noCost());
        }
        additions().put
                (subject
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(currentRating.combine(additionalRating))
                                .withResultingGroupId(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void updateRating_viaAddition(Line subject, Rating additionalRating, List<Constraint> children,
                                          Optional<Rating> ratingBeforeAddition) {
        final Rating currentRating;
        if (additions().containsKey(subject)) {
            currentRating = additions().get(subject).rating();
        } else {
            currentRating = ratingBeforeAddition.orElse(noCost());
        }
        updateRating_withReplacement(subject
                , localRating()
                        .withPropagationTo(children)
                        .withRating(currentRating.combine(additionalRating))
                        .withResultingGroupId(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
    }

    default void updateRating_withReplacement(Line subject, LocalRating newRating) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(additions().keySet()).doesNotContain(subject);
            assertThat(removal()).doesNotContain(subject);
            {
                Assertions.assertThat(subject.value(Constraint.LINE)).isNotNull();
                Assertions.assertThat(subject.value(Constraint.INCOMING_CONSTRAINT_GROUP)).isNotNull();
            }
        }
        removal().add(subject);
        additions().put(subject, newRating);
    }
}
