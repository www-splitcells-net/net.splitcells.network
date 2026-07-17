/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution;

import net.splitcells.gel.data.assignment.Assignments;

public interface SolutionHistory extends Assignments {

	Solution subject();
}
