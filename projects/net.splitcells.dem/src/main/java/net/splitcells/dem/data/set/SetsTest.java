/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

public class SetsTest {
    @Test
    public void testForDefaultFactory() {
        assertThat
                (process(() -> assertThat(configValue(Sets.class).set()._isDeterministic()).contains(false)).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(configValue(Sets.class).set(list())._isDeterministic()).contains(false))
                        .hasError())
                .isFalse();
    }

    @Test
    public void testForDeterministicFactory() {
        assertThat
                (process(() -> assertThat(configValue(Sets.class).set()._isDeterministic()).contains(true)
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                ).hasError())
                .isFalse();
        assertThat
                (process(() -> assertThat(configValue(Sets.class).set(list())._isDeterministic()).contains(true)
                        , env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                ).hasError())
                .isFalse();
    }
}
