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
package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

/**
 * <p>Provides a view to a subset of a {@link View} as a {@link View}.
 * By default, the {@link PersistedLookupView} is empty.
 * Every {@link Line} has to be manually added via {@link #register(Line)}
 * or manually removed  via {@link #removeRegistration(Line)}.
 * See {@link Lookup} for automated synchronization.</p>
 */
public interface PersistedLookupView extends View {

    static LookupTableFactory lookupTableFactory() {
        return PersistedLookupViewI.lookupTableFactory();
    }

    void register(Line line);

    void removeRegistration(Line line);

    View base();
}
