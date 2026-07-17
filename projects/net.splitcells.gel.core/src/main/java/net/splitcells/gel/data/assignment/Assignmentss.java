/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.gel.data.table.Table;

import static net.splitcells.dem.Dem.environment;

public class Assignmentss extends ResourceOptionImpl<AssignmentsFactory> {
    public Assignmentss() {
        super(() -> new AssignmentsIFactory());
    }

    public static Assignments assignments(String name, Table demands, Table supplies) {
        return environment().config().configValue(Assignmentss.class).assignments(name, demands, supplies);
    }
}
