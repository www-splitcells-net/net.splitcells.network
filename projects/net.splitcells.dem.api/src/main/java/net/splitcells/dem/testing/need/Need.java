/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.need;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.LogMessage;

import java.util.Optional;

/**
 *
 * @param <T> This is the type of thing to be checked.
 */
@FunctionalInterface
public interface Need<T> {
    /**
     * @param arg This is the things, that should comply with this {@link Need}.
     * @return Returns an {@link List#isEmpty()}, when arg complies with this {@link Need}.
     * Otherwise, arbitrary formatted error messages are returned.
     * {@link LogLevel#INFO} is potentially relevant info to an error in this context.
     */
    List<LogMessage<Tree>> checkCompliance(T arg);
}
