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
package net.splitcells.dem.object;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.ProgramRepresentative;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

/**
 * <p>Provides a path for the object, that implements the interface.
 * The overarching idea behind this interface is to provide discovery paths for everything and not just Java objects.
 * Thereby everything has a path based identifier,
 * in order to support their discoverability and to enable a basic organisational structure for semantics.
 * This leads to a global semantic path system, that is an inventory of everything.</p>
 * <p>This makes the object identifiable in a {@link net.splitcells.dem.resource.FileSystem}
 * or the webserver.
 * It also can be used, in order to organize shell command names etc.</p>
 * <p>IDEA Provide a tree structure, that ensures, that every {@link Discoverable} has a unique path and
 * in order to get better performance,
 * when an object is searched by a path prefix.</p>
 */
public interface Discoverable {

    /**
     * This is basically the null value of {@link Discoverable} and states,
     * that the respective object is not part of a {@link Discoverable} tree.
     */
    Discoverable NO_CONTEXT = () -> list();

    /**
     *
     * @return Returns true, if there is no path, which means,
     * that there is semantically no parent or owning element for this {@link Discoverable}.
     * Such objects can be considered to be temporary objects,
     * that most of the time are not integrated in persistent data structures.
     */
    default boolean isNoContext() {
        return path().isEmpty();
    }

    static Discoverable discoverable(List<String> path) {
        return () -> path;
    }

    static Discoverable discoverable(Class<?> clazz) {
        return () -> list(simplifiedName(clazz).split("\\."));
    }

    /**
     * TODO Use {@link net.splitcells.dem.data.set.list.ListView}.
     *
     * @return
     */
    List<String> path();

    default Discoverable child(List<String> extension) {
        final var rThis = this;
        return new Discoverable() {
            @Override
            public List<String> path() {
                return rThis.path().shallowCopy().withAppended(extension);
            }
        };
    }

    /**
     * @return Returns all metadata.
     * The {@link Discovery#value(Class)} is this and {@link #path()} and {@link Discovery#path()} are equals.
     * If the returned value is not {@link Optional#empty()}, than it is ensured, that {@link #path()} is unique
     * in the {@link Discovery} tree.
     */
    default Optional<Discovery> discovery() {
        return Optional.empty();
    }
}
