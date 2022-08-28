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
package net.splitcells.gel.rating.framework;

import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;

public interface RatingTranslator {
	<T extends Rating> T translate(Class<T> type);

	void registerTranslator(Class<? extends Rating> target, Predicate<Map<Class<? extends Rating>, Rating>> condition,
							Function<Map<Class<? extends Rating>, Rating>, Rating> translator);

}