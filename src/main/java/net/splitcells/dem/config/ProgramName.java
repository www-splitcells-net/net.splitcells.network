package net.splitcells.dem.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.OptionI;

import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

public class ProgramName extends OptionI<String> {

	public ProgramName() {
		super(() -> simplifiedName(Dem.m().configValue(ProgramRepresentative.class)));
	}

}
