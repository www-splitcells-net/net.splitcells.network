<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
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