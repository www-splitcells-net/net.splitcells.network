package net.splitcells.cin;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.sep.Network.network;

public class World {
    private static final String WORLD_HISTORY = "world-history";
    private static final Attribute<Integer> WORLD_TIME = attribute(Integer.class, "world-time");
    private static final Attribute<Integer> POSITION_X = attribute(Integer.class, "position-x");
    private static final Attribute<Integer> POSITION_Y = attribute(Integer.class, "position-y");
    private static final Attribute<Boolean> STATE = attribute(Boolean.class, "state");

    public static void main(String... args) {
        GelDev.process(() -> {
            final var network = network();
            network.withNode(WORLD_HISTORY, worldHistory());
        }, env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())));
    }

    private static Solution worldHistory() {
        return defineProblem("Conway's Game of Life")
                .withDemandAttributes(WORLD_TIME)
                .withDemands(worldsTimeSpace(0))
                .withSupplyAttributes(POSITION_X, POSITION_Y, STATE)
                .withSupplies(worldWithGlider())
                .withConstraint(r -> {
                    r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(loneliness()).then(dies());
                    r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(goodCompany()).then(survives());
                    r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(crowded()).then(dies());
                    r.forAll(timeSteps()).forAll(positionClusters()).forAll(isDead()).forAll(revivalCondition()).then(becomesAlive());
                    r.forAll(timeSteps()).forAll(positionClusters()).then(unchanged());
                    return r;
                }).toProblem()
                .asSolution();
    }

    private static List<List<Object>> worldsTimeSpace(Integer worldTime) {
        final List<List<Object>> worldsTimeSpace = list();
        IntStream.rangeClosed(-10, 0).forEach(i -> {
            IntStream.rangeClosed(-10, 0).forEach(j -> {
                worldsTimeSpace.add(list(worldTime));
            });
        });
        return worldsTimeSpace;
    }

    @SuppressWarnings("unchecked")
    private static List<List<Object>> worldWithGlider() {
        final List<List<Object>> worldWithGlider = list();
        worldWithGlider.withAppended(
                list(1, 0, 1)
                , list(2, -1, 1)
                , list(3, -1, 1)
                , list(1, -2, 1)
                , list(2, -2, 1));
        IntStream.rangeClosed(-10, 0).forEach(i -> {
            IntStream.rangeClosed(-10, 0).forEach(j -> {
                if (!worldWithGlider.contains(list(i, j, 1))) {
                    worldWithGlider.add(list(i, j, 0));
                }
            });
        });
        return worldWithGlider;
    }

    private static Rater positionClusters() {
        throw notImplementedYet();
    }

    private static Rater timeSteps() {
        throw notImplementedYet();
    }

    private static Rater crowded() {
        throw notImplementedYet();
    }

    private static Rater survives() {
        throw notImplementedYet();
    }

    private static Rater isAlive() {
        throw notImplementedYet();
    }

    private static Rater isDead() {
        throw notImplementedYet();
    }

    private static Rater revivalCondition() {
        throw notImplementedYet();
    }

    private static Rater becomesAlive() {
        throw notImplementedYet();
    }

    private static Rater loneliness() {
        throw notImplementedYet();
    }

    private static Rater dies() {
        throw notImplementedYet();
    }

    private static Rater unchanged() {
        throw notImplementedYet();
    }

    private static Rater goodCompany() {
        throw notImplementedYet();
    }
}
