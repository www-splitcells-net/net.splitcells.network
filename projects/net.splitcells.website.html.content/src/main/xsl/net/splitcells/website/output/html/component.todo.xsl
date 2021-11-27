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
	<!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
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