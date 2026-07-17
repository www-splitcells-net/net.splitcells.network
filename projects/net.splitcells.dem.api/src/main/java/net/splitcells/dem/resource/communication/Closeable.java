/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
