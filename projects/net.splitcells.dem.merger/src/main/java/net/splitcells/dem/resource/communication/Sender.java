/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.communication;

import net.splitcells.dem.data.set.SetWA;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

public interface Sender<T> extends ListWA<T>, Resource {

    @JavaLegacyBody
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

    @JavaLegacyBody
    static Sender<String> stringSenderWithoutClosing(java.io.OutputStream output) {
        return new Sender<String>() {

            @Override
            public void close() {
            }

            private java.io.PrintWriter printer = new java.io.PrintWriter(output);

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

    static Sender<String> extend(Sender<String> sender, String prefix, String suffix, String first_prefix, String first_suffix) {
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
