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
package net.splitcells.website.server.security.access;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.utils.lambdas.TriConsumer;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.website.server.security.authentication.Authentication.anonymous;

/**
 * <p>Provides authenticated and authorized access to existing things.</p>
 * <p>TODO Extend interface or builder in such a way, that authentication rules can be created.
 * Although known as defining which roles, can access a subject.</p>
 * <p>TODO Consider using the {@link UserSession} that is stored at a new {@link Dem#config()}.
 * In other words, for every user access a child {@link Dem} would have to be created,
 * where {@link Dem#config()} is the same as the parent one,
 * but contains the current {@link UserSession}.
 * The advantage of this, is the fact, that a {@link UserSession} always exists and
 * is harder to accidentally swap with other {@link UserSession},
 * which causes access rights escalations.</p>
 * <p>TODO Create an interface for code, that is executed in an not authenticated environment and
 * than transitions to an authenticated environment.
 * An authenticated environment is where the current user is determined via {@link Dem#configValue(Class)},
 * which also needs to be implemented.
 * In other words, all code is assumed to be executed in an authenticated environment except for the code,
 * whose job is to authenticate requests.</p>
 *
 * @param <T> This is the type of object, whose access is controlled via {@link UserSession}.
 *            The instance of these themselves are responsible to only allow things,
 *            that the {@link UserSession} is allowed to do.
 */
public interface AccessControl<T> {

    /**
     * Provides a {@link T}, that is encapsulated in this, to the given running action.
     * During the run the {@link UserSession} is {@link Authenticator#isValid(UserSession)}
     * as long as the given action is running.
     * The provided {@link T} should only be valid while running the given action and
     * after that {@link AutoCloseable#close()} has to be executed.
     *
     * @param action This is an action, that requires a {@link UserSession} for authorization.
     * @param login  This is the {@link Login} used, in order to create a {@link UserSession} for authorization.
     */
    void access(BiConsumer<UserSession, T> action, Login login);

    /**
     * This is the helper method, in case the creation time of the {@link UserSession} cannot full be controlled,
     * by this {@link AccessControl}.
     * Keep in mind, that after the access, the user session will be invalidated.
     *
     * @param action      This is an action, that requires a {@link UserSession} for authorization.
     * @param userSession This object authorizes the access for the action.
     */
    default void access(BiConsumer<UserSession, T> action, UserSession userSession) {
        access(t -> action.accept(userSession, t), userSession);
    }

    /**
     *
     * @param action
     * @param userSession
     * @param lifeCycleId The caller wants to get data authorized for the userSession, that was provided
     *                    by the same {@link UserSession}, but with a different lifeCycleId.
     *                    If this implementation of the {@link UserSession} does not allow or support it,
     *                    the data of the different {@link Authenticator#lifeCycleId(UserSession)} is not provided silently.
     */
    default void access(BiConsumer<UserSession, T> action, UserSession userSession, String lifeCycleId) {
        access(t -> action.accept(userSession, t), userSession, lifeCycleId);
    }

    /**
     * <p>Provides access to a typed value for the duration of the accessor's {@link Consumer#accept(Object)}.</p>
     * <p>After that, this class may or may not invalidate the value given to the {@link Consumer}.
     * The implementor decides, what is done in this regard,
     * but it should comply with the overall security requirements.</p>
     *
     * @param action
     * @deprecated I think using the {@link BiConsumer} versions would be better,
     * as this makes it more obvious, when it is forgotten to pass {@link UserSession} to the consuming code.
     */
    void access(Consumer<T> action, UserSession userSession);

    /**
     *
     * @param action
     * @param userSession
     * @param lifeCycleId The caller wants to get data authorized for the userSession, that was provided
     *                    by the same {@link UserSession}, but with a different lifeCycleId.
     *                    If this implementation of the {@link UserSession} does not allow or support it,
     *                    the data of the different {@link Authenticator#lifeCycleId(UserSession)} is not provided silently.
     * @deprecated I think using the {@link BiConsumer} versions would be better,
     * as this makes it more obvious, when it is forgotten to pass {@link UserSession} to the consuming code.
     */
    void access(Consumer<T> action, UserSession userSession, String lifeCycleId);

    /**
     *
     * @param processor   The processor should never return the argument or part of it,
     *                    that is still mutually connected to the argument.
     * @param userSession
     * @param lifeCycleId The caller wants to get data authorized for the userSession, that was provided
     *                    by the same {@link UserSession}, but with a different lifeCycleId.
     *                    If this implementation of the {@link UserSession} does not allow or support it,
     *                    the data of the different {@link Authenticator#lifeCycleId(UserSession)} is not provided silently.
     * @param <R>
     * @return The result of the processor is returned.
     * @deprecated I think using a {@link TriConsumer} version would be better,
     * as this makes it more obvious, when it is forgotten to pass {@link UserSession} to the consuming code..
     */
    <R> R process(Function<T, R> processor, UserSession userSession, String lifeCycleId);
}
