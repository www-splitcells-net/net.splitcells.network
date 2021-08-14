/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.data.atom.Bools;
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
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                ).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(configValue(Maps.class).map(map())._isDeterministic()).contains(true)
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                ).hasError())
                .isFalse();
    }
}
