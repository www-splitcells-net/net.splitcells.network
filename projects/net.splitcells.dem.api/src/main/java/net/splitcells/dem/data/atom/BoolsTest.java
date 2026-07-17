/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.atom;

import net.splitcells.dem.utils.ExecutionException;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.testing.Assertions.requireThrow;


public class BoolsTest {
    /**
     * If condition is met nothing happens.
     */
    @Test
    public void testRequiredTruth() {
        Bools.require(true);
    }

    @Test
    public void testRequiredUntruth() {
        requireThrow(ExecutionException.class, () -> Bools.require(false));
    }
}
