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
 * <p>This interface contains information about 2 {@link Solution}, that are linked to each other.
 * Allocated {@link Line} from {@link #original()} are sent to {@link #dependent()}.</p>
 */
public interface SolutionConnection {

    /**
     * {@link Line} of {@link Solution#allocations()} are provided,
     * in order to create {@link Line} of {@link #dependent()}'s. {@link Solution#demands()} or {@link Solution#supplies()}.
     *
     * @return The source of {@link Line} for {@link #dependent()}
     */
    Solution original();

    /**
     * The {@link Line} of {@link Solution#supplies()} or {@link Solution#demands()} of the {@link #dependent()}
     * are derived from {@link Line} of {@link #original()}.
     *
     * @return The {@link Solution} that depends on {@link Line} from {@link #original()}.
     */
    Solution dependent();

    /**
     * For a given {@link Line} of {@link #dependent()},
     * returns the corresponding {@link Line} of {@link #original()}.
     *
     * @param dependentLine {@link Line} of {@link #dependent()}
     * @return corresponding {@link Line} of {@link #original()}
     */
    List<Line> findLinesOfOriginal(Line dependentLine);
}
