package net.splitcells.gel.kodols.resursi;

import static net.splitcells.dem.Dem.environment;

import java.nio.file.Path;

import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.dem.resource.host.DocumentsPath;

public class PagaiduTaka extends OptionI<Path> {
    public PagaiduTaka() {
        super(() -> environment().config()
                .configValue(DocumentsPath.class)
                .resolve("pagaidu"));
    }
}
