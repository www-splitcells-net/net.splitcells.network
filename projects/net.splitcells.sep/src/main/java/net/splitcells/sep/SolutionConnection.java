/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
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
