package net.splitcells.gel.rating.structure;

import static net.splitcells.gel.rating.type.Cost.noCost;

import net.splitcells.dem.data.set.map.Map;

public interface MetaRating extends Rating, RatingTranslator, MetaRatingMerger {
    static MetaRating neitrāla() {
        final MetaRating neitrāla = MetaRatingI.rflektētsNovērtējums();
        neitrāla.kombinē(noCost());
        return neitrāla;
    }

    Map<Class<? extends Rating>, Rating> saturs();

    @SuppressWarnings("unchecked")
    default <T> T gūtSaturuDaļa(Class<? extends T> tips) {
        return (T) saturs().get(tips);
    }

    @Override
    default MetaRating kāReflektētsNovērtējums() {
        return this;
    }
}
