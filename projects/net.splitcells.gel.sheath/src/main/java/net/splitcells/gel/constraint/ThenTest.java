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
package net.splitcells.gel.constraint;

import net.splitcells.gel.constraint.type.Then;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueBasedOnRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Only costs are used for testing in order to simplify the defiance detection.
 */
public class ThenTest {
    @Test
    public void test_propagation_of_defiance() {
        final var lineSupplier = database();
        final var firstDefianceCost = 7.0;
        final var secondDefianceCost = 3.0;
        final var testSubject = then(cost(firstDefianceCost));
        final var propagationValidator
                = testSubject.withChildren(then(cost(secondDefianceCost)));
        final var testValue = lineSupplier.addTranslated(list());
        final var otherTestValue = lineSupplier.addTranslated(list());
        assertThat(testSubject.complying()).isEmpty();
        assertThat(propagationValidator.complying()).isEmpty();
        {
            testSubject.register(testValue);
            assertThat(testSubject.defying()).hasSize(1);
            assertThat(propagationValidator.defying()).hasSize(1);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(propagationValidator.complying()).isEmpty();
            assertThat(testSubject.rating())
                    .isEqualTo(cost(firstDefianceCost + secondDefianceCost));
        }
        {
            testSubject.register(otherTestValue);
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(propagationValidator.defying()).hasSize(2);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(propagationValidator.complying()).isEmpty();
            assertThat(testSubject.rating())
                    .isEqualTo(cost(2 * (firstDefianceCost + secondDefianceCost)));
        }
        {
            testSubject.registerBeforeRemoval(testValue);
            assertThat(testSubject.defying()).hasSize(1);
            assertThat(propagationValidator.defying()).hasSize(1);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(propagationValidator.complying()).isEmpty();
            assertThat(testSubject.rating())
                    .isEqualTo(cost(firstDefianceCost + secondDefianceCost));
        }
        {
            testSubject.registerBeforeRemoval(otherTestValue);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(propagationValidator.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(propagationValidator.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(0));
        }
    }

    @Test
    public void test_propagation_of_compliance() {
        final var lineSupplier = database();
        final var testSubject = then(noCost());
        final var propagationValidator = testSubject.withChildren(then(noCost()));
        final var testValue = lineSupplier.addTranslated(list());
        final var otherTestvalue = lineSupplier.addTranslated(list());
        assertThat(testSubject.complying()).isEmpty();
        assertThat(propagationValidator.complying()).isEmpty();
        {
            testSubject.register(testValue);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(propagationValidator.complying()).hasSize(1);
            assertThat(propagationValidator.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(0));
        }
        {
            testSubject.register(otherTestvalue);
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(propagationValidator.complying()).hasSize(2);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(propagationValidator.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(0));
        }
        {
            testSubject.registerBeforeRemoval(testValue);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(propagationValidator.complying()).hasSize(1);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(propagationValidator.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(0));
        }
        {
            testSubject.registerBeforeRemoval(otherTestvalue);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(propagationValidator.complying()).isEmpty();
            assertThat(testSubject.defying()).isEmpty();
            assertThat(propagationValidator.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(0));
        }
    }

    @Test
    public void test_rating_of_defying() {
        final var defianceCost = 3.0;
        final var lineSupplier = database();
        final var testSubject = then(cost(defianceCost));
        final var testValue = lineSupplier.addTranslated(list());
        assertThat(testSubject.rating()).isEqualTo(cost(0));
        final var testGroup = testSubject.register(testValue);
        assertThat(testSubject.rating(testGroup))
                .isEqualTo(cost(defianceCost));
        final var additionalLine = lineSupplier.addTranslated(list());
        final var additionalGroup = testSubject.register(additionalLine);
        assertThat(testGroup).isEqualTo(additionalGroup);
        assertThat(testSubject.rating(testGroup))
                .isEqualTo(cost(2 * defianceCost));
        assertThat(testSubject.complying()).isEmpty();
        assertThat(testSubject.defying())
                .containsExactlyInAnyOrder(testValue, additionalLine);
        testSubject.registerBeforeRemoval(testValue);
        assertThat(testSubject.rating(additionalGroup))
                .isEqualTo(cost(defianceCost));
        assertThat(testSubject.complying()).isEmpty();
        assertThat(testSubject.defying()).containsExactly(additionalLine);
        testSubject.registerBeforeRemoval(additionalLine);
        assertThat(testSubject.rating()).isEqualTo(cost(0));
        testEmptyThen(testSubject, testGroup, additionalGroup);
    }

    @Test
    public void test_rating_of_complying() {
        final var lineSupplier = database();
        final var testSubject = then(noCost());
        final var testValue = lineSupplier.addTranslated(list());
        assertThat(testSubject.rating()).isEqualTo(noCost());
        final var testGroup = testSubject.register(testValue);
        assertThat(testSubject.rating(testGroup)).isEqualTo(noCost());
        final var additionalLine = lineSupplier.addTranslated(list());
        final var additionalGroup = testSubject.register(additionalLine);
        assertThat(testGroup).isEqualTo(additionalGroup);
        assertThat(testSubject.rating(testGroup)).isEqualTo(noCost());
        assertThat(testSubject.complying())
                .containsExactlyInAnyOrder(testValue, additionalLine);
        assertThat(testSubject.defying()).isEmpty();
        testSubject.registerBeforeRemoval(testGroup, testValue);
        assertThat(testSubject.rating(additionalGroup)).isEqualTo(noCost());
        assertThat(testSubject.complying()).containsExactly(additionalLine);
        assertThat(testSubject.defying()).isEmpty();
        testSubject.registerBeforeRemoval(additionalGroup, additionalLine);
        assertThat(testSubject.rating()).isEqualTo(noCost());
        testEmptyThen(testSubject, testGroup, additionalGroup);
    }

    @Test
    public void testRating() {
        final double defianceCost = 3.0;
        final var lineSupplier = database(attribute(String.class));
        final var complyingLine = lineSupplier.addTranslated(list("complying"));
        final var defyingLine = lineSupplier.addTranslated(list("defying"));
        final var testSubject = then(lineValueBasedOnRater(line -> {
            if (line == complyingLine) {
                return noCost();
            } else {
                return cost(defianceCost);
            }
        }));
        final var complyingGroup = testSubject.register(complyingLine);
        assertThat(testSubject.rating(complyingGroup)).isEqualTo(noCost());
        assertThat(testSubject.complying()).containsExactly(complyingLine);
        assertThat(testSubject.defying()).isEmpty();
        final var defyingGroup = testSubject.register(defyingLine);
        assertThat(testSubject.rating(testSubject.injectionGroup(), complyingLine))
                .isEqualTo(noCost());
        assertThat(testSubject.rating(testSubject.injectionGroup(), defyingLine))
                .isEqualTo(cost(defianceCost));
        assertThat(complyingGroup).isEqualTo(defyingGroup);
        assertThat(testSubject.rating(complyingGroup))
                .isEqualTo(cost(defianceCost));
        assertThat(testSubject.defying()).containsExactly(defyingLine);
        assertThat(testSubject.complying()).containsExactly(complyingLine);
        testSubject.registerBeforeRemoval(complyingGroup, complyingLine);
        assertThat(testSubject.rating(complyingGroup))
                .isEqualTo(cost(defianceCost));
        assertThat(testSubject.defying()).containsExactly(defyingLine);
        assertThat(testSubject.complying()).isEmpty();
        testSubject.registerBeforeRemoval(defyingGroup, defyingLine);
        testEmptyThen(testSubject, complyingGroup, defyingGroup);
    }

    void testEmptyThen(Then testSubject, GroupId... groups) {
        asList(groups).forEach(grupa -> {
            assertThat(testSubject.rating(grupa)).isEqualTo(cost(0));
            assertThat(testSubject.complying(grupa)).isEmpty();
            assertThat(testSubject.defying(grupa)).isEmpty();
        });
        assertThat(testSubject.complying()).isEmpty();
        assertThat(testSubject.defying()).isEmpty();
        assertThat(testSubject.rating()).isEqualTo(cost(0));
    }
}
