/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated.raters.deprecated;

import net.splitcells.dem.data.set.map.typed.TypedKey;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class PositionClustersCenterY implements TypedKey<Integer> {
    private PositionClustersCenterY() {
        throw constructorIllegal();
    }
}
