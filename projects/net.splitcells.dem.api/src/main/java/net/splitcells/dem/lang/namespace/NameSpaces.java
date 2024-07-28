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

import net.splitcells.dem.lang.perspective.Perspective;

import java.util.Optional;

import static net.splitcells.dem.lang.namespace.NameSpace.nameSpace;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class NameSpaces {
    public static final NameSpace FODS_OFFICE = nameSpace("office",
            "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
    public static final NameSpace FODS_STYLE = nameSpace("style",
            "urn:oasis:names:tc:opendocument:xmlns:style:1.0");
    public static final NameSpace FODS_FO = nameSpace("fo",
            "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0");
    public static final NameSpace FODS_FO_ATTRIBUTE = nameSpace("fo",
            "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0", Optional.of(true));
    public static final NameSpace FODS_TABLE = nameSpace("table",
            "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
    public static final NameSpace FODS_TEXT = nameSpace("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
    @Deprecated
    public static final NameSpace TEXT = nameSpace("nt", "http://splitcells.net/text.xsd");
    public static final NameSpace STRING = nameSpace("str", "http://splitcells.net/string.xsd");
    public static final NameSpace NAME_SPACE = nameSpace("ns", "http://splitcells.net/namespace.xsd");
    public static final NameSpace DEN = nameSpace("d", "http://splitcells.net/den.xsd");
    public static final NameSpace GEL = nameSpace("d", "http://splitcells.net/gel.xsd");
    public static final NameSpace SEW = nameSpace("s", "http://splitcells.net/sew.xsd");

    public static final NameSpace HTML = nameSpace("html", "http://www.w3.org/1999/xhtml");
    public static final String LINK = "link";
    public static final String URL = "url";
    public static final NameSpace NATURAL = nameSpace("n", "http://splitcells.net/natural.xsd");
    public static final String VAL = "val";
    public static final String NAME = "name";
    public static final NameSpace XLINK = nameSpace("xl", "http://www.w3.org/1999/xlink");
    public static final String HREF = "href";

    private NameSpaces() {
        throw constructorIllegal();
    }

    public static Perspective string(String value) {
        return perspective(value, STRING);
    }
}
