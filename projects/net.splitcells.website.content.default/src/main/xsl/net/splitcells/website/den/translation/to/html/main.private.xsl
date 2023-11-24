<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ns="http://splitcells.net/namespace.xsd"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:t="http://splitcells.net/text.xsd"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
    <!--
    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
    SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

    TODO Use only one main XSL file,
    because the configuration should be done via the XSL executor.
    -->
    <xsl:output method="html" indent="no" omit-xml-declaration="yes"/>
    <xsl:include href="main.lib.xsl"/>
    <xsl:template name="file-path-environment">
        <xsl:param name="path"/>
        <xsl:call-template name="file-path-environment-of-layout">
            <xsl:with-param name="path" select="$path"/>
            <xsl:with-param name="layout" select="document('/net/splitcells/website/server/config/layout.xml')/node()"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="file-path-environment-relevant">
        <xsl:param name="path"/>
        <xsl:call-template name="file-path-environment-of-layout">
            <xsl:with-param name="path" select="$path"/>
            <xsl:with-param name="layout"
                            select="document('/net/splitcells/website/server/config/layout.relevant.xml')/node()"/>
        </xsl:call-template>
    </xsl:template>
</xsl:stylesheet>