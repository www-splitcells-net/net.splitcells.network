package net.splitcells.dem.environment.config;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.util.Optional;

import static net.splitcells.dem.data.atom.BoolI.truthful;

public class IsDeterministic extends OptionI<Optional<Bool>> {

	public IsDeterministic() {
		super(() -> Optional.of(truthful()));
	}

}
