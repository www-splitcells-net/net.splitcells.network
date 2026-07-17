/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

public interface LookupTableFactory extends ConnectingConstructor<LookupTable>, AspectOrientedConstructor<LookupTable> {

    /**
     * @param view The {@link View} on which the lookup will be performed.
     * @param name  This is the name of the {@link LookupTable} being constructed.
     * @return An instance, where no {@link Line} of {@link View} is {@link LookupTable#register(Line)}.
     */
    LookupTable lookupTable(View view, String name);

    /**
     * @param view     The {@link View} on which the lookup will be performed.
     * @param attribute The {@link Attribute}, that will be looked up.
     * @return An instance, where no {@link Line} of {@link View} is {@link LookupTable#register(Line)}.
     */
    LookupTable lookupTable(View view, Attribute<?> attribute);

    LookupTable lookupTable(View view, Attribute<?> attribute, boolean cacheRawLines);
}
