package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.for_all;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.define_problem;
import static net.splitcells.gel.solution.optimization.primitive.TemplateInitializer.templateInitializer;
import static org.assertj.core.api.Assertions.assertThat;

public class TemplateInitializerTest {
    @Test
    public void testTemplate() {
        final var demandNumber = attribute(Integer.class, "demand-number");
        final var supplyNumber = attribute(Integer.class, "supply-number");
        final var testProduct = define_problem()
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
        assertThat(testProduct.getLines().get(3).values()).isEqualTo(list(1, 2));
        assertThat(testProduct.getLines().get(4).values()).isEqualTo(list(1, 1));
        assertThat(testProduct.getLines().get(5).values()).isEqualTo(list(1, 1));
    }
}
