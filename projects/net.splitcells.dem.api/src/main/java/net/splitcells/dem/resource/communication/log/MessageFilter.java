/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;
import java.util.function.Predicate;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;

/**
 * TODO RENAME This is not a filter but a selector.
 * <p>
 * If a {@link LogMessage} is true according to this predicate,
 * it should be printed according to this predicate.
 */
public class MessageFilter extends OptionImpl<Predicate<LogMessage<Tree>>> {
    public MessageFilter() {
        super(() -> logMessage -> logMessage.priority().greaterThan(DEBUG));
    }

    @Override public Optional<Tree> serialize(Predicate<LogMessage<Tree>> currentValue) {
        return Optional.empty();
    }
}
