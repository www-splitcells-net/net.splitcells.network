/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.set.map.MapFI_configured.mapFI_configured;
import static net.splitcells.dem.data.set.map.Maps.map;
import static org.assertj.core.api.Assertions.assertThat;

public class MapsTest {
    @Test
    public void testForDefaultFactory() {
        assertThat
                (process(() -> assertThat(mapFI_configured().map()._isDeterministic()).contains(false)
                        , env -> env.config()
                                .withConfigValue(IsDeterministic.class, Optional.of(Bools.untrue()))).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(mapFI_configured().map(map())._isDeterministic()).contains(false)
                        , env -> env.config()
                                .withConfigValue(IsDeterministic.class, Optional.of(Bools.untrue())))
                        .hasError())
                .isFalse();
    }

    @Test
    public void testForDeterministicFactory() {
        assertThat
                (process(() -> assertThat(mapFI_configured().map()._isDeterministic()).contains(true)
                        , env -> env.config()
                                .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                                .withConfigValue(Maps.class, mapFI_configured())
                ).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(mapFI_configured().map(map())._isDeterministic()).contains(true)
                        , env -> env.config()
                                .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                                .withConfigValue(Maps.class, mapFI_configured())
                ).hasError())
                .isFalse();
    }
}
