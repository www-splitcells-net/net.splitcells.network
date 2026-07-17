/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.tree;

import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.object.DeepCloneable2;

public class XmlConfig implements DeepCloneable2<XmlConfig> {
    public static XmlConfig xmlConfig() {
        return new XmlConfig();
    }

    private boolean printNameSpaceAttributeAtTop = true;
    private boolean printXmlDeclaration = true;
    private NameSpace defaultNameSpace = NameSpaces.STRING;

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
                .withPrintXmlDeclaration(printXmlDeclaration)
                .withDefaultNameSpace(defaultNameSpace);
    }

    public NameSpace defaultNameSpace() {
        return this.defaultNameSpace;
    }

    public XmlConfig withDefaultNameSpace(NameSpace arg) {
        defaultNameSpace = arg;
        return this;
    }
}
