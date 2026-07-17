/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.need;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.LogMessage;
import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.communication.log.LogMessageI.logMessage;

/**
 * This {@link RuntimeException} makes it easy, to pass error documents to the error handler,
 * while supporting arbitrary reporting languages via {@link Tree}.
 * Using this instead of {@link ExecutionException} also indicates, that this is an error suitable for users.
 * {@link LogMessage} are used, as this is basically a user facing scoped log with abortion conditions and
 * different severities of errors.
 *
 * @deprecated This is deprecated, as this {@link Exception} is only used for errors.
 * Therefore, using {@link LogMessage} does not make sense.
 * So, using {@link ExecutionException} is enough.
 */
@Deprecated
@Accessors(chain = true)
public class NeedException extends RuntimeException {
    protected static NeedException needErrorException(Tree... messages) {
        return needException(listWithValuesOf(messages).stream().map(m -> logMessage(m, LogLevel.ERROR)).toList());
    }

    protected static NeedException needException(LogMessage<Tree>... messages) {
        return needException(listWithValuesOf(messages));
    }

    protected static NeedException needException(List<LogMessage<Tree>> messages) {
        return new NeedException(messages);
    }

    @Getter private final List<LogMessage<Tree>> messages;

    private NeedException(List<LogMessage<Tree>> argMessages) {
        messages = argMessages;
    }
}
