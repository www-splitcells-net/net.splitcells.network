<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/1999/xhtml"
                xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd" xmlns:p="http://splitcells.net/private.xsd"
                xmlns:m="http://www.w3.org/1998/Math/MathML" xmlns:r="http://splitcells.net/raw.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="s:for-each-article">
        <!-- TODO Should article tags be supported? -->
        <xsl:variable name="detailed_tags">
            <xsl:choose>
                <xsl:when test="./s:detailed_tags">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>false</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="limit">
            <xsl:choose>
                <xsl:when test="./@limit">
                    <xsl:value-of select="./@limit"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- HACK -->
                    <xsl:text>999999</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="result">
            <!-- REFACTOR remove code duplication related to if else construct. -->
            <xsl:variable name="related_to">
                <xsl:apply-templates select="./s:related_to/node()"/>
            </xsl:variable>
            <xsl:variable name="unrelated">
                <xsl:choose>
                    <xsl:when test="./s:unrelated">
                        <xsl:value-of select="'yes'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'no'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <!-- TODO path -->
            <xsl:for-each select="collection(concat($articles.folder, '?select=*.xml;recurse=yes'))">
                <!-- REMOVE <xsl:sort select="tokenize(document-uri(.), '/')[last()]" /> -->
                <xsl:sort
                        select="concat(./s:article/s:meta/s:publication_date/@year, ./s:article/s:meta/s:publication_date/@month, ./s:article/s:meta/s:publication_date/@day_of_month )"
                        order="descending"/>
                <xsl:if test="$limit + 1 > position()">
                    <!-- FIXME This causes that articles with multiple tags may be rendered multiple times. Recursion has to be used. -->
                    <xsl:variable name="card">
                        <s:card>
                            <s:name>
                                <xsl:apply-templates select="./s:article/s:meta/s:title/node()"/>
                            </s:name>
                            <s:description>
                                <xsl:apply-templates select="./s:article/s:meta/s:description/node()"/>
                            </s:description>
                            <xsl:if test="./s:article/s:meta/s:descriptive_imagery">
                                <s:logos>
                                    <xsl:copy-of select="./s:article/s:meta/s:descriptive_imagery/*[1]"/>
                                </s:logos>
                            </xsl:if>
                            <xsl:variable name="year" select="./s:article/s:meta/s:publication_date/@year"/>
                            <xsl:variable name="month" select="./s:article/s:meta/s:publication_date/@month"/>
                            <xsl:variable name="day_of_month"
                                          select="./s:article/s:meta/s:publication_date/@day_of_month"/>
                            <xsl:variable name="document-uri" select="document-uri(.)"/>
                            <xsl:variable name="document-path"
                                          select="substring($document-uri, string-length($articles.folder) + 5, string-length($document-uri))"/>
                            <xsl:variable name="document-path-without-file-suffix"
                                          select="substring($document-path, 1, string-length($document-path) - 4)"/>
                            <s:location>
                                <xsl:value-of select="$document-path-without-file-suffix"/>
                            </s:location>
                            <xsl:element name="s:publication_date">
                                <xsl:attribute name="year">
                                    <xsl:value-of select="$year"/>
                                </xsl:attribute>
                                <xsl:attribute name="month">
                                    <xsl:value-of select="$month"/>
                                </xsl:attribute>
                                <xsl:attribute name="day_of_month">
                                    <xsl:value-of select="$day_of_month"/>
                                </xsl:attribute>
                            </xsl:element>
                        </s:card>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$related_to != ''">
                            <xsl:for-each select="./s:article/s:meta/s:related_to/node()">
                                <xsl:variable name="foreign_related_to_url">
                                    <xsl:apply-templates select="."/>
                                </xsl:variable>
                                <xsl:for-each select="$related_to">
                                    <!-- FIXME This causes that articles with multiple tags may be rendered multiple times. Recursion has to be used. -->
                                    <xsl:variable name="related_to_url">
                                        <xsl:apply-templates select="."/>
                                    </xsl:variable>
                                    <!-- this_<xsl:value-of select="$related_to_url"/> -->
                                    <xsl:if test="$related_to_url=$foreign_related_to_url">
                                        <xsl:apply-templates select="$card">
                                            <xsl:with-param name="detailed_tags" select="$detailed_tags"/>
                                        </xsl:apply-templates>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:when test="$unrelated = 'yes'">
                            <xsl:if test="not(./s:article/s:meta/s:related_to)">
                                <xsl:apply-templates select="$card">
                                    <xsl:with-param name="detailed_tags" select="$detailed_tags"/>
                                </xsl:apply-templates>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="$card">
                                <xsl:with-param name="detailed_tags" select="$detailed_tags"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:copy-of select="$result/node()"/>
        <!-- <xsl:call-template name="list.randomize"> <xsl:with-param name="items" select="$result/node()" /> </xsl:call-template> -->
    </xsl:template>
</xsl:stylesheet>