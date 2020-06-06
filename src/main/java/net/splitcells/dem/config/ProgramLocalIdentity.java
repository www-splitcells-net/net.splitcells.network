package net.splitcells.dem.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.OptionI;

import java.time.format.DateTimeFormatter;

public class ProgramLocalIdentity extends OptionI<String> {

	public ProgramLocalIdentity() {
		super(() -> Dem.environment().configValue(ProgramName.class)
				+ Dem.environment().configValue(StartTime.class).format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.m.s.n"))//
		);
	}

}
