/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.resource.communication;

import net.splitcells.dem.data.set.SetWA;
import net.splitcells.dem.data.set.list.ListWA;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface Sender<T> extends ListWA<T>, Flushable, Closeable {

    static Sender<String> stringSender(OutputStream output) {
        return new Sender<String>() {

            @Override
            public void close() {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private PrintWriter printer = new PrintWriter(output);

            @Override
            public <R extends ListWA<String>> R append(String arg) {
                printer.println(arg);
                printer.flush();
                return (R) this;
            }

            @Override
            public void flush() {
                printer.flush();
            }

            @Override
            public <R extends SetWA<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    static Sender<String> stringSenderWithoutClosing(OutputStream output) {
        return new Sender<String>() {

            @Override
            public void close() {
            }

            private PrintWriter printer = new PrintWriter(output);

            @Override
            public <R extends ListWA<String>> R append(String arg) {
                printer.println(arg);
                printer.flush();
                return (R) this;
            }

            @Override
            public void flush() {
                printer.flush();
            }

            @Override
            public <R extends SetWA<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * RENAME
     */
    static Sender<String> extend(Sender<String> sender, String prefix, //
                                 String suffix) {
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
            public <R extends ListWA<String>> R append(String value) {
                sender.append(prefix + value + suffix);
                return (R) this;
            }

            @Override
            public <R extends SetWA<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    static Sender<String> extend(Sender<String> sender, String prefix, //
                                 String suffix, String first_prefix, String first_suffix) {
        return new Sender<>() {

            boolean is_first = true;

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
            public <R extends ListWA<String>> R append(String value) {
                if (is_first) {
                    sender.append(first_prefix + value + first_suffix);
                } else {
                    sender.append(prefix + value + suffix);
                }
                return (R) this;
            }

            @Override
            public <R extends SetWA<String>> R add(String value) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
