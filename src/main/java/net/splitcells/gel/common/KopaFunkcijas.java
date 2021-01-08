package net.splitcells.gel.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class KopaFunkcijas {

	private KopaFunkcijas() {
		throw constructorIllegal();
	}

	public static <T, R extends T> R vetība(Map<Class<? extends T>, T> map, Class<R> key) {
		return (R) map.get(key);
	}

	@Deprecated
	public static <E> Collection<E> summa(Collection<Set<E>> arg) {
		final var argIter = arg.iterator();
		final var summa = new HashSet<>(argIter.next());
		while (argIter.hasNext()) {
			summa.addAll(argIter.next());
		}
		return summa;
	}

	@Deprecated
	public static <E> Set<E> pārklājas(Set<E>... arg) {
		return pārklājas(Arrays.asList(arg));
	}

	@Deprecated
	public static <E> Set<E> pārklājas(Collection<Set<E>> arg) {
		if (arg.size() == 0) {
			return new HashSet<>();
		} else if (arg.size() == 1) {
			return arg.iterator().next();
		}
		final var argIter = arg.iterator();
		Set<E> rVal = new HashSet<>(argIter.next());
		Set<E> tmpIntersection = new HashSet<>();
		Set<E> argCurrent;
		while (argIter.hasNext()) {
			argCurrent = argIter.next();
			for (E ele : argCurrent) {
				if (rVal.contains(ele)) {
					tmpIntersection.add(ele);
				}
			}
			if (tmpIntersection.isEmpty()) {
				return tmpIntersection;
			}
			rVal = tmpIntersection;
		}
		return rVal;
	}

	@Deprecated
	public static <E> Set<E> complemention(Set<E> all, Set<E> exceptThis) {
		if (exceptThis.isEmpty()) {
			return all;
		}
		Set<E> rVal = new HashSet<>();
		for (E ele : all) {
			if (exceptThis.contains(ele)) {

			} else {
				rVal.add(ele);
			}
		}
		return rVal;
	}

}
