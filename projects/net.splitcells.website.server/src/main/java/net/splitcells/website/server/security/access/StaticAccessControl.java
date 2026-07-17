/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;

/**
 * <p>Provides access to a single mutable object.</p>
 * <p>TODO Implement a wrapper, that allows one to make given access invalid.
 * For that T probably would need to implement something like a closeable clone.</p>
 *
 * @param <T> The type of value being provided.
 */
public class StaticAccessControl<T> implements AccessControl<T> {
    public static <R> AccessControl<R> staticAccessControl(R argSubject) {
        return new StaticAccessControl<>(configValue(Authentication.class), argSubject);
    }

    public static <R> AccessControl<R> staticAccessControl(Authenticator authenticator, R argSubject) {
        return new StaticAccessControl<>(authenticator, argSubject);
    }

    private final Authenticator authenticator;
    private final T subject;

    private StaticAccessControl(Authenticator argAuthenticator, T argSubject) {
        authenticator = argAuthenticator;
        subject = argSubject;
    }

    @Override public void access(BiConsumer<UserSession, T> action, Login login) {
        access(action, authenticator.userSession(login));
    }

    @Override public void access(BiConsumer<UserSession, T> action, UserSession userSession) {
        try {
            action.accept(userSession, subject);
        } finally {
            authenticator.endSession(userSession);
        }
    }

    @Override public void access(BiConsumer<UserSession, T> action, UserSession userSession, String lifeCycleId) {
        try {
            action.accept(userSession, subject);
        } finally {
            authenticator.endSession(userSession);
        }
    }

    @Override public void access(Consumer<T> action, UserSession userSession) {
        try {
            action.accept(subject);
        } finally {
            authenticator.endSession(userSession);
        }
    }

    @Override public void access(Consumer<T> action, UserSession userSession, String lifeCycleId) {
        try {
            action.accept(subject);
        } finally {
            authenticator.endSession(userSession);
        }
    }

    @Override public <R> R process(Function<T, R> processor, UserSession userSession, String lifeCycleId) {
        try {
            return processor.apply(subject);
        } finally {
            authenticator.endSession(userSession);
        }
    }
}
