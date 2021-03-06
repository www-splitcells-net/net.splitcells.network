<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    <xsl:variable name="generated-files"
                  select="concat($siteFolder, 'target/generated')"/>
</xsl:stylesheet>