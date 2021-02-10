package net.splitcells.gel.rating.structure;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.splitcells.dem.data.set.map.Map;

public interface MetaRatingMerger extends Rating {
    <T extends Rating> void registerMerger(
            BiPredicate
                    <Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>> condition,
            BiFunction
                    <Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>> combiner);
}
