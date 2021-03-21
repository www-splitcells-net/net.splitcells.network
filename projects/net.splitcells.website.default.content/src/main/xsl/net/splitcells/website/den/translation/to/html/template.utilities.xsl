<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:s="http://splitcells.net/sew.xsd"
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.w3.org/1999/xhtml"
	xmlns:d="http://splitcells.net/den.xsd"
	xmlns:p="http://splitcells.net/private.xsd"
	xmlns:m="http://www.w3.org/1998/Math/MathML"
	xmlns:r="http://splitcells.net/raw.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="seed.initial" select="7" />
	<xsl:template name="list.randomize">
		<!-- Source: https://stackoverflow.com/questions/21953336/randomize-node-order-xslt -->
		<xsl:param name="items" />
		<xsl:param name="seed" select="$seed.initial" />
		<xsl:if test="$items">
			<!-- generate a random number using the "linear congruential generator" 
				algorithm -->
			<xsl:variable name="a" select="1664525" />
			<xsl:variable name="c" select="1013904223" />
			<xsl:variable name="m" select="4294967296" />
			<xsl:variable name="random"
				select="($a * $seed + $c) mod $m" />
			<!-- scale random to integer 1..n -->
			<xsl:variable name="i"
				select="floor($random div $m * count($items)) + 1" />
			<!-- write out the corresponding item -->
			<xsl:copy-of select="$items[$i]" />
			<!-- recursive call with the remaining items -->
			<xsl:call-template name="list.randomize">
				<xsl:with-param name="items"
					select="$items[position()!=$i]" />
				<xsl:with-param name="seed" select="$random" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>