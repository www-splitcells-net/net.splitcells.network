/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.SetWA;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.annotations.ReturnsThis;

/**
 * TODOC What is the difference between append and add? -> add set specific and
 * the method does not guarrenties that is added to the of the list. Append adds
 * to end.
 */
public interface ListWA<T> extends SetWA<T> {
	@ReturnsThis
	<R extends ListWA<T>> R append(T arg);

	@Override
	default <R extends SetWA<T>> R add(T arg) {
		return append(arg);
	}

	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends ListWA<T>> R appendAll(T... arg) {
		for (int i = 0; i < arg.length; ++i) {
			append(arg[i]);
		}
		return (R) this;
	}

	@JavaLegacyBody
	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends ListWA<T>> R appendAll(java.util.Collection<T> arg) {
		arg.forEach(e -> this.append(e));
		return (R) this;
	}

	@JavaLegacyBody
	@Override
	default <R extends SetWA<T>> R addAll(java.util.Collection<T> value) {
		return appendAll(value);
	}
}
