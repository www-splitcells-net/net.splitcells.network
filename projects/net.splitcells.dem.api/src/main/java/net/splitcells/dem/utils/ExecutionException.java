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
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.perspective.Perspective;

@JavaLegacyArtifact
public class ExecutionException extends RuntimeException {

    public static ExecutionException unsupportedOperation() {
        return executionException("Unsupported operation");
    }

    /**
     * TODO Support {@link net.splitcells.dem.lang.perspective.Perspective} as message.
     *
     * @param message This message describes reason for the exception.
     * @return This is an exception, that can be thrown in order to abort the execution.
     */
    public static ExecutionException executionException(String message) {
        return new ExecutionException(message);
    }

    public static ExecutionException executionException(Perspective message) {
        return new ExecutionException(message.toXmlString());
    }

    public static ExecutionException executionException(Perspective message, Throwable t) {
        return new ExecutionException(message.toXmlString(), t);
    }

    public static ExecutionException executionException(String message, Throwable t) {
        return new ExecutionException(message, t);
    }

    private ExecutionException(String message, Throwable t) {
        super(message, t);
    }

    public static ExecutionException executionException(Throwable t) {
        return new ExecutionException(t);
    }

    private ExecutionException(String message) {
        super(message);
    }

    private ExecutionException(Throwable t) {
        super(t);
    }
}
