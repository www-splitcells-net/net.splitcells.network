/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.client;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.testing.Assertions;

import java.util.function.Function;

public interface Tab extends Closeable {
    /**
     * <p>Reasonable maximum amount of time for a user to wait to get a result.</p>
     * <p>TODO Maybe this number could be decreased.</p>
     */
    final long MILLISECOND_MAX_WAIT_TIME = 60_000L;
    Element elementByClass(String cssClass);

    Element elementById(String id);

    /**
     * @param checker
     * @return Waits at most {@link #MILLISECOND_MAX_WAIT_TIME} milliseconds until the {@code checker} returns true. 
     * Returns this or throws an exception, if the max wait time was reached..
     */
    default Tab waitUntilRequirementIsTrue(Function<Tab, Boolean> checker) {
        Assertions.waitUntilRequirementIsTrue(MILLISECOND_MAX_WAIT_TIME, () -> checker.apply(this));
        return this;
    }
}
