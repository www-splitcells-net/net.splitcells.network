package net.splitcells.gel.kodols.novērtējums.struktūra;

import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;

public interface RatingTulks {
	<T extends Novērtējums> T tulkošana(Class<T> type);

	void reģistrēTulks(Class<? extends Novērtējums> target, Predicate<Map<Class<? extends Novērtējums>, Novērtējums>> condition,
					   Function<Map<Class<? extends Novērtējums>, Novērtējums>, Novērtējums> translator);

}
