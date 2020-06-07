package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.time.format.DateTimeFormatter;

public class ProgramLocalIdentity extends OptionI<String> {

	public ProgramLocalIdentity() {
		super(() -> Dem.environment().config().configValue(ProgramName.class)
				+ Dem.environment().config().configValue(StartTime.class).format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.m.s.n"))//
		);
	}

}
