/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discovery;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.data.lookup.LookupTable.lookupTableFactory;

public class LookupTables implements Option<LookupTableFactory> {
    @Override
    public LookupTableFactory defaultValue() {
        return lookupTableFactory();
    }

    public static LookupTable lookupTable(View view, String name) {
        return configValue(LookupTables.class).lookupTable(view, name);
    }

    public static LookupTable lookupTable(View view, Attribute<?> attribute) {
        return configValue(LookupTables.class).lookupTable(view, attribute);
    }

    public static LookupTable lookupTable(View view, Attribute<?> attribute, boolean cacheRawLines) {
        return configValue(LookupTables.class).lookupTable(view, attribute, cacheRawLines);
    }

    @Override public Optional<Tree> serialize(LookupTableFactory currentValue) {
        return Optional.empty();
    }
}
