package net.splitcells.dem.config;

import net.splitcells.dem.environment.config.OptionI;

import java.time.ZonedDateTime;

public class StartTime extends OptionI<ZonedDateTime> {

	public StartTime() {
		super(() -> ZonedDateTime.now());
	}

}
