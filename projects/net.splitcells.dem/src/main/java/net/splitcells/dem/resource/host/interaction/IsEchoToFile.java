package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.environment.config.framework.OptionI;

public class IsEchoToFile extends OptionI<Boolean> {

	public IsEchoToFile() {
		super(() -> false);
	}

}
