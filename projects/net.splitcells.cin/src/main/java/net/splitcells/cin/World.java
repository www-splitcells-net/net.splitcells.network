package net.splitcells.cin;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.rater.RegulatedLength.regulatedLength;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.sep.Network.network;

public class World {
    private static final String WORLD_HISTORY = "world-history";
    private static final Attribute<Integer> WORLD_TIME = attribute(Integer.class, "world-time");
    private static final Attribute<Integer> OBJECT_ID = attribute(Integer.class, "object-id");
    private static final Attribute<Integer> POSITION_X = attribute(Integer.class, "position-x");
    private static final Attribute<Integer> POSITION_Y = attribute(Integer.class, "position-y");

    public static void main(String... args) {
        GelDev.process(() -> {
            final var network = network();
            network.withNode(WORLD_HISTORY, worldHistory());
        }, env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())));
    }

    private static Solution worldHistory() {
        return defineProblem("Conway's Game of Life")
                .withDemandAttributes(WORLD_TIME, OBJECT_ID)
                .withSupplyAttributes(POSITION_X, POSITION_Y)
                .withConstraint(r -> {
                    return r;
                }).toProblem()
                .asSolution();
    }
}
