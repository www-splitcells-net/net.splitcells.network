package net.splitcells.gel.novērtējums.struktūra;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.splitcells.dem.data.set.map.Map;

public interface RefleksijaRatingSavienoties extends Novērtējums {
    <T extends Novērtējums> void reģistrētieKombinētajs(
            BiPredicate
                    <Map<Class<? extends Novērtējums>, Novērtējums>
                            , Map<Class<? extends Novērtējums>, Novērtējums>> nosacījums,
            BiFunction
                    <Map<Class<? extends Novērtējums>, Novērtējums>
                            , Map<Class<? extends Novērtējums>, Novērtējums>
                            , Map<Class<? extends Novērtējums>, Novērtējums>> kombinētajs);
}
