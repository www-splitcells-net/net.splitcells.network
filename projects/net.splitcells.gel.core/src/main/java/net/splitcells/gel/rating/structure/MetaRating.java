package net.splitcells.gel.rating.structure;

import static net.splitcells.gel.rating.structure.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.type.Cost.noCost;

import net.splitcells.dem.data.set.map.Map;

public interface MetaRating extends Rating, RatingTranslator, MetaRatingMerger {
    static MetaRating neutral() {
        final MetaRating neutral = metaRating();
        neutral.combine(noCost());
        return neutral;
    }

    Map<Class<? extends Rating>, Rating> content();

    @SuppressWarnings("unchecked")
    default <T> T getContentValue(Class<? extends T> tips) {
        return (T) content().get(tips);
    }

    @Override
    default MetaRating asMetaRating() {
        return this;
    }
}
