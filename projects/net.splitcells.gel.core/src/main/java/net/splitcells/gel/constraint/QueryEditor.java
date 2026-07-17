/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint;

import net.splitcells.dem.data.set.Set;

/**
 * This is a write interface to the {@link Query} interface,
 * that enables one to extend the {@link Query} functionality dynamically.
 */
public interface QueryEditor extends Query {

    QueryEditor nextQueryPathElement(Set<GroupId> nextGroups, Constraint nextConstraint);
}
