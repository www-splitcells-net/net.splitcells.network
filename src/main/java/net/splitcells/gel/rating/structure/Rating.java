package net.splitcells.gel.rating.structure;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;

public interface Rating extends PartiallyOrdered<Rating>, Domable {

    @Returns_this
    <R extends Rating> R combine(Rating... additionalNovērtējums);

    default MetaRating asMetaRating() {
        return MetaRatingI.metaRating().combine(this);
    }

    <R extends Rating> R _clone();

    default boolean betterThan(Rating novērtējums) {
        return smallerThan(novērtējums);
    }
}
