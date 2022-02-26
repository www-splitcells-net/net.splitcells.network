<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <xsl:variable name="site_domain" select="'splitcells.net'"/>
    <xsl:variable name="site.url"
                  select="'http://splitcells.net'"/>
    <!-- purl = pseudo url = root relative url -->
    <!-- TODO REMOVE This is the base path to the main additional resources. -->

    <xsl:variable name="site_instance_burl"
                  select="concat('http://', $site_domain)"/>
    <!-- TODO These are the new and correct locations. -->
    <xsl:variable name="site-instance-host-root-path">
        <!-- This is used, if the site is not hosted a the root path of a domain (i.e. rendering to file system).
        If this would not be supported, it would be harder to link between projects.-->
        <xsl:choose>
            <xsl:when test="document('/net/splitcells/website/server/config/root.path.xml')">
                <xsl:value-of select="document('/net/splitcells/website/server/config/root.path.xml')/node()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'/'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="site-instance-root-path-default"
                  select="concat($site-instance-host-root-path, '')"/>
    <xsl:variable name="site_instance_purl" select="concat($site-instance-host-root-path, 'net/splitcells/martins/avots/website/')"/>
    <xsl:variable name="site_generic_asset_purl" select="$site_instance_purl"/>
    <xsl:variable name="site_generic_asset_burl"
                  select="concat($site_domain, $site_generic_asset_purl)"/>
</xsl:stylesheet>