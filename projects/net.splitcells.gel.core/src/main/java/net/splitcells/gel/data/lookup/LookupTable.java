/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

/**
 * <p>Provides a view to a subset of a {@link View} as a {@link View}.
 * By default, the {@link LookupTable} is empty.
 * Every {@link Line} has to be manually added via {@link #register(Line)}
 * or manually removed  via {@link #removeRegistration(Line)}.
 * See {@link LookupColumn} for automated synchronization.</p>
 */
public interface LookupTable extends View {

    static LookupTableFactory lookupTableFactory() {
        return LookupTableImpl.lookupTableFactory();
    }

    void register(Line line);

    void removeRegistration(Line line);

    View base();

    SetT<Integer> contentIndexes();
}
