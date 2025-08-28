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
    <xsl:template match="*" mode="net-splitcells-gel-editor">
        <xsl:apply-templates select="./*" mode="net-splitcells-gel-editor"/>
    </xsl:template>
    <xsl:template match="s:form-editor" mode="net-splitcells-gel-editor">
        <xsl:variable name="quote">'</xsl:variable>
        <xsl:variable name="form-id" select="@id"/>
        <xsl:for-each select="descendant::s:text-area">
            <div>
                <xsl:attribute name="class" select="concat('net-splitcells-button net-splitcells-action-button net-splitcells-action-text-button ', $form-id, '-tab-button ', $form-id, '-', @name, '-tab-button')"/>
                <xsl:attribute name="onclick" select="concat('net_splitcells_webserver_form_tab_select(', $quote, $form-id, $quote, ', ', $quote, @name, $quote, ');')"/>
                <xsl:value-of select="@name"/>
            </div>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>