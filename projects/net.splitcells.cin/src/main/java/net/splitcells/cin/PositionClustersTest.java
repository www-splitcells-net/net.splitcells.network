package net.splitcells.cin;

import net.splitcells.dem.utils.MathUtils;
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
                        , list(4, 4)
                        , list(4, 5)
                        , list(5, 5)
                        , list(5, 4)
                        , list(5, 3)
                        , list(4, 3)
                        , list(3, 3)
                        , list(3, 4)
                        , list(3, 5)
                        , list(4, 3)
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
                        , list()
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
        rangeClosed(1, 20).forEach(i -> testSubject.allocate(testSubject.demandsFree().line(0)
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
                        , groupNameOfPositionCluster(0, 0)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)
                        , groupNameOfPositionCluster(3, 3)));
        rangeClosed(1, 20).forEach(i -> testSubject.deallocate(testSubject.demandsUsed().line(0)
                , testSubject.suppliesUsed().line(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list());
    }

    @Test
    public void testBottomRightPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testPositions")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(1, -2)
                        , list(1, -1)
                        , list(2, -1)
                        , list(2, -2)
                        , list(2, -3)
                        , list(1, -3)
                        , list(0, -3)
                        , list(0, -2)
                        , list(0, -1)
                        , list(1, -3)
                        , list(4, -5)
                        , list(4, -4)
                        , list(5, -4)
                        , list(5, -5)
                        , list(5, -6)
                        , list(4, -6)
                        , list(3, -6)
                        , list(3, -5)
                        , list(3, -4)
                        , list(4, -6)
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
                        , list()
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
        rangeClosed(1, 20).forEach(i -> testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(0, -1)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)
                        , groupNameOfPositionCluster(3, -4)));
    }

    @Test
    public void testBottomLeftPositionCluster() {
        final var x = attribute(Integer.class, "x");
        final var y = attribute(Integer.class, "y");
        final var testSubject = defineProblem("testPositions")
                .withDemandAttributes(x, y)
                .withDemands(list(
                        list(-2, -2)
                        , list(-2, -1)
                        , list(-1, -1)
                        , list(-1, -2)
                        , list(-1, -3)
                        , list(-2, -3)
                        , list(-3, -3)
                        , list(-3, -2)
                        , list(-3, -1)
                        , list(-2, -3)
                        , list(-5, -5)
                        , list(-5, -4)
                        , list(-4, -4)
                        , list(-4, -5)
                        , list(-4, -6)
                        , list(-5, -6)
                        , list(-6, -6)
                        , list(-6, -5)
                        , list(-6, -4)
                        , list(-5, -6)
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
                        , list()
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
        rangeClosed(1, 20).forEach(i -> testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0)));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-1, -1)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)
                        , groupNameOfPositionCluster(-4, -4)));
    }
}