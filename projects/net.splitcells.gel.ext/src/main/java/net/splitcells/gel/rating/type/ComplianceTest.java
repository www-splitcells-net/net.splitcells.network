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
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Compliance.compliance;
import static org.assertj.core.api.Assertions.assertThat;

public class ComplianceTest {

    @Test
    public void testEquality() {
        assertThat(compliance(false)).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(false))).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(true))).isEqualTo(compliance(false));
        assertThat(compliance(true).combine(compliance(false))).isNotEqualTo(compliance(true));
    }

    @Test
    public void testCombinationsAndOrderConsistency() {
        assertThat(compliance(false).combine(compliance(false)).compare_partially_to(compliance(false)).orElseThrow())
                .isEqualTo(EQUAL);
        assertThat(compliance(false).combine(compliance(true)).compare_partially_to(compliance(false)).orElseThrow())
                .isEqualTo(EQUAL);
        assertThat(compliance(true).combine(compliance(false)).compare_partially_to(compliance(true)).orElseThrow())
                .isNotEqualTo(EQUAL);
    }

    @Test
    public void testOrderConsistency() {
        assertThat(compliance(true).compare_partially_to(compliance(true)).orElseThrow()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(false)).orElseThrow()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(true)).orElseThrow()).isEqualTo(LESSER_THAN);
        assertThat(compliance(true).compare_partially_to(compliance(false)).orElseThrow()).isEqualTo(GREATER_THAN);
    }

}
