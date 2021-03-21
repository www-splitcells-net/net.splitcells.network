package net.splitcells.website.server;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO Make this an program argument.
 */
@Deprecated
public class SslKeystorePassword extends OptionI<String> {
    public SslKeystorePassword() {
        super(() -> "password");
    }
}
