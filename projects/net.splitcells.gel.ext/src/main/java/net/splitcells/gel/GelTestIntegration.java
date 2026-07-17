/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.gel.ext.GelExtCell;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Test.testIntegration;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.ext.GelExtCell.standardDeveloperConfigurator;

public final class GelTestIntegration {
    private GelTestIntegration() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        testIntegration();
        if (GelExtCell.process(() -> {
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
