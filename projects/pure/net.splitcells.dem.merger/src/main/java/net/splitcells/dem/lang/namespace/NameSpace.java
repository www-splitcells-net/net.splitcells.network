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
package net.splitcells.dem.lang.namespace;

public class NameSpace {

    public static NameSpace nameSpace(String prefix, String uri) {
        return new NameSpace(prefix, uri);
    }

    private final String uri;
    private final String prefix;

    private NameSpace(String prefix, String uri) {
        this.uri = uri;
        this.prefix = prefix;

    }

    public String uri() {
        return uri;
    }

    public String defaultPrefix() {
        return prefix;
    }

    public String prefixedName(String name) {
        return prefix + ":" + name;
    }
}
