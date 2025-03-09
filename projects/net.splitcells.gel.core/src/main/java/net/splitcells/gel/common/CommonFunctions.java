/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.common;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * TODO Move this to the appropriate collection interfaces.
 */
@JavaLegacyArtifact
@Deprecated
public class CommonFunctions {

	private CommonFunctions() {
		throw constructorIllegal();
	}

	public static <T, R extends T> R value(Map<Class<? extends T>, T> map, Class<R> key) {
		return (R) map.get(key);
	}

	@Deprecated
	public static <E> Collection<E> sum(Collection<Set<E>> arg) {
		final var argIter = arg.iterator();
		final var sum = new HashSet<>(argIter.next());
		while (argIter.hasNext()) {
			sum.addAll(argIter.next());
		}
		return sum;
	}

	@Deprecated
	public static <E> Set<E> intersection(Set<E>... arg) {
		return intersection(Arrays.asList(arg));
	}

	@Deprecated
	public static <E> Set<E> intersection(Collection<Set<E>> arg) {
		if (arg.isEmpty()) {
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
			if (!exceptThis.contains(ele)) {
				rVal.add(ele);
			}
		}
		return rVal;
	}

}
