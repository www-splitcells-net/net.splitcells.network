/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.gel.ext.GelExtCell;

import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.ext.GelExtCell.standardDeveloperConfigurator;

public final class GelTestUnits {
    private GelTestUnits() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelExtCell.process(() -> {
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
