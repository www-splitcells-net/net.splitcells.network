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
package net.splitcells.gel.rating.rater.framework;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.List;
import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.framework.LocalRating;

public class RatingEventI implements RatingEvent {

	private final Map<Line, LocalRating> additions = map();

	private final Map<Line, List<LocalRating>> complexAdditions = map();
	private final Set<Line> removal = setOfUniques();

	public static RatingEvent ratingEvent() {
		return new RatingEventI();
	}

	private RatingEventI() {

	}

	@Override
	public Map<Line, LocalRating> additions() {
		return additions;
	}

	@Override
	public Map<Line, List<LocalRating>> complexAdditions() {
		return complexAdditions;
	}

	@Override
	public Set<Line> removal() {
		return removal;
	}

}
