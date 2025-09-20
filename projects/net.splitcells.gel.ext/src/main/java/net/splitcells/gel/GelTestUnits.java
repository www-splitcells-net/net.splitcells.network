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
import net.splitcells.dem.resource.communication.log.MessageFilter;

import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelDev.standardDeveloperConfigurator;

public final class GelTestUnits {
    private GelTestUnits() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelDev.process(() -> {
                    if (!testUnits()) {
                        System.out.println("Could not execute tests successfully.");
                        Dem.systemExit(1);
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config().withConfigValue(MessageFilter.class, a -> false);
                }));
    }
}
