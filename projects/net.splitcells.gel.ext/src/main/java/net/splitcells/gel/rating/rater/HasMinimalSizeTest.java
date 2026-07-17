/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.Tables;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.rating.rater.lib.HasMinimalSize.hasMinimalSize;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class HasMinimalSizeTest {

    @UnitTest public void testConstructor() {
        requireThrow(() -> hasMinimalSize(1));
    }

    @UnitTest public void testRating() {
        final var lineSupplier = Tables.table();
        final var testValue = Then.then(hasMinimalSize(2));
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
            assertThat(testValue.defying()).hasSize(1);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1));
        }
        {
            testValue.register(secondTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(2);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register(thirdTestVaLUE);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(3);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.registerBeforeRemoval(thirdTestVaLUE);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(2);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.registerBeforeRemoval(secondTestValue);
            assertThat(testValue.defying()).hasSize(1);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1));
        }
        {
            testValue.registerBeforeRemoval(firstTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
    }
}
