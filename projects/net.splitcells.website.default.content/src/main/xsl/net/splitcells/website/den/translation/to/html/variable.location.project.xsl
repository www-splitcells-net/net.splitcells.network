<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:s="http://splitcells.net/sew.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="siteFolder"
		select="'/home/splitcells/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/'" />
	<xsl:variable name="layoutFolder"
		select="concat('', '../net.splitcells.website.default.content/src/main/resources/html/')" />
	<xsl:variable name="source_asset_folder"
		select="concat('', '../net.splitcells.gel.doc/src/main/resources.private/_includes')" />
	<xsl:variable name="source_asset_sourceCode_folder"
		select="concat('', '../net.splitcells.website.default.content/src/main/resources/_includes/source-code/')" />
	<xsl:variable name="apostroph">'</xsl:variable>
	<xsl:variable name="source.folder"
				  select="concat($siteFolder, './src/main/xml/')" />
	<xsl:variable name="articles.folder"
		select="concat($source.folder, './net/splitcells/martins/avots/website/')" />
	<xsl:variable name="generated-files"
				  select="concat($siteFolder, 'target/generated')" />
</xsl:stylesheet>