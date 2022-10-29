/*
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.rating.type.Cost;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class AllSameTest {
    @Test
    public void testAddAndRemove() {
        final var firstValue = 1;
        final var secondValue = 2;
        final var attribute = attribute(Integer.class, "attribute");
        final var lineSupplier = database(attribute);
        final var testValue = Then.then(allSame(attribute));
        final var firstTestValue = lineSupplier.addTranslated(list(firstValue));
        final var secondTestValue = lineSupplier.addTranslated(list(secondValue));
        final var thirdTestValue = lineSupplier.addTranslated(list(firstValue));
        {
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(noCost());
        }
        {
            testValue.register(firstTestValue);
            assertThat(testValue.complying()).hasSize(1);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(noCost());
        }
        {
            testValue.register(secondTestValue);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.defying()).hasSize(2);
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(cost(1));
        }
        {
            testValue.register(thirdTestValue);
            // IDEA This functionality is simplistic, but correct. Maybe provide alternatives?
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.defying()).hasSize(3);
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(cost(1));
        }
        {
            testValue.registerBeforeRemoval(firstTestValue);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.defying()).hasSize(2);
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(cost(1));
        }
        {
            testValue.registerBeforeRemoval(secondTestValue);
            assertThat(testValue.complying()).hasSize(1);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(noCost());
        }
        {
            testValue.registerBeforeRemoval(thirdTestValue);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.rating().asMetaRating().getContentValue(Cost.class)).isEqualTo(noCost());
        }
    }
}
