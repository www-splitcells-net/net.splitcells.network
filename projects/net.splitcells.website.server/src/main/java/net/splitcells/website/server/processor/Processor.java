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
package net.splitcells.website.server.processor;

import net.splitcells.dem.lang.perspective.Perspective;

import java.util.Optional;

/**
 * <p>This class is the currently the most abstract server API supported on a concept level.
 * This API allows to model all kinds of simple function calls and any kind of return value types.
 * This API was introduced, in order to support HTML/HTTP forms.</p>
 * <p>TODO Use this API for the webserver instead of {@link net.splitcells.website.server.projects.ProjectsRenderer}.</p>
 * <p>TODO IDEA The most abstract server API concept, is an API,
 * where a path and tree of binary arrays annotated with
 * an ID with a URL format and like {@link Perspective#nameSpace()} for each array is sent to a server,
 * that responses with the same kind of binary tree,
 * but without a path.
 * Such an API could model any kind of function signature natively,
 * with an high amount of adaptivity to other APIs.
 * Alternatively, the most abstract API only supports only a byte array as an argument and
 * a byte array as a result set,
 * but this makes it harder to write adapters for.
 * In other words, an API based on type binary array trees makes it easiest,
 * to create an adapter for such an API to any other API.
 * </p>
 */
public interface Processor<Source, Target> {
    /**
     * Provides binary data given binary data, which may be used directly.
     * Alternatively, this may be used in order to create an side effect.
     *
     * @param request <p>This contains the request data for which a set of data is being requested.</p>
     *                <p>The return type is not an {@link Optional}, as this simplifies the API and
     *                there is no use case for an empty answer, except for composing a {@link Processor}
     *                out of multiple sub {@link Processor} in the form of a tree of {@link Processor}.</p>
     *                <p>If the return type {@link Optional} would be used,
     *                a root processor could query all of its direct sub processors via {@link Optional#isEmpty()}
     *                in order to find the one processor, that can answer the given {@link Request}.
     *                Most importantly, the root processor could find out this way,
     *                which processor can process a given {@link Request#trail()},
     *                without maintaining a map matching {@link Request#trail()}
     *                to the corresponding {@link Processor}.</p>
     *                <p>If the return type is not {@link Optional},
     *                one could assume, that creating a processor is much harder.
     *                Especially, when the set of supported {@link Request#trail()} changes,
     *                as every meta processor would have to manage and update
     *                a map of supported {@link Request#trail()} to it's processors.
     *                This is not the case, because the method for creating the list of all supported paths,
     *                which is required for the complete website rendering,
     *                can also be used, in order to dynamically determine,
     *                which processor supports which {@link Request#trail()}.</p>
     *                <p>Therefore, an {@link Optional} is not required and does not make the API more future proof.</p>
     *                <p>The type of data itself is generic, so it is possible to chain processors.
     *                For now {@link Perspective} is preferred as default type,
     *                because it can hold arbitrary formatted String structures and
     *                binary data can be stored as base64 as well.</p>
     * @return This is the set of requested binary data.
     */
    Response<Target> process(Request<Source> request);
}
