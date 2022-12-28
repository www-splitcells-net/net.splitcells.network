package net.splitcells.cin;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.PositionClusters.groupNameOfPositionCluster;
import static net.splitcells.cin.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class PositionClustersTest {
    @Test
    public void testPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testPositions")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(1, 1)
                        , list(1, 2)
                        , list(2, 2)
                        , list(2, 1)
                        , list(2, 0)
                        , list(1, 0)
                        , list(0, 0)
                        , list(0, 1)
                        , list(0, 2)
                        , list(1, 0)
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
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(positionClusters(x, y));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 10).forEach(i -> testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)));
        testSubject.deallocate(testSubject.demandsUsed().line(0)
                , testSubject.suppliesUsed().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(0, 0)));
        rangeClosed(1, 9).forEach(i -> testSubject.deallocate(testSubject.demandsUsed().line(0)
                , testSubject.suppliesUsed().line(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list());
    }
}