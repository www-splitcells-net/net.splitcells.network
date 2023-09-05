<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
    SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <xsl:variable name="site_domain" select="'splitcells.net'"/>
    <xsl:variable name="site.url"
                  select="'http://splitcells.net'"/>
    <!-- purl = pseudo url = root relative url -->
    <!-- TODO REMOVE This is the base path to the main additional resources. -->
    <!-- TODO RENAME site_instance_burl -->

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
    <!-- TODO Remove `site-instance-root-path-default`, `site_instance_purl`, `site_generic_asset_purl`
    and `site_generic_asset_burl`,
    because these only create problems. -->
    <xsl:variable name="site-instance-root-path-default"
                  select="concat($site-instance-host-root-path, '')"/>
    <xsl:variable name="site_instance_purl" select="concat($site-instance-host-root-path, 'net/splitcells/martins/avots/website/')"/>
    <xsl:variable name="site_generic_asset_purl" select="$site_instance_purl"/>
    <xsl:variable name="site_generic_asset_burl"
                  select="concat($site_domain, $site_generic_asset_purl)"/>
</xsl:stylesheet>