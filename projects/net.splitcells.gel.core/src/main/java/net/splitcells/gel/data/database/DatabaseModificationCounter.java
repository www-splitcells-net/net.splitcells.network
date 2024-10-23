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
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.testing.MetaCounter;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.website.server.project.renderer.CsvRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.testing.MetaCounter.metaCounter;

public class DatabaseModificationCounter implements Option<MetaCounter> {
    @Override
    public MetaCounter defaultValue() {
        final var metaCounter = metaCounter(discoverable(DatabaseModificationCounter.class)).withConfig(env
                -> env.config().configValue(Databases.class).withAspect(DatabaseModificationCounterAspect::databaseModificationCounterAspect));
        ObjectsRenderer.registerObject(new CsvRenderer() {
            @Override
            public String renderCsv() {
                return "time,count\n" + metaCounter.sumCounter().measurements()
                        .stream()
                        .map(m -> m.time() + "," + m.value() + "\n")
                        .reduce("", (a, b) -> a + b);
            }

            @Override
            public Optional<String> title() {
                return Optional.empty();
            }

            @Override
            public List<String> path() {
                final var path = metaCounter.path();
                path.set(path.size() - 1, path.get(path.size() - 1) + ".csv");
                return path;
            }
        });
        return metaCounter;
    }
}
