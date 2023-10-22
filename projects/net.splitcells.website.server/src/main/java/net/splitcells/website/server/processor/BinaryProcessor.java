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
import net.splitcells.website.server.Server;

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
public interface BinaryProcessor {
    BinaryResponse process(BinaryRequest request);
}
