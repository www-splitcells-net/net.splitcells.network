package net.splitcells.website.server;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class SslKeystore extends OptionI<Path> {
    public SslKeystore() {
        super(() -> Paths.get("target/keystore.p12"));
    }
}
