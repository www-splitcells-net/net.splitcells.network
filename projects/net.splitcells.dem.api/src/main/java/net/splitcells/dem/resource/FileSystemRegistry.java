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
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.resource.Service;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.atom.Thing.instance;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class FileSystemRegistry<Key> implements Service {
    public static <A> FileSystemRegistry<A> fileSystemRegistry() {
        return new FileSystemRegistry<>();
    }

    private final Map<Key, FileSystemView> content = map();

    private FileSystemRegistry() {

    }

    public FileSystemRegistry<Key> withFileSystemView(Key key, FileSystemView fileSystemView) {
        if (false) {
            // TODO Remove this code, when it is clear, that it is really not needed.
            if (content.containsKey(key)) {
                logs().appendWarning(tree("Ignoring file system registration, as it is already present.")
                        .withProperty("key", key.toString())
                        .withProperty("old value", content.get(key).toString())
                        .withProperty("new value", fileSystemView.toString()));
                return this;
            }
        }
        content.put(key, fileSystemView);
        return this;
    }

    public Map<Key, FileSystemView> content() {
        return content.shallowCopy();
    }

    @Override
    public void start() {
        config().consume(Cell.class, (cellClass, cellValue) -> {
            // TODO Create and keep option instances inside ConfigurationI, in order to avoid duplicate instances.
            final var cellOption = instance(cellClass);
            content.put((Key) cellClass, fileSystemViaClassResources(cellClass, cellOption.groupId(), cellOption.artifactId()));
        });
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }
}
