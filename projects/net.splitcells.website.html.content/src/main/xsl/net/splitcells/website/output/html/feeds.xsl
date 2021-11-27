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
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rss="http://www.rssboard.org/rss-specification"
	xmlns:atom="http://www.w3.org/2005/Atom">
	<!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
	<xsl:template match="s:article.feed.latest.atom">
		<!-- TODO Remove namespace prefixes and namepsace declarations. -->
		<xsl:variable name="limit">
			<xsl:choose>
				<xsl:when test="./s:limit">
					<xsl:value-of
						select="s:string.remove.whitespace(./s:limit)" />
				</xsl:when>
				<xsl:otherwise>
					<!-- HACK -->
					<xsl:text>999999</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<atom:feed>
			<xsl:element name="atom:link">
				<xsl:attribute name="type"
					select="'application/atom+xml'" />
				<xsl:attribute name="href" select="$site.url" />
				<xsl:attribute name="rel" select="'self'" />
			</xsl:element>
			<xsl:element name="atom:link">
				<xsl:attribute name="type" select="'text/html'" />
				<xsl:attribute name="href" select="$site.url" />
				<xsl:attribute name="rel" select="'alternate'" />
			</xsl:element>
			<xsl:for-each
				select="collection(concat($articles.folder ,'/?select=*.xml'))">
				<xsl:sort
					select="concat(./s:article/s:meta/s:publication_date/@year, ./s:article/s:meta/s:publication_date/@month, 
					./s:article/s:meta/s:publication_date/@day_of_month )"
					order="descending" />
				<xsl:if test="1 = position()">
					<atom:updated>
						<!-- FIXME date time format -->
						<xsl:copy-of
							select="concat(./s:article/s:meta/s:publication_date/@day_of_month, ' ', ./s:article/s:meta/s:publication_date/@month, ' ', ./s:article/s:meta/s:publication_date/@year)" />
					</atom:updated>
				</xsl:if>
			</xsl:for-each>
			<atom:id>
				<xsl:value-of select="$site.url" />
			</atom:id>
			<atom:title>
				<xsl:value-of select="$siteName" />
			</atom:title>
			<atom:subtitle>
				<xsl:value-of select="$site.description" />
			</atom:subtitle>
			<atom:author>
				<atom:name><!-- TODO Define main author globally. -->
					<xsl:copy-of select="'Mārtiņš Avots'" />
				</atom:name>
				<atom:email>martins.avots@splitcells.net</atom:email>
				<atom:uri>
					<xsl:value-of select="$site.url" />
				</atom:uri>
			</atom:author>
			<xsl:for-each select="collection(concat($articles.folder, '?select=*.xml;recurse=yes'))">
				<xsl:sort
					select="concat(./s:article/s:meta/s:publication_date/@year, ./s:article/s:meta/s:publication_date/@month, 
					./s:article/s:meta/s:publication_date/@day_of_month )"
					order="descending" />
				<xsl:if test="$limit + 1 > position()">
					<atom:entry>
						<atom:title>
							<xsl:value-of
								select="./s:article/s:meta/s:title/node()" />
						</atom:title>
						<atom:link rel="alternate" type="text/html">
							<xsl:attribute name="href">
							<xsl:call-template name="s:site.file.link">
								<xsl:with-param name="file" select="." />
							</xsl:call-template>
							</xsl:attribute>
						</atom:link>
						<atom:published>
							<!-- FIX Date time format -->
							<xsl:copy-of
								select="concat(./s:article/s:meta/s:publication_date/@day_of_month, ' ', ./s:article/s:meta/s:publication_date/@month, ' ', ./s:article/s:meta/s:publication_date/@year)" />
						</atom:published>
						<atom:id>
							<xsl:call-template name="s:site.file.link">
								<xsl:with-param name="file" select="." />
							</xsl:call-template>
						</atom:id>
						<!-- TODO Content -->
						<!-- TODO Thumbnail -->
					</atom:entry>
				</xsl:if>
			</xsl:for-each>
		</atom:feed>
	</xsl:template>
	<xsl:template match="s:article.feed.latest.rss">
		<!-- TODO Remove namespace prefixes and namepsace declarations. -->
		<xsl:variable name="limit">
			<xsl:choose>
				<xsl:when test="./s:limit">
					<xsl:value-of
						select="s:string.remove.whitespace(./s:limit)" />
				</xsl:when>
				<xsl:otherwise>
					<!-- HACK -->
					<xsl:text>999999</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="rss.items">
			<xsl:for-each select="collection(concat($articles.folder, '?select=*.xml;recurse=yes'))">
				<xsl:sort
					select="concat(./s:article/s:meta/s:publication_date/@year, ./s:article/s:meta/s:publication_date/@month, 
					./s:article/s:meta/s:publication_date/@day_of_month )"
					order="descending" />
				<xsl:if test="$limit + 1 > position()">
					<rss:item>
						<xsl:element name="rss:title">
							<xsl:value-of
								select="./s:article/s:meta/s:title/node()" />
						</xsl:element>
						<xsl:element name="rss:creator">
							<!-- TODO Read author from article. -->
							<xsl:copy-of select="'Mārtiņš Avots'" />
						</xsl:element>
						<!--xsl:if test="./s:article/s:meta/s:description"> TODO Embedd and 
							encode HTML code or create pure text. <xsl:element name="rss:description"> 
							<xsl:apply-templates select="./s:article/s:meta/s:description/node()" /> 
							</xsl:element> </xsl:if -->
						<xsl:variable name="publication.date">
							<xsl:copy-of
								select="./s:article/s:meta/s:publication_date" />
						</xsl:variable>
						<xsl:element name="rss:pupDate">
							<xsl:copy-of
								select="concat(./s:article/s:meta/s:publication_date/@day_of_month, ' ', ./s:article/s:meta/s:publication_date/@month, ' ', ./s:article/s:meta/s:publication_date/@year)" />
						</xsl:element>
						<xsl:element name="rss:link">
							<!-- TODO Create and use general template for link creation based 
								on article. -->
							<xsl:call-template name="s:site.file.link">
								<xsl:with-param name="file" select="." />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="rss:guid">
							<xsl:attribute name="isPermaLink" select="'true'" />
							<xsl:value-of select="generate-id(./s:article)" />
						</xsl:element>
					</rss:item>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<rss:rss version="2.0">
			<rss:channel>
				<xsl:element name="rss:title">
					<xsl:copy-of select="$siteName" />
				</xsl:element>
				<xsl:element name="rss:description">
					<xsl:copy-of select="$site.description" />
				</xsl:element>
				<xsl:element name="rss:link">
					<xsl:copy-of select="$site.url" />
				</xsl:element>
				<xsl:copy-of select="$rss.items" />
			</rss:channel>
		</rss:rss>
	</xsl:template>
</xsl:stylesheet>
