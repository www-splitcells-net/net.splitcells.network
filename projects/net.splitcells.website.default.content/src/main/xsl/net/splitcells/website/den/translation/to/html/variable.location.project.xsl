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
    <xsl:param name="siteFolder"
               select="'/home/splitcells/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects/'"/>
    <xsl:variable name="privateFolder"
                  select="'/home/splitcells/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/'"/>
    <xsl:variable name="layoutFolder"
                  select="concat($siteFolder, '/net.splitcells.website.default.content/src/main/resources/html/')"/>
    <xsl:variable name="source_asset_folder"
                  select="concat($siteFolder, '/net.splitcells.gel.doc/src/main/resources.private/_included')"/>
    <xsl:variable name="source_asset_sourceCode_folder"
                  select="concat($siteFolder, '/net.splitcells.website.default.content/src/main/resources/_included/source-code/')"/>
    <xsl:variable name="apostroph">'</xsl:variable>
    <xsl:variable name="source.folder"
                  select="concat($siteFolder, './src/main/xml/')"/>
    <xsl:variable name="articles.folder"
                  select="concat($privateFolder, 'src/main/xml/net/splitcells/martins/avots/website/')"/>
</xsl:stylesheet>