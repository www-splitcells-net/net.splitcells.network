<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd"
                xmlns:m="http://www.w3.org/1998/Mar:descriptionth/MathML"
                xmlns:r="http://splitcells.net/raw.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <!-- Microdata from https://schema.org/docs/gs.html is used for meta data. -->
    <!-- TODO Annotate type of item described by  via Microdata
        annotation. -->
    <!-- Microdata: Note that the order of the attributes itemprop, itemtype
        and itemscope have a meaning: https://schema.org/docs/gs.html#microdata_how -->
    <xsl:template match="rdf:Description">
        <xsl:if test="s:is.public.ontology.fully.allowed(.) = false()">
            <!-- TODO reactivate -->
            <xsl:message terminate="false">
                : Reference require a license in order to check if
                public tags are
                allowed.
            </xsl:message>
        </xsl:if>
        <div class="standard-margin Standard_highlighted standardborder"
             itemscope="" itemtype="https://schema.org/Thing">
            <xsl:if test="@rdf:resource">
                <xsl:message terminate="false">
                    <xsl:copy-of
                            select="'WARNING: Object id cannot be for subject.2017-06-04-the-art-of-programming.xml'"/>
                    <xsl:copy-of select="."/>
                </xsl:message>
            </xsl:if>
            <xsl:variable name="about">
                <xsl:choose>
                    <xsl:when test="@rdf:resource">
                        <xsl:value-of select="@rdf:resource"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@rdf:about"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="id" select="concat('net-splitcells-website-description-', @rdf:about)"/>
            <div class="highlighted">
                <a class="NonStandard IndexPostHeader"
                   style="font-size: 125%; font-weight: bold; margin-left: .5em;"
                   itemprop="url">
                    <xsl:attribute name="href" select="$about"/>
                    <xsl:copy-of select="dc:title/node()"/>
                </a>
                <div class="hidden" itemprop="name">
                    <!-- An additional hidden div is used in order to reference the text
                        value and not the href attribute of the link. -->
                    <xsl:copy-of select="dc:title/node()"/>
                </div>
            </div>
            <!-- TODO Match not processed tags and throw error. -->
            <!-- TODO Use of Microdata. -->
            <div class="summary_footer"
                 style="display: flex; flex-direction: row; flex-wrap: wrap;">
                <xsl:for-each select="./*">
                    <xsl:apply-templates select="."
                                         mode="rdf:Description"/>
                </xsl:for-each>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="skos:broader" mode="rdf:Description">
        <div class="cell">
            <xsl:value-of select="@rdf:id"/>
        </div>
    </xsl:template>
    <xsl:template match="dc:title" mode="rdf:Description">
        <!-- The title is displayed at the top of the card. -->
    </xsl:template>
    <xsl:template match="dc:creator" mode="rdf:Description">
        <div class="cell" itemprop="author" itemscope=""
             itemtype="https://schema.org/Person">
            <xsl:apply-templates select="node()"/>
        </div>
    </xsl:template>
    <xsl:template match="dc:source" mode="rdf:Description">
        <div class="cell" itemprop="url">
            <a>
                <xsl:attribute name="href" select="@rdf:resource"/>
                source
            </a>
        </div>
    </xsl:template>
    <xsl:template match="d:todo" mode="rdf:Description">
        <div class="cell premature">
            <xsl:apply-templates select="node()"/>
        </div>
    </xsl:template>
    <xsl:template match="dc:date" mode="rdf:Description">
        <div class="cell" itemprop="datePublished">
            <xsl:apply-templates select="node()"/>
        </div>
    </xsl:template>
    <xsl:template match="dc:description" mode="rdf:Description">
        <div class="cell">
            <xsl:apply-templates select="node()"/>
        </div>
    </xsl:template>
    <xsl:template match="p:*" mode="rdf:Description">
        <!-- Hide private information. -->
    </xsl:template>
    <xsl:template match="*" mode="rdf:label">
        <!-- Not needed for now. -->
    </xsl:template>
    <xsl:template match="*" mode="rdf:Description">
        <xsl:message terminate="true">
            prefix
            <xsl:copy-of select="."/>
        </xsl:message>
    </xsl:template>
</xsl:stylesheet>