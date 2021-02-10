package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.time.ZonedDateTime;

public class StartTime extends OptionI<ZonedDateTime> {

	public StartTime() {
		super(() -> ZonedDateTime.now());
	}

}
