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
                xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ln="http://splitcells.net/local-namespace.xsd"
                xmlns:xl="http://www.w3.org/1999/xlink">
    <!--
        SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
        SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

        TODO Rename den to tree.
        TODO Rename perspective mode to tree.
    -->
    <xsl:template match="text()" mode="perspective">
        <div class="perspective">
            <div class="perspective-value">
                <xsl:value-of select="normalize-space(.)"/>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="node()" mode="perspective">
        <xsl:call-template name="tree">
            <xsl:with-param name="tree" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="d:*">
        <xsl:apply-templates select="." mode="perspective"/>
    </xsl:template>
    <xsl:template match="d:*" mode="perspective">
        <xsl:call-template name="tree">
            <xsl:with-param name="tree" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="n:*" mode="perspective">
        <xsl:call-template name="tree">
            <xsl:with-param name="tree" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="s:path-of">
        <xsl:param name="document"/>
        <s:path>
            <xsl:choose>
                <xsl:when test="./d:path">
                    <xsl:value-of select="./d:path/node()"/>
                </xsl:when>
                <xsl:when test="./s:meta/s:path">
                    <xsl:value-of select="./s:meta/s:path/node()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                            select="s:path.without.element.last(s:to-projects-relative-path(document-uri(/)))"/>
                </xsl:otherwise>
            </xsl:choose>
        </s:path>
    </xsl:template>
    <xsl:template name="file-path-environment-of-layout">
        <xsl:param name="path"/>
        <xsl:param name="layout"/>
        <ol>
            <xsl:call-template name="file-path-environment-of-layout-node">
                <xsl:with-param name="path" select="$path"/>
                <xsl:with-param name="layout" select="$layout"/>
            </xsl:call-template>
        </ol>
    </xsl:template>
    <xsl:template name="file-path-environment-of-layout-node">
        <!-- TODO uri-collection: https://www.w3.org/TR/xpath-functions/#func-doc-available -->
        <xsl:param name="path"/>
        <xsl:param name="layout"/>
        <xsl:param name="previous-depth" select="0"/>
        <!-- max-depth was chosen in such way, that the indention takes up half of the default content column width.-->
        <xsl:param name="max-depth" select="7"/>
        <xsl:if test="$previous-depth &lt;= $max-depth">
            <xsl:choose>
                <xsl:when test="$path = ''">
                    <li>
                        <xsl:choose>
                            <xsl:when test="$layout/d:link/d:url">
                                <a>
                                    <xsl:attribute name="href"
                                                   select="$layout/d:link/d:url/node()"/>
                                    <xsl:variable name="file-path"
                                                  select="concat($source.folder, $layout/d:link/d:url/node())"/>
                                    <xsl:choose>
                                        <xsl:when test="doc-available($file-path)">
                                            <xsl:variable name="linked-document"
                                                          select="document($file-path)"/>
                                            <xsl:choose>
                                                <xsl:when test="local-name($linked-document/s:article) = 'article'">
                                                    <xsl:apply-templates
                                                            select="$linked-document/s:article/s:meta/s:title/node()"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:apply-templates select="$layout/n:name/node()"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="$layout/n:name/node()"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$layout/n:name/node()"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </li>
                    <xsl:choose>
                        <xsl:when test="count($layout/n:val) &gt; 0">
                            <ol>
                                <xsl:for-each select="$layout/n:val">
                                    <xsl:sort select="n:name/node()"/>
                                    <xsl:call-template name="file-path-environment-of-layout-node">
                                        <xsl:with-param name="path" select="$path"/>
                                        <xsl:with-param name="layout"
                                                        select="."/>
                                        <xsl:with-param name="previous-depth" select="$previous-depth + 1"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </ol>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="$layout/n:val">
                                <xsl:sort select="n:name/node()"/>
                                <xsl:call-template name="file-path-environment-of-layout-node">
                                    <xsl:with-param name="path" select="$path"/>
                                    <xsl:with-param name="layout"
                                                    select="."/>
                                    <xsl:with-param name="previous-depth" select="$previous-depth + 1"/>
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="$layout/n:val[n:name/node() = s:path.element.first($path)]">
                        <xsl:call-template name="file-path-environment-of-layout-node">
                            <xsl:with-param name="path" select="s:path.without.element.first($path)"/>
                            <xsl:with-param name="layout"
                                            select="."/>
                            <xsl:with-param name="previous-depth" select="$previous-depth"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <xsl:function name="s:can-show-text-as-line">
        <!-- TODO Parameterize. -->
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="100 &lt; string-length(normalize-space($text)) and 1 &lt; count(tokenize($text, '\n\n'))">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:when test="140 &lt; string-length(normalize-space($text))">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    <xsl:function name="s:can-show-children-in-row">
        <!--TODO Create target width of result text.-->
        <!--TODO Create currently already used width of result text.-->
        <!--TODO Create function call heuristic, where an xml tree can be rendered as a function call.-->
        <xsl:param name="parent-element"/>
        <xsl:choose>
            <xsl:when test="1 >= count($parent-element/node())">
                <!-- TODO Create path heuristic, where only things are rendered in a row, which can be rendered as line. -->
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    <xsl:template name="tree">
        <xsl:param name="tree"/>
        <ol>
            <li>
                <xsl:value-of select="local-name($tree)"/>
            </li>
            <!-- TODO Strictly speaking this form of list nesting is not correct,
            as any child element of an ol, has to be an li.
            Unfortunately, the current CSS relies on that, so this has to be adjusted as well. -->
            <ol>
                <xsl:for-each select="$tree/node()">
                    <xsl:choose>
                        <xsl:when test="self::text()">
                            <xsl:if test="normalize-space(.) != ''">
                                <li>
                                    <xsl:choose>
                                        <xsl:when test="true() = s:can-show-text-as-line(.)">
                                            <xsl:for-each select="tokenize(., '\n\n')">
                                                <xsl:value-of select="normalize-space(.)"/>
                                            </xsl:for-each>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="."/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </li>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="tree">
                                <xsl:with-param name="tree" select="."/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </ol>
        </ol>
    </xsl:template>
</xsl:stylesheet>