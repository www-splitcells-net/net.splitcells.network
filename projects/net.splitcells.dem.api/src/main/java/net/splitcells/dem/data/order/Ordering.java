/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.order;

import net.splitcells.dem.object.Base;

public enum Ordering implements Base {
    EQUAL, LESSER_THAN, GREATER_THAN;

    public Ordering invert() {
        if (this.equals(LESSER_THAN)) {
            return GREATER_THAN;
        } else if (this.equals(GREATER_THAN)) {
            return LESSER_THAN;
        } else {
            return EQUAL;
        }
    }
}