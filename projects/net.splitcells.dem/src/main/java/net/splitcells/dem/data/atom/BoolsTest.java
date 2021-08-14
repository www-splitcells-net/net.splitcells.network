/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.atom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


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
        assertThrows(AssertionError.class, () -> Bools.require(false));
    }
}
