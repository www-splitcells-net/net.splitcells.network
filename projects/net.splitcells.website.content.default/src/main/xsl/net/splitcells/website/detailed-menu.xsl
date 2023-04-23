<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:ns="http://splitcells.net/namespace.xsd">
    <!--
        SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
        SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <xsl:variable name="net-splitcells-website-server-config-menu-detailed">
        <xsl:variable name="content">
            <s:chapter>
                <s:title>Sections</s:title>
                <a class="net-splitcells-button net-splitcells-component-priority-3">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/info/contact.html')"/>
                    </xsl:attribute>
                    Contact
                </a>
                <a class="net-splitcells-button net-splitcells-component-priority-3">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:root-relative-url('/legal/licensing.html')"/>
                    </xsl:attribute>
                    Licensing
                </a>
                <a class="net-splitcells-button net-splitcells-component-priority-3">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:root-relative-url('/index.html')"/>
                    </xsl:attribute>
                    Main Page
                </a>
                <a class="net-splitcells-button net-splitcells-component-priority-3">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:root-relative-url('/legal/privacy-policy.html')"/>
                    </xsl:attribute>
                    Privacy Policy
                </a>
            </s:chapter>
        </xsl:variable>
        <xsl:apply-templates select="$content"/>
    </xsl:variable>
</xsl:stylesheet>