<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
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
	<xsl:variable name="siteName" select="'splitcells.net'" />
	<xsl:variable name="site.description" select="'splitcells.net by Mārtiņš Avots NOS'" />
</xsl:stylesheet>