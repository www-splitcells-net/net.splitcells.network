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
