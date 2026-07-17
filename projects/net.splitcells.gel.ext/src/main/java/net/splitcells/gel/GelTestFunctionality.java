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

import static net.splitcells.dem.testing.Test.testFunctionality;
import static net.splitcells.gel.ext.GelExtCell.standardDeveloperConfigurator;

public class GelTestFunctionality {
    public static void main(String... args) {
        if (GelExtCell.process(() -> {
                    if (!testFunctionality()) {
                        throw new RuntimeException();
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config()
                            .withConfigValue(MessageFilter.class, a -> false)
                            .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()));
                })).hasError()) {
            Dem.systemExit(0);
        }
    }
}
