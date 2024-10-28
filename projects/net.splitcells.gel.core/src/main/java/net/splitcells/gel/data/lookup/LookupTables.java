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

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.gel.data.lookup.LookupView.lookupTableFactory;

public class LookupTables implements Option<LookupTableFactory> {
    @Override
    public LookupTableFactory defaultValue() {
        return lookupTableFactory();
    }

    public static LookupView lookupTable(View view, String name) {
        return configValue(LookupTables.class).lookupTable(view, name);
    }

    public static LookupView lookupTable(View view, Attribute<?> attribute) {
        return configValue(LookupTables.class).lookupTable(view, attribute);
    }

    public static LookupView lookupTable(View view, Attribute<?> attribute, boolean cacheRawLines) {
        return configValue(LookupTables.class).lookupTable(view, attribute, cacheRawLines);
    }
}
