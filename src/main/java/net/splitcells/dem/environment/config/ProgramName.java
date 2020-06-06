package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.OptionI;

import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

public class ProgramName extends OptionI<String> {

	public ProgramName() {
		super(() -> simplifiedName(Dem.environment().configValue(ProgramRepresentative.class)));
	}

}
