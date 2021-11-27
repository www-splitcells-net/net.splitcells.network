<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd"
                xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ln="http://splitcells.net/local-namespace.xsd">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <xsl:template match="node()" mode="content.outline">
        <xsl:apply-templates select="./node()" mode="content.outline"/>
    </xsl:template>
    <xsl:template match="s:chapter" mode="content.outline">
        <li class="">
            <a>
                <xsl:attribute name="href" select="concat('#', generate-id(.))"/>
                <xsl:value-of select="./s:title/node()"/>
            </a>
            <xsl:if test=".//s:chapter">
                <ol class="">
                    <xsl:apply-templates select="./node()" mode="content.outline"/>
                </ol>
            </xsl:if>
        </li>
    </xsl:template>
    <xsl:template name="content.outline">
        <xsl:param name="content"/>
        <xsl:param name="style" select="'Standard_p1'"/>
        <ol>
            <xsl:attribute name="class" select="$style"/>
            <xsl:apply-templates select="$content/node()" mode="content.outline"/>
        </ol>
    </xsl:template>
</xsl:stylesheet>