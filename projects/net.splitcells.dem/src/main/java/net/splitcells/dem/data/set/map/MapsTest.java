package net.splitcells.dem.data.set.map;

import net.splitcells.dem.data.atom.BoolI;
import net.splitcells.dem.environment.config.IsDeterministic;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.set.map.Maps.map;
import static org.assertj.core.api.Assertions.assertThat;

public class MapsTest {
    @Test
    public void testForDefaultFactory() {
        assertThat
                (process(() -> assertThat(configValue(Maps.class).map()._isDeterministic()).contains(false)).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(configValue(Maps.class).map(map())._isDeterministic()).contains(false))
                        .hasError())
                .isFalse();
    }

    @Test
    public void testForDeterministicFactory() {
        assertThat
                (process(() -> assertThat(configValue(Maps.class).map()._isDeterministic()).contains(true)
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(BoolI.truthful()))
                ).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(configValue(Maps.class).map(map())._isDeterministic()).contains(true)
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(BoolI.truthful()))
                ).hasError())
                .isFalse();
    }
}
