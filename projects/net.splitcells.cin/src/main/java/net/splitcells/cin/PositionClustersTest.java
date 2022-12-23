package net.splitcells.cin;

import org.junit.jupiter.api.Test;

import static net.splitcells.cin.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class PositionClustersTest {
    @Test
    public void testPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testPositions")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(0, 0)
                        , list(0, 1)
                        , list(1, 1)
                        , list(1, 0)
                        , list(1, -1)
                        , list(0, -1)
                        , list(-1, -1)
                        , list(-1, 0)
                        , list(-1, 1)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
    }
}