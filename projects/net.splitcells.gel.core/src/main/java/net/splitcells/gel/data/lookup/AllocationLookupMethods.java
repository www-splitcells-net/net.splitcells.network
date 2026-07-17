/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import java.util.function.Predicate;

import net.splitcells.gel.data.assignment.Assignments;

public interface AllocationLookupMethods<T> extends LookupMethods<T> {
	Assignments persistedLookup(T value);

	Assignments persistedLookup(Predicate<T> predicate);
}
