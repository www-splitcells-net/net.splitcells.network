package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.for_all;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.TemplateInitializer.templateInitializer;
import static org.assertj.core.api.Assertions.assertThat;

public class TemplateInitializerTest {
    @Test
    public void testTemplate() {
        final var demandNumber = attribute(Integer.class, "demand-number");
        final var supplyNumber = attribute(Integer.class, "supply-number");
        final var testProduct = defineProblem()
                .withDemandAttributes(demandNumber)
                .withDemands(list(
                        list(1)
                        , list(1)
                        , list(1)
                        , list(1)
                        , list(2)
                        , list(2)
                ))
                .withSupplyAttributes(supplyNumber)
                .withSupplies(list(
                        list(1)
                        , list(1)
                        , list(2)
                        , list(2)
                        , list(1)
                        , list(1)))
                .withConstraint(for_all())
                .toProblem()
                .asSolution();
        testProduct.optimize(templateInitializer(
                Databases.database(list(demandNumber, supplyNumber)
                        , list
                                (list(2, 1)
                                        , list(2, 1)
                                        , list(1, 2)
                                        , list(1, 2)
                                        , list(1, 1)
                                        , list(1, 1)))));
        assertThat(testProduct.getLines().get(0).values()).isEqualTo(list(2, 1));
        assertThat(testProduct.getLines().get(1).values()).isEqualTo(list(2, 1));
        assertThat(testProduct.getLines().get(2).values()).isEqualTo(list(1, 2));
        /*
         * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
         *
         * This program and the accompanying materials are made available under the
         * terms of the Eclipse Public License 2.0, which is available at
         * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
         * which is available at https://spdx.org/licenses/MIT.html.
         *
         * This Source Code may also be made available under the following Secondary
         * Licenses when the conditions for such availability set forth in the Eclipse
         * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
         * or any later versions with the GNU Classpath Exception which is
         * available at https://www.gnu.org/software/classpath/license.html.
         *
         * SPDX-License-Identifier: EPL-2.0 OR MIT
         */
        assertThat(testProduct.getLines().get(3).values()).isEqualTo(list(1, 2));
        assertThat(testProduct.getLines().get(4).values()).isEqualTo(list(1, 1));
        assertThat(testProduct.getLines().get(5).values()).isEqualTo(list(1, 1));
    }
}
