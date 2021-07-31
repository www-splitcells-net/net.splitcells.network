/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.framework.Rating;
import org.assertj.core.api.Assertions;

public interface RatingEvent {

    Map<Line, LocalRating> additions();

    Set<Line> removal();

    default void addRating_viaAddition(Line subject, Rating additionalRating, List<Constraint> children,
                                       Optional<Rating> ratingBeforeAddition) {
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
