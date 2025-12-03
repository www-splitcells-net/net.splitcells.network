<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd"
                xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
        SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
        SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <xsl:template match="*" mode="net-splitcells-gel-editor-menu">
        <xsl:apply-templates select="./*" mode="net-splitcells-gel-editor-menu"/>
    </xsl:template>
    <xsl:template match="s:commands" mode="net-splitcells-gel-editor-menu">
        <xsl:apply-templates mode="net-splitcells-gel-editor-menu"/>
    </xsl:template>
    <xsl:template match="s:command-group" mode="net-splitcells-gel-editor-menu">
        <div>
            <xsl:attribute name="class" select="concat('net-splitcells-website-menu-sub net-splitcells-website-menu-name-', @name, ' net-splitcells-website-static')"/>
            <div class="net-splitcells-website-menu-sub-title">
                <xsl:value-of select="@name"/>
            </div>
            <xsl:apply-templates mode="net-splitcells-gel-editor-menu"/>
        </div>
    </xsl:template>
    <xsl:template match="s:command" mode="net-splitcells-gel-editor-menu">
        <xsl:choose>
            <xsl:when test="./@target-id">
                <xsl:variable name="onClick">
                    <xsl:value-of
                            select="concat('javascript: ', ./@method, '(document.getElementById(&quot;')"/>
                    <xsl:value-of select="./@target-id"/>
                    <xsl:value-of select="'&quot;))'"/>
                </xsl:variable>
                <div class="net-splitcells-button net-splitcells-action-button net-splitcells-action-text-button net-splitcells-website-static">
                    <xsl:attribute name="onclick" select="$onClick"/>
                    <xsl:if test="./@id">
                        <xsl:attribute name="id" select="./@id"/>
                    </xsl:if>
                    <xsl:apply-templates select="./node()" mode="net-splitcells-gel-editor-menu"/>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <div class="net-splitcells-button net-splitcells-action-button net-splitcells-action-text-button net-splitcells-website-static">
                    <xsl:choose>
                        <xsl:when test="ends-with(@method, ')')">
                            <xsl:attribute name="onclick"
                                           select="concat('javascript: ', ./@method)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="onclick"
                                           select="concat('javascript: ', ./@method, '()')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="./@id">
                        <xsl:attribute name="id" select="./@id"/>
                    </xsl:if>
                    <xsl:apply-templates select="./node()" mode="net-splitcells-gel-editor-menu"/>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>