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
package net.splitcells.gel;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Test.testIntegration;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.standardDeveloperConfigurator;

public final class GelTestIntegration {
    private GelTestIntegration() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        testIntegration();
        if (GelEnv.process(() -> {
                    if (!testIntegration()) {
                        throw new RuntimeException();
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config()
                            .withConfigValue(MessageFilter.class, a -> false)
                            .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()));
                })).hasError()) {
            System.exit(1);
        }
    }
}
