/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.data.lookup.LookupColumnImpl.lookupColumnImpl;
import static net.splitcells.gel.data.lookup.LookupManager.lookupManager;

public class Lookups {
    private Lookups() {
        throw constructorIllegal();
    }

    public static <R> LookupColumn<R> lookup(View view, Attribute<R> attribute) {
        return lookupManager(view, attribute);
    }
}
