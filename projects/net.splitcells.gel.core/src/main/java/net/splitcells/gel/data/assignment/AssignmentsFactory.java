/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;


import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.table.Table;

public interface AssignmentsFactory extends Resource, AspectOrientedConstructor<Assignments>, ConnectingConstructor<Assignments> {
    Assignments assignments(String name, Table demands, Table supply);
}
