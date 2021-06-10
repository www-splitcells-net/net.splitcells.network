package net.splitcells.gel.rating.framework;

import java.util.List;

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
