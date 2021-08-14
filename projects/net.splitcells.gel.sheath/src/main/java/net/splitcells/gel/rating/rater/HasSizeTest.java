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
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.type.Then;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class HasSizeTest extends TestSuiteI {
    @Test
    public void test_size_requirement_greater_than_one() {
        final var lineSupplier = database();
        final var testSubject = Then.then(hasSize(2));
        assertThat(testSubject.complying()).isEmpty();
        assertThat(testSubject.defying()).isEmpty();
        final var firstTestValue = lineSupplier.addTranslated(list());
        final var secondTestValue = lineSupplier.addTranslated(list());
        final var thirdTestValue = lineSupplier.addTranslated(list());
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(firstTestValue);
            assertThat(testSubject.defying()).hasSize(1);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(1.0));
        }
        {
            testSubject.register(secondTestValue);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(thirdTestValue);
            assertThat(testSubject.defying()).hasSize(3);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(1.0));
        }
        {
            testSubject.register_before_removal(thirdTestValue);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register_before_removal(secondTestValue);
            assertThat(testSubject.defying()).hasSize(1);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(1.0));
        }
        {
            testSubject.register_before_removal(firstTestValue);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
    }

    @Test
    public void testRating() {
        final var lineSupplier = database();
        final var testValue = Then.then(hasSize(1));
        assertThat(testValue.complying()).isEmpty();
        assertThat(testValue.defying()).isEmpty();
        final var firstTestValue = lineSupplier.addTranslated(list());
        final var secondTestValue = lineSupplier.addTranslated(list());
        final var thirdTestVaLUE = lineSupplier.addTranslated(list());
        {
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register(firstTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(1);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register(secondTestValue);
            assertThat(testValue.defying()).hasSize(2);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1.0));
        }
        {
            testValue.register(thirdTestVaLUE);
            assertThat(testValue.defying()).hasSize(3);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(2.0));
        }
        {
            testValue.register_before_removal(thirdTestVaLUE);
            assertThat(testValue.defying()).hasSize(2);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1.0));
        }
        {
            testValue.register_before_removal(secondTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(1);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register_before_removal(firstTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
    }
}
