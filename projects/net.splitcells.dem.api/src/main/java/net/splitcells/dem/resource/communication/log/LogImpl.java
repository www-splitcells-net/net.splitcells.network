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
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.Sender;

import java.util.function.Predicate;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class LogImpl implements Log {

    private static final boolean ENABLE_EXPERIMENTAL_XML_RENDERING = true;

    public static LogImpl logBasedOnPerspective(Sender<String> output, Predicate<LogMessage<Perspective>> messageFilter) {
        return new LogImpl(output, messageFilter);
    }

    private final Sender<String> output;
    private final Predicate<LogMessage<Perspective>> messageFilter;

    private LogImpl(Sender<String> output, Predicate<LogMessage<Perspective>> messageFilter) {
        this.output = output;
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Perspective>>> R append(LogMessage<Perspective> arg) {
        if (messageFilter.test(arg)) {
            print(output, arg.content());
        }
        return (R) this;
    }

    private static void print(Sender<String> output, Perspective content, String prefix) {
        if (ENABLE_EXPERIMENTAL_XML_RENDERING) {
            output.append(content.toXmlString());
        } else {
            if (content.children().size() == 1) {
                if (prefix.isEmpty()) {
                    print(output, content.children().get(0), content.name());
                } else {
                    print(output, content.children().get(0), prefix + "." + content.name());
                }
                return;
            } else if (content.children().size() > 0) {
                output.append(prefix + content.name() + ":");
            } else if (content.children().size() == 0) {
                if (prefix.isEmpty()) {
                    output.append(content.name());
                } else {
                    output.append(prefix + "." + content.name());
                }
                return;
            } else {
                throw new UnsupportedOperationException();
            }
            content.children().forEach(child -> {
                print(Sender.extend(output, "\t", ""), child);
            });
        }
    }

    private static void print(Sender<String> output, Perspective content) {
        print(output, content, "");
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
