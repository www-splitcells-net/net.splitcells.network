package net.splitcells.dem.resource.host;

import net.splitcells.dem.config.ProgramName;
import net.splitcells.dem.environment.config.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.Dem.m;

/**
 * IDEA Only use target folder during development.
 */
public class OutputPath extends OptionI<Path> {

	public OutputPath() {
		super(() -> Paths.get("."//
				, "target"//
				, m().configValue(ProgramName.class)//
		));
	}

}
