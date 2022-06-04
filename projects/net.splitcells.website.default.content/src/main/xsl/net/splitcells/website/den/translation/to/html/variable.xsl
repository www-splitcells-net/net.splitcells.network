<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <!-- TODO Bad naming: pseudo root is the real root. -->
    <!-- purl = pseudo root relative URL -->
    <!-- not possible on this site: rurl = relative URL -->
    <!-- burl = absolute URL -->
    <!-- -->
    <!-- These are constants not variables. -->
    <!-- generic info ( -->
    <xsl:variable name='newline'><![CDATA[

]]>&#10;&#10;&#10;&#9;
    </xsl:variable>
    <xsl:variable name="siteName" select="'Splitcells Network'"/>
    <xsl:variable name="site.description" select="'splitcells.net by Mārtiņš Avots NOS'"/>
    <xsl:variable name="generation.style">
        <!-- Currently, only standard and minimal are supported. -->
        <xsl:choose>
            <xsl:when test="document('/net/splitcells/website/server/config/generation.style.xml')">
                <xsl:value-of select="document('/net/splitcells/website/server/config/generation.style.xml')"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO Standard should match minimal as good as possible.
                     In the best case scenario standard only differs from minimal in linked linked info. -->
                <xsl:value-of select="'standard'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
</xsl:stylesheet>