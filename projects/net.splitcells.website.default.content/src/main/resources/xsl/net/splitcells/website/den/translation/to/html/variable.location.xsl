<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:s="http://splitcells.net/sew.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="site_domain" select="'splitcells.net'" />
	<xsl:variable name="site.url"
		select="'http://splitcells.net'" />
		<!-- purl = pseudo url = root relative url -->
	<!-- TODO site_instance_purl may be deprecated as themes are now handled 
		in pure html/cs/js without mutlipe instances. -->
	<xsl:variable name="site_instance_purl" select="'/net/splitcells/martins/avots/website/'" />
	<xsl:variable name="site_generic_asset_purl" select="$site_instance_purl" />
	<xsl:variable name="site_instance_burl"
		select="concat('http://', $site_domain)" />
	<xsl:variable name="site_generic_asset_burl"
		select="concat($site_domain, $site_generic_asset_purl)" />
</xsl:stylesheet>