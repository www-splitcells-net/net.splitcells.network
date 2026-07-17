/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.object.Discoverable;

public class LogMessageI<T> implements LogMessage<T> {

    public static <T> LogMessage<T> logMessage(T content, LogLevel priority) {
        return new LogMessageI<>(content, Discoverable.EXPLICIT_NO_CONTEXT, priority);
    }

    public static <T> LogMessage<T> logMessage(T content, Discoverable context, LogLevel priority) {
        return new LogMessageI<>(content, context, priority);
    }

    private final T content;
    private final LogLevel priority;
    private final Discoverable context;

    private LogMessageI(T content, Discoverable context, LogLevel priority) {
        this.content = content;
        this.context = context;
        this.priority = priority;

    }

    @Override
    public T content() {
        return content;
    }

    @Override
    public LogLevel priority() {
        return priority;
    }

    @Override
    public ListView<String> path() {
        return context.path();
    }

}
