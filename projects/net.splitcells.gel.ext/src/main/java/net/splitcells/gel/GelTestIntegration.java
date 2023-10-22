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
package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.communication.log.MessageFilter;

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
            Dem.systemExit(1);
        }
    }
}
