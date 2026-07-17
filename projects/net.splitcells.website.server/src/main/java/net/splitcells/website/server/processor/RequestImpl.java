/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.website.server.security.authentication.Authentication.anonymous;

/**
 * This is the base class for all requests.
 * In the worst case scenario, the requests are user input or even comes from a public source.
 * Always treat such requests as potentially malicious.
 *
 * @param <T> This is the data, that is provided as additional arguments for the requests.
 */
public class RequestImpl<T> implements Request<T> {
    public static <T> RequestImpl<T> requestImpl(Trail trail, T data) {
        return new RequestImpl<>(trail, data, anonymous());
    }

    public static <T> RequestImpl<T> requestImpl(Trail trail, T data, UserSession argUserSession) {
        return new RequestImpl<>(trail, data, argUserSession);
    }

    private Trail trail;
    private final T data;
    private final UserSession userSession;

    private RequestImpl(Trail trailArg, T dataArg, UserSession argUserSession) {
        trail = trailArg;
        data = dataArg;
        userSession = argUserSession;
    }

    @Override public T data() {
        return data;
    }

    @Override public Trail trail() {
        return trail;
    }

    @Override public UserSession userSession() {
        return userSession;
    }
}
