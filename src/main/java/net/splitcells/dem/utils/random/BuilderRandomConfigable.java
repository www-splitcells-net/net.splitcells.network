package net.splitcells.dem.utils.random;

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.data.atom.Bool;

import java.util.Optional;

import static net.splitcells.dem.Dem.environment;

public class BuilderRandomConfigable implements RndSrcF {

    private RndSrcF builderRandom;

    protected BuilderRandomConfigable() {
        if (environment().config().configValue(IsDeterministic.class).isEmpty() || environment().config().configValue(IsDeterministic.class).get().is_true()) {
            deterministic_builder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else {
            builderRandom = new RndSrcStandardF();
        }
    }

    protected BuilderRandomConfigable(Optional<Bool> deterministic) {
        update_determinism(deterministic);
    }

    protected BuilderRandomConfigable(Long seed) {
        deterministic_builder(seed);
    }

    public void update_determinism(Optional<Bool> arg) {
        if (arg.isEmpty() || arg.get().is_true()) {
            deterministic_builder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else if (arg.isPresent() && arg.get().is_false()) {
            this.builderRandom = new RndSrcStandardF();
        } else {
            throw new RuntimeException();
        }
    }

    private void deterministic_builder(Long seed) {
        this.builderRandom = new BuilderRandomDeterministic(seed);
    }

    @Override
    public Randomness rnd() {
        return builderRandom.rnd();
    }

    @Override
    public RndSrcCrypt rndCrypt() {
        return builderRandom.rndCrypt();
    }

}
