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
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.Sender;

import java.util.function.Predicate;

import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.utils.TimeUtils.currentLocalTime;

/**
 * <p>TODO Print each stack strace only once in log, in order to not clutter the log.
 * If a stack trace is logged a second time an appropriate anchor link should be created.
 * Only cache a limited number of stack traces.
 * Avoid using hashes for caching stack traces in order to rule out low probability hash conflicts.
 * </p>
 * <p>This is a user friendly logger, that stores it's log as CommonMark document.
 * Such a log also looks nice in issue on platforms like SourceHut or GitHub and
 * probably also looks nice for not technical users.</p>
 */
public class CommonMarkLogger implements Logger {
    public static Logger commonMarkDui(Sender<String> output, Predicate<LogMessage<Tree>> messageFilter) {
        return new CommonMarkLogger(output, messageFilter);
    }

    private final Sender<String> output;
    private final Predicate<LogMessage<Tree>> messageFilter;

    private CommonMarkLogger(Sender<String> output, Predicate<LogMessage<Tree>> messageFilter) {
        this.output = output;
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Tree>>> R append(LogMessage<Tree> arg) {
        if (messageFilter.test(arg)) {
            perspective(currentLocalTime() + ": " + arg.content().name())
                    .withChildren(arg.content().children()).printCommonMarkString(output);
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
