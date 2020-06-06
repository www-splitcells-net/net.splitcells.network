package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.Dem.environment;

/**
 * IDEA Only use target folder during development.
 */
public class OutputPath extends OptionI<Path> {

	public OutputPath() {
		super(() -> Paths.get("."//
				, "target"//
				, environment().configValue(ProgramName.class)//
		));
	}

}
