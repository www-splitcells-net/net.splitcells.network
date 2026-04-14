/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.Consumer;

/**
 * Provides access to existing data for authenticated and authorized users.
 * 
 * @param <T>
 */
public interface AccessProvider<T> {
    static <R> AccessProvider<R> unsecuredStaticAccessSession(R staticValue) {
        return new AccessProvider<>() {

            @Override public void access(Consumer<R> accessor, UserSession userSession) {
                accessor.accept(staticValue);
            }

            @Override public void access(Consumer<R> accessor, UserSession userSession, String lifeCycleId) {
                accessor.accept(staticValue);
            }
        };
    }

    /**
     * <p>Provides access to a typed value for the duration of the accessor's {@link Consumer#accept(Object)}.</p>
     * <p>After that, this class may or may not invalidate the value given to the {@link Consumer}.
     * The implementor decides, what is done in this regard,
     * but it should comply with the overall security requirements.</p>
     *
     * @param accessor
     */
    void access(Consumer<T> accessor, UserSession userSession);

    void access(Consumer<T> accessor, UserSession userSession, String lifeCycleId);
}
