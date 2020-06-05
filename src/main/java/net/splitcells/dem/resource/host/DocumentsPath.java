package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.OptionI;

import java.nio.file.Path;

import static net.splitcells.dem.Dem.m;

public class DocumentsPath extends OptionI<Path> {
	public DocumentsPath() {
		super(() -> m().configValue(OutputPath.class)//
				.resolve("documents"));
	}
}
