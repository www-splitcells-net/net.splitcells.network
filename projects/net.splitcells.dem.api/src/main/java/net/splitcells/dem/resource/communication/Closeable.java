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
package net.splitcells.dem.resource.communication;

import lombok.val;

import java.util.function.Supplier;

import static net.splitcells.dem.utils.ExecutionException.execException;

public interface Closeable extends AutoCloseable {

    /**
     * Used in order to circumvent checked exceptions to {@link AutoCloseable#close}.
     */
    void close();

    /**
     * This exists in order to {@link AutoCloseable#close()} a resource in tests,
     * which tests the creation of invalid {@link AutoCloseable}
     * without creating code warnings or empty code blocks, that are never covered by tests.
     * Normally {@link Supplier#get()} will throw an {@link Exception},
     * when the test is successful.
     * When the test fails {@link Supplier#get()} will not throw an {@link Exception} and
     * the {@link AutoCloseable} has to be closed.
     * 
     * @param supplier
     */
    static void close(Supplier<AutoCloseable> supplier) {
        try (val closeable = supplier.get()) {
            return;
        } catch (Throwable e) {
            throw execException(e);
        }
    }
}
