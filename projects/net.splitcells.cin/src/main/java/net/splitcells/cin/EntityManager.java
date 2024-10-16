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
package net.splitcells.cin;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

/**
 * Floats instead of doubles are used, as these are used in game development and
 * seem to be better supported on GPUs.
 */
public class EntityManager {
    public static final Attribute<Float> TIME = attribute(Float.class, "time");
    public static final Attribute<Float> OWNER = attribute(Float.class, "owner");
    public static final Attribute<Float> ATTRIBUTE = attribute(Float.class, "attribute");
    public static final Attribute<Float> VALUE = attribute(Float.class, "value");

    private EntityManager() {

    }

    public static Solution entityManager(String name) {
        return defineProblem(name)
                .withDemandAttributes(TIME, OWNER)
                .withNoDemands()
                .withSupplyAttributes(ATTRIBUTE, VALUE)
                .withSupplies()
                .withConstraint(query -> query)
                .toProblem()
                .asSolution()
                ;
    }

    public static void initPlayers(Solution entityManager, float initTime, int numberOfPlayers) {
        final var demands = entityManager.demands();
        range(0, numberOfPlayers).forEach(i -> {
            demands.addTranslated(list(initTime, (float) i));
        });
    }


    public static void supplyNextTime(Solution entityManager, float currentTime, float nextTime) {
        final var demands = entityManager.demands();
        demands.lookup(TIME, currentTime).unorderedLinesStream().forEach(demand -> {
            demands.addTranslated(list(nextTime, demand.value(OWNER)));
        });
    }
}
