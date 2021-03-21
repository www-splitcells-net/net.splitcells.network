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
	<xsl:template match="rdf:description" mode="todo">
		<s:premature>
			<xsl:apply-templates select="." />
		</s:premature>
	</xsl:template>
	<xsl:template match="s:paragraph" mode="todo">
		<!-- TODO alternating line colors -->
		<div
			style="padding-top: 1.4em; padding-bottom: 1.4em; padding-left: 1em; padding-right: 1em;">
			<xsl:apply-templates mode="todo" />
		</div>
	</xsl:template>
</xsl:stylesheet>