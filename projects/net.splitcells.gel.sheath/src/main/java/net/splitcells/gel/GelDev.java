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

import net.splitcells.gel.test.functionality.OralExamsTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        new OralExamsTest().testRandomInstanceSolving();
        /*process(() -> {
            //new MinimalDistanceTest().test_multiple_line_addition_and_removal();
            //new ConstraintTest().test_incomingGroupsOfConstraintPath();
            new OralExamsTest().testCurrentDevelopment();
        }, standardConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")))
                    //.withConfigValue(MessageFilter.class
                    //        , a -> a.path().equals(list("demands", "Solution", "optimization", "Escalator"))
                    //                || a.path().equals(list("demands", "Solution")))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L)
                    .withConfigValue(ProcessHostPath.class
                            , Paths.userHome("connections", "tmp.storage", "dem"));
        }));*/
    }
}
