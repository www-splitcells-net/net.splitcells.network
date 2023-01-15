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
package net.splitcells.sep;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.Solution;

/**
 * <p>TODO Make this interface the main one to create protocols,
 * synchronizing 2 {@link Solution} in a {@link Network},
 * instead of {@link DatabaseSynchronization}.</p>
 * <p>This interface contains information about 2 {@link Solution}, that are linked to each other.</p>
 */
public interface SolutionConnection {
    /**
     * @param original
     * @param dependent
     * @param dependentLine
     * @return
     */
    List<Line> findLinesOfOriginal(Solution original, Solution dependent, Line dependentLine);
}
