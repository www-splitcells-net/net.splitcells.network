<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:s="http://splitcells.net/sew.xsd"
	xmlns:d="http://splitcells.net/den.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
	<xsl:function name="s:default-root-relative-url">
		<xsl:param name="default-layout-relative-path" />
		<xsl:value-of
				select="concat($site-instance-root-path-default, $default-layout-relative-path)" />
	</xsl:function>
	<xsl:function name="s:root-relative-url">
		<xsl:param name="layout-relative-path" />
		<xsl:value-of
			select="concat($site_instance_purl, $layout-relative-path)" />
	</xsl:function>
	<xsl:function name="s:image_location">
		<xsl:param name="license" />
		<xsl:param name="image" />
		<xsl:value-of
			select="replace(normalize-space(concat($site_generic_asset_purl,'images/license.', $license, '/', $image)), ' ', '')" />
	</xsl:function>
	<xsl:function name="s:string.remove.whitespace">
		<xsl:param name="input"/>
		<xsl:value-of select="replace(normalize-space($input), ' ', '')"/>
	</xsl:function>
	<xsl:function name="s:image_thumbnail_medium_location">
		<xsl:param name="license" />
		<xsl:param name="image" />
		<xsl:value-of
			select="replace(normalize-space(concat($site_generic_asset_purl,'images/license.', $license, '/thumbnail/medium/', $image)), ' ', '')" />
	</xsl:function>
	<xsl:function name="s:licensed.quoting.allowed">
		<xsl:message terminate="yes" />
	</xsl:function>
	<xsl:function name="s:is.public.ontology.fully.allowed">
		<xsl:param name="subject" />
		<xsl:if test="count($subject/s:license) = 0">
			<xsl:message terminate="false">
				<!-- TODO reactivate -->
				<xsl:value-of select="$subject" />
			</xsl:message>
			<xsl:value-of select="true()" />
		</xsl:if>
		<xsl:variable name="license.name"
			select="$subject/s:license/@name" />
		<xsl:choose>
			<xsl:when
				test="$license.name='public.ontology.fully.allowed.assumed'">
				<xsl:comment>
					Permissive as defined in
					https://en.wikipedia.org/wiki/Permissive_software_licence, but no
					explicit license information present.
				</xsl:comment>
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when
				test="$license.name='GNU Free Documentation License'">
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:otherwise>
				<!-- TODO Disallow tagged references without license. -->
				<xsl:message terminate="false">
					Warning: License for reference is not present.
				</xsl:message>
				<xsl:value-of select="false()" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="s:licensed.permissive.is">
		<xsl:param name="subject" />
		<xsl:if test="count($subject/s:license) = 0">
			<xsl:message terminate="yes">
				<xsl:value-of select="$subject" />
			</xsl:message>
			<xsl:value-of select="true()" />
		</xsl:if>
		<xsl:variable name="license.name"
			select="$subject/s:license/@name" />
		<xsl:choose>
			<xsl:when test="$license.name='pseudo.permissive'">
				<xsl:comment>
					Permissive as defined in
					https://en.wikipedia.org/wiki/Permissive_software_licence
				</xsl:comment>
				<xsl:value-of select="true()" />
			</xsl:when>
			<xsl:when
				test="$license.name='GNU Free Documentation License'">
				<xsl:value-of select="false()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="s:path.element.last">
		<xsl:param name="uri.str"/>
		<xsl:value-of select="subsequence(reverse(tokenize($uri.str, '/')), 1, 1)"/>
	</xsl:function>
	<xsl:function name="s:path.element.first">
		<xsl:param name="uri.str"/>
		<xsl:value-of select="subsequence(tokenize($uri.str, '/'), 1, 1)"/>
	</xsl:function>
	<xsl:function name="s:path.without.element.first">
		<xsl:param name="uri.str"/>
		<xsl:variable name="uri.tokenized" select="reverse(tokenize($uri.str, '/'))"/>
		<xsl:value-of select="string-join(reverse(subsequence($uri.tokenized, 1, count($uri.tokenized) - 1)), '/')"/>
	</xsl:function>
	<xsl:function name="s:path.without.element.last">
		<xsl:param name="uri.str"/>
		<xsl:variable name="uri.tokenized" select="tokenize($uri.str, '/')"/>
		<xsl:value-of select="string-join(subsequence($uri.tokenized, 1, count($uri.tokenized) - 1), '/')"/>
	</xsl:function>
	<xsl:function name="s:string-remove-whitespace">
		<xsl:param name="arg"/>
		<xsl:value-of select="replace(normalize-space($arg), ' ', '')"/>
	</xsl:function>
	<xsl:function name="s:to-projects-relative-path">
		<xsl:param name="path"/>
		<!-- TODO This is an hack. -->
		<!--xsl:value-of select="substring($path, string-length($source.folder) - 1, string-length($path))"/-->
		<xsl:value-of select="concat('net/splitcells/', tokenize($path, '/net/splitcells/')[2])"/>
	</xsl:function>
</xsl:stylesheet>