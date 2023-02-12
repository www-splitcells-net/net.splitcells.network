<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
    SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <xsl:variable name="siteFolder">
        <xsl:value-of select="document('/net/splitcells/website/server/config/site.folder.xml')"/>
    </xsl:variable>
    <xsl:variable name="layoutFolder"
                  select="concat($siteFolder, '/net.splitcells.website.default.content/src/main/resources/html/')"/>
    <xsl:variable name="source_asset_folder"
                  select="concat($siteFolder, '/net.splitcells.gel.doc/src/main/resources.private/_included')"/>
    <xsl:variable name="source_asset_sourceCode_folder"
                  select="concat($siteFolder, '/net.splitcells.website.default.content/src/main/resources/_included/source-code/')"/>
    <xsl:variable name="apostroph">'</xsl:variable>
    <xsl:variable name="source.folder"
                  select="concat($siteFolder, './src/main/xml/')"/>
</xsl:stylesheet>