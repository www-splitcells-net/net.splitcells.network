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
	xmlns:gm="http://graphml.graphdrawing.org/xmlns"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
	<xsl:template match="s:graph">
		<div viewBox="0 0 100 100">
			<svg style="width: 100%; height: auto;">
				<defs>
					<marker id='head' orient="auto" markerWidth='2'
						markerHeight='4' refX='0.1' refY='2'>
						<!-- triangle pointing right (+x) -->
						<path d='M0,0 V4 L2,2 Z' fill="red" />
					</marker>
				</defs>
				<xsl:apply-templates select="./node()" />
			</svg>
		</div>
	</xsl:template>
	<xsl:template match="s:node">
		<g>
			<rect width="15" height="15" fill="grey">
				<xsl:attribute name="x" select="./@x" />
				<xsl:attribute name="y" select="./@y" />
			</rect>
			<text fill="black">
				<xsl:attribute name="x" select="./@x" />
				<xsl:attribute name="y" select="./@y" />
				<xsl:value-of select="./node()" />
			</text>
		</g>
	</xsl:template>
	<xsl:template match="s:edge">
		<xsl:variable name="source"
			select="//s:node[@id = ./@source]" />
		<xsl:variable name="target"
			select="//s:node[@id = ./@target]" />
		<path id='arrow-line' marker-end='url(#head)' stroke-width='5'
			fill='none' stroke='black'>
			<xsl:attribute name="d"
				select="concat('M ', $source/@x1, ',', $source/@y1, ' L ', $target/@x2, ',', $target/@y2, ' ')" />
		</path>

	</xsl:template>
</xsl:stylesheet>