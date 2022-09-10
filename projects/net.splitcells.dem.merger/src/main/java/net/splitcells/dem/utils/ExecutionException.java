/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

@JavaLegacyArtifact
public class ExecutionException extends RuntimeException {
    /**
     * TODO Support {@link net.splitcells.dem.lang.perspective.Perspective} as message.
     *
     * @param message This message describes reason for the exception.
     * @return This is an exception, that can be thrown in order to abort the execution.
     */
    public static ExecutionException executionException(String message) {
        return new ExecutionException(message);
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
