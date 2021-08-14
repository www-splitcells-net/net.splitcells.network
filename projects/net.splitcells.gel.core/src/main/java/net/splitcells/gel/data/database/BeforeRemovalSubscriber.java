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
package net.splitcells.gel.data.database;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void register_before_removal(Line line);

	default void register_before_removal(Collection<Line> lines) {
		lines.forEach(line -> register_before_removal(line));
	}

}
