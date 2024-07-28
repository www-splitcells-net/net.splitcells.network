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
package net.splitcells.dem.lang.namespace;

import net.splitcells.dem.environment.config.framework.Option;

import java.util.Optional;

public class NameSpace {

    public static NameSpace nameSpace(String prefix, String uri) {
        return new NameSpace(prefix, uri, Optional.empty());
    }

    public static NameSpace nameSpace(String prefix, String uri, Optional<Boolean> isXmlAttributeArg) {
        return new NameSpace(prefix, uri, isXmlAttributeArg);
    }

    private final String uri;
    private final String prefix;
    /**
     * TODO Maybe something like a generic tag system for perspectives would be a better fit.
     */
    private Optional<Boolean> isXmlAttribute;

    private NameSpace(String prefix, String uri, Optional<Boolean> isXmlAttributeArg) {
        this.uri = uri;
        this.prefix = prefix;
        isXmlAttribute = isXmlAttributeArg;
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

    public Optional<Boolean> isXmlAttribute() {
        return isXmlAttribute;
    }
}
