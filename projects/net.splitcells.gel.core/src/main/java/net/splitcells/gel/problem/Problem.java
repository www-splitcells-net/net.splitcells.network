/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;

import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.solution.Solution;

public interface Problem extends Assignments, ProblemView {
	Solution asSolution();
}
