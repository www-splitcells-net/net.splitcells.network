/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.AppendableList;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.Sender;

import java.util.function.Predicate;

/**
 * <p>Writes logs in a format, that is suitable for servers:
 * every message takes one line in the logs.
 * This makes the logs easier to query with simple tools, when compared to logs with multi line messages.</p>
 */
public class ServerLogger implements Logger {
    public static Logger serverLog(Sender<String> output, Predicate<LogMessage<Tree>> messageFilter) {
        return new ServerLogger(output, messageFilter);
    }

    private final Sender<String> output;
    private final Predicate<LogMessage<Tree>> messageFilter;

    private ServerLogger(Sender<String> output, Predicate<LogMessage<Tree>> messageFilter) {
        this.output = output;
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends AppendableList<LogMessage<Tree>>> R append(LogMessage<Tree> arg) {
        if (messageFilter.test(arg)) {
            output.append(arg.content().createToJsonPrintable().toJsonString());
        }
        return (R) this;
    }

    @Override
    public void close() {
        output.close();
    }

    @Override
    public void flush() {
        output.flush();
    }
}
