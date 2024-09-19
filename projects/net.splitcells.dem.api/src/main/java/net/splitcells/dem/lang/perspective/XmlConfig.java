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
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.object.DeepCloneable2;

public class XmlConfig implements DeepCloneable2<XmlConfig> {
    public static XmlConfig xmlConfig() {
        return new XmlConfig();
    }

    private boolean printNameSpaceAttributeAtTop = true;
    private boolean printXmlDeclaration = true;

    private XmlConfig() {

    }

    public boolean printNameSpaceAttributeAtTop() {
        return printNameSpaceAttributeAtTop;
    }

    public XmlConfig withPrintNameSpaceAttributeAtTop(boolean arg) {
        printNameSpaceAttributeAtTop = arg;
        return this;
    }

    public boolean printXmlDeclaration() {
        return printXmlDeclaration;
    }

    public XmlConfig withPrintXmlDeclaration(boolean arg) {
        printXmlDeclaration = arg;
        return this;
    }

    @Override
    public XmlConfig deepClone() {
        return xmlConfig()
                .withPrintNameSpaceAttributeAtTop(printNameSpaceAttributeAtTop)
                .withPrintXmlDeclaration(printXmlDeclaration);
    }
}
