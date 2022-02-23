<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ns="http://splitcells.net/namespace.xsd"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:t="http://splitcells.net/text.xsd"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <xsl:include href="article.select.xsl"/>
    <xsl:include href="component.standard.xsl"/>
    <xsl:include href="component.todo.xsl"/>
    <xsl:include href="content.outline.xsl"/>
    <xsl:include href="feeds.xsl"/>
    <xsl:include href="den.xsl"/>
    <xsl:include href="rdf.as.javascript.json.xsl"/>
    <xsl:include href="rdf.xsl"/>
    <xsl:include href="functions.xsl"/>
    <xsl:include href="namespace.xsl"/>
    <xsl:include href="template.utilities.xsl"/>
    <xsl:include href="natural.xsl"/>
    <xsl:include href="variable.xsl"/>
    <xsl:include href="variable.location.project.xsl"/>
    <xsl:include href="variable.location.xsl"/>
    <!-- Following templates are only applied if previous templates did not match. -->
    <xsl:include href="layout.xsl"/>
</xsl:stylesheet>