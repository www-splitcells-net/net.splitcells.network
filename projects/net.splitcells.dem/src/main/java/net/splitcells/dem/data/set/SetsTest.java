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
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.set.list.Lists.list;

public class SetsTest {
    @Test
    public void testForDefaultFactory() {
        bool(process(()
                -> bool(configValue(Sets.class).set()._isDeterministic().get()).requireFalse()).hasError())
                .requireFalse();
        bool(process(()
                -> bool(configValue(Sets.class).set(list())._isDeterministic().get()).requireFalse()).hasError())
                .requireFalse();
    }

    @Test
    public void testForDeterministicFactory() {
        bool(process(() -> bool(configValue(Sets.class).set()._isDeterministic().get()).requireFalse()
                , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())))
                .hasError())
                .isFalse();
        bool(process(() -> bool(configValue(Sets.class).set(list())._isDeterministic().get()).requireFalse()
                , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())))
                .hasError())
                .isFalse();
    }
}
