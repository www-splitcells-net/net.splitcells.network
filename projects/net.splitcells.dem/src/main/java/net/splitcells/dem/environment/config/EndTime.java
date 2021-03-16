package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.time.ZonedDateTime;
import java.util.Optional;

public class EndTime extends OptionI<Optional<ZonedDateTime>> {

    public EndTime() {
        super(() -> Optional.empty());
    }

}
