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

import net.splitcells.dem.data.set.AppendableSet;
import net.splitcells.dem.data.set.list.AppendableList;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacy;

public interface Sender<T> extends AppendableList<T>, Resource {

    @JavaLegacy
    static Sender<String> stringSender(java.io.OutputStream output) {
        return new Sender<String>() {

            @Override
            public void close() {
                try {
                    output.close();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private java.io.PrintWriter printer = new java.io.PrintWriter(output);

            @Override
            public <R extends AppendableList<String>> R append(String arg) {
                /* The new line ending added by println at the end of the argument is
                 * dependent on the current operation system.
                 * To make the program portable, the line ending is done via a constant.
                 */
                printer.print(arg + "\n");
                printer.flush();
                return (R) this;
            }

            @Override
            public void flush() {
                printer.flush();
            }

            @Override
            public <R extends AppendableSet<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Creates a {@link Sender}, that does not close its underlying {@link java.io.OutputStream}.
     * This is mainly used for the original {@link System#out} etc.,
     * as closing these just creates problems.
     *
     * @param output
     * @return
     */
    @JavaLegacy
    static Sender<String> stringSenderWithoutClosing(java.io.OutputStream output) {
        return new Sender<String>() {

            @Override
            public void close() {
                // Nothing needs to be done here.
            }

            private java.io.PrintWriter printer = new java.io.PrintWriter(output);

            @Override
            public <R extends AppendableList<String>> R append(String arg) {
                printer.println(arg);
                printer.flush();
                return (R) this;
            }

            @Override
            public void flush() {
                printer.flush();
            }

            @Override
            public <R extends AppendableSet<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * RENAME
     */
    static Sender<String> extend(Sender<String> sender, String prefix, String suffix) {
        return new Sender<>() {

            @Override
            public void close() {
                sender.close();
            }

            @Override
            public void flush() {
                sender.flush();
            }

            @SuppressWarnings("unchecked")
            @Override
            public <R extends AppendableList<String>> R append(String value) {
                sender.append(prefix + value + suffix);
                return (R) this;
            }

            @Override
            public <R extends AppendableSet<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    static Sender<String> extend(Sender<String> sender, String prefix, String suffix, String firstPrefix, String firstSuffix) {
        return new Sender<>() {

            boolean isFirst = true;

            @Override
            public void close() {
                sender.close();
            }

            @Override
            public void flush() {
                sender.flush();
            }

            @SuppressWarnings("unchecked")
            @Override
            public <R extends AppendableList<String>> R append(String value) {
                if (isFirst) {
                    sender.append(firstPrefix + value + firstSuffix);
                } else {
                    sender.append(prefix + value + suffix);
                }
                return (R) this;
            }

            @Override
            public <R extends AppendableSet<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
