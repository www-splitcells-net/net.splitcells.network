package net.splitcells.cin;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.gel.GelDev;

import java.util.Optional;

public class World {
    public static void main(String... args) {
        GelDev.process(() -> {

        }, env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())));
    }
}
