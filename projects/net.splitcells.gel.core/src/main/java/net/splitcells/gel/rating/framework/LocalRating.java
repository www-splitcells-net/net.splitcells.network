/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public interface LocalRating {

	GroupId resultingConstraintGroupId();

	Rating rating();

	List<Constraint> propagateTo();

	LocalRatingI withPropagationTo(List<Constraint> propagationTo);

	LocalRatingI withRating(Rating rating);

	LocalRatingI withResultingGroupId(GroupId resultingGroupId);
}
