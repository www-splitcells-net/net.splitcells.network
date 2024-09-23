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
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;

import java.util.Optional;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.communication.log.LogMessageI.logMessage;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.TODO_NOT_IMPLEMENTED_YET;
import static net.splitcells.dem.utils.StringUtils.throwableToString;

/**
 * <p>TODO Render stack traces as one line optionally.
 * Maybe this should be somehow be optionally supported for
 * messages consisting of multiple lines.
 * In other words, this may be should be implemented as a general
 * functionality in order to provide one message one line logs.</p>
 */
public interface Log extends ListWA<LogMessage<Perspective>> {

    default Log append(String name) {
        return append(logMessage(perspective(name), NO_CONTEXT, LogLevel.DEBUG));
    }

    default Log append(String message, LogLevel logLevel) {
        return append(logMessage(perspective(message), NO_CONTEXT, logLevel));
    }

    /**
     * Mark code locations for optional functionality and makes it possible to detect,
     * whether such optional functionality would be used,
     * if it was present.
     *
     * @param clazz Points to the class with missing implementation.
     *              This is useful for default implementations.
     * @return this
     */
    @ReturnsThis
    @JavaLegacyBody
    default Log appendUnimplementedWarning(Class<?> clazz) {
        logs().append("Unimplemented program part for class "
                        + clazz
                        + " at:\n"
                        + throwableToString(executionException("no-message"))
                , LogLevel.WARNING);
        return this;
    }

    default Log append(Domable domable, LogLevel logLevel) {
        return append(logMessage(domable.toPerspective(), NO_CONTEXT, logLevel));
    }

    default Log append(Perspective perspective, LogLevel logLevel) {
        return append(logMessage(perspective, NO_CONTEXT, logLevel));
    }

    default Log append(Domable content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context, logLevel));
    }

    default Log append(Domable content, Optional<Discoverable> context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context.orElse(NO_CONTEXT), logLevel));
    }

    default Log append(Perspective content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content, context, logLevel));
    }

    @JavaLegacyBody
    default Log appendError(Throwable throwable) {
        try {
            final var error = perspective("error");
            if (throwable.getMessage() != null) {
                error.withProperty("message", throwable.getMessage());
            }
            error.withProperty("stack-trace", throwableToString(throwable));
            if (throwable.getCause() != null) {
                final var cause = perspective("cause");
                if (throwable.getCause().getMessage() != null) {
                    cause.withProperty("message", throwable.getCause().getMessage());
                }
                cause.withProperty("stack-trace", throwableToString(throwable.getCause()));
                error.withChild(cause);
            }
            return append(logMessage(error, NO_CONTEXT, LogLevel.CRITICAL));
        } catch (Throwable th) {
            // This is a fallback, if the error could not be logged.
            throwable.printStackTrace();
            throw th;
        }
    }

    @JavaLegacyBody
    /**
     * Warnings are errors, where an action is not required.
     * <p>
     * This is typically used in order to find out,
     * if a certain usage of the code executes code,
     * which is not fully implemented and where therefore the functionality is not complete.
     * {@link Throwable}s are used, so that it is obvious,
     * where the related code is and to identify the caller.
     *
     * @param throwable
     * @return
     */
    default Log appendWarning(Throwable throwable) {
        final var warning = perspective("warning");
        warning.withProperty("message", throwable.getMessage());
        warning.withProperty("stack-trace", throwableToString(throwable));
        return append(logMessage(warning, NO_CONTEXT, LogLevel.CRITICAL));
    }

    /**
     * TODO TOFIX This does not output anything during tests.
     *
     * @param warning
     * @return
     */
    default Log appendWarning(Perspective warning) {
        final var exception = executionException("warning");
        final var message = perspective("warning");
        message.withProperty("message", message);
        message.withProperty("stack-trace", throwableToString(exception));
        return append(logMessage(warning, NO_CONTEXT, LogLevel.CRITICAL));
    }


    default Log appendWarning(String message, Throwable throwable) {
        return appendWarning(perspective(message), throwable);
    }

    default Log appendWarning(Perspective message, Throwable throwable) {
        final var throwablePerspective = perspective("throwable");
        throwablePerspective.withProperty("message", throwable.getMessage());
        throwablePerspective.withProperty("stack-trace", throwableToString(throwable));
        return append(logMessage(message.withChild(throwablePerspective), NO_CONTEXT, LogLevel.CRITICAL));
    }
}
