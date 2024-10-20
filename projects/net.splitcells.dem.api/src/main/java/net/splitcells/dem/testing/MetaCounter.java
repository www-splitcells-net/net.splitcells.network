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
package net.splitcells.dem.testing;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.object.Discoverable;

import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.testing.Counter.counter;

public class MetaCounter {
    public static MetaCounter metaCounter() {
        return new MetaCounter();
    }

    private Map<Discoverable, Counter> counters = map();
    private List<Consumer<Environment>> configs = list();
    private Counter sumCounter = counter();

    private MetaCounter() {

    }

    public void count(Discoverable subject, long times) {
        final Counter counter;
        if (counters.containsKey(subject)) {
            counter = counters.get(subject);
        } else {
            counter = counter();
            counters.put(subject, counter);
        }
        counter.count(times);
        sumCounter.count(times);
    }

    public Map<Discoverable, Counter> counters() {
        return counters;
    }

    public Counter sumCounter() {
        return sumCounter;
    }

    public MetaCounter withConfig(Consumer<Environment> config) {
        configs.add(config);
        return this;
    }

    public MetaCounter withInit(Environment env) {
        configs.forEach(c -> c.accept(env));
        return this;
    }
}
