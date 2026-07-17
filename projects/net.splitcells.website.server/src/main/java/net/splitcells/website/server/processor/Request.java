/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.website.server.processor.RequestImpl.requestImpl;

/**
 * This is the base class for all requests.
 * In the worst case scenario, the requests are user input or even comes from a public source.
 * Always treat such requests as potentially malicious.
 *
 * @param <T> This is the data, that is provided as additional arguments for the requests.
 */
public interface Request<T> {
    static <T> Request<T> request(Trail trail, T data) {
        return requestImpl(trail, data);
    }

    static <T> Request<T> request(Trail trail, T data, UserSession userSession) {
        return requestImpl(trail, data, userSession);
    }

    T data();

    Trail trail();

    UserSession userSession();
}
