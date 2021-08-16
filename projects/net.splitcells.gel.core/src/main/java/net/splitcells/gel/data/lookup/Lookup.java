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
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.column.ColumnSubscriber;

/**
 * IDEA Used/unused demand/supply should be a lookup of the primary demand/supply table.
 * @param <T> Value type being looked up.
 */
public interface Lookup<T> extends ColumnSubscriber<T>, LookupComponents<T>, Discoverable {

}
