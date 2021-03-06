package net.splitcells.dem.environment.config;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.untrue;

public class IsDeterministic extends OptionI<Optional<Bool>> {

    /**
     * Programs are not required to deterministic by default, because performance is more important by default.
     */
    public IsDeterministic() {
        super(() -> Optional.empty());
    }

}
