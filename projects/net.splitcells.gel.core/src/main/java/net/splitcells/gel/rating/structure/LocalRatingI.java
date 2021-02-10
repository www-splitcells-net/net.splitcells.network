package net.splitcells.gel.rating.structure;

import java.util.List;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class LocalRatingI implements LocalRating {

    private GroupId resultingConstraintGroupId;
    private Rating rating;
    private List<Constraint> propagationTo;

    public static LocalRating localRating() {
        return new LocalRatingI();
    }

    private LocalRatingI() {

    }

    @Override
    public GroupId resultingConstraintGroupId() {
        return resultingConstraintGroupId;
    }

    @Override
    public Rating rating() {
        return rating;
    }

    @Override
    public List<Constraint> propagateTo() {
        return propagationTo;
    }

    @Override
    public LocalRatingI withPropagationTo(List<Constraint> propagationTo) {
        this.propagationTo = propagationTo;
        return this;
    }

    @Override
    public LocalRatingI withRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public LocalRatingI withResultingGroupId(GroupId resultingConstraintGroupId) {
        this.resultingConstraintGroupId = resultingConstraintGroupId;
        return this;
    }

}
