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
    <xsl:template match="text()" mode="perspective">
        <div class="perspective">
            <div class="perspective-value">
                <xsl:value-of select="normalize-space(.)"/>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="node()" mode="perspective">
        <xsl:call-template name="den-ast">
            <xsl:with-param name="den-document" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="d:*">
        <xsl:apply-templates select="." mode="perspective"/>
    </xsl:template>
    <xsl:template match="d:*" mode="perspective">
        <xsl:call-template name="den-ast">
            <xsl:with-param name="den-document" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="n:*" mode="perspective">
        <xsl:call-template name="den-ast">
            <xsl:with-param name="den-document" select="."/>
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
                                                   select="s:convert-internal-project-relative-path-to-public-project-relative-path($layout/d:link/d:url/node())"/>
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
    <!-- TODO
    <xsl:template name="den-ast">
        <xsl:param name="den-document"/>
        <ol>
            <xsl:apply-templates select="$den-document/node()" mode="den-ast-node"/>
        </ol>
    </xsl:template>
    <xsl:template match="*" mode="den-ast-node">
        <li class="table-of-content">
                <xsl:value-of select="./text()"/>
            <xsl:if test=".//s:chapter">
                <ol class="table-of-content">
                    <xsl:apply-templates select="./node()" mode="content.outline"/>
                </ol>
            </xsl:if>
        </li>
    </xsl:template>
    -->
    <xsl:template name="den-ast">
        <xsl:param name="den-document"/>
        <xsl:param name="is-root">
            <xsl:text>true</xsl:text>
        </xsl:param>
        <xsl:variable name="den-document-id" select="generate-id()"/>
        <xsl:variable name="den-document-hide-current-note-id" select="concat($den-document-id, 'hide-current-node')"/>
        <xsl:variable name="content">
            <xsl:variable name="perspective-namespace">
                <xsl:if test="name($den-document) != 'n:val'">
                    <xsl:value-of select="name($den-document)"/>
                </xsl:if>
            </xsl:variable>
            <div class="perspective-name">
                <div class="perspective-namespace">
                    <xsl:copy-of select="$perspective-namespace"/>
                </div>
                <xsl:variable name="perspective-value">
                    <xsl:choose>
                        <xsl:when test="$den-document/@xl:href">
                            <a>
                                <xsl:attribute name="href" select="$den-document/@xl:href"/>
                                <xsl:choose>
                                    <xsl:when test="normalize-space($den-document/@name) = ''">
                                        <xsl:value-of select="$den-document/@xl:href"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="$den-document/@name"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </a>
                        </xsl:when>
                        <xsl:when test="$den-document/@name">
                            <xsl:value-of select="$den-document/@name"/>
                        </xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose>
                </xsl:variable>
                <div class="perspective-word">
                    <xsl:copy-of select="$perspective-value"/>
                </div>
            </div>
            <ul class="net-splitcells-den-perspective">
                <xsl:for-each select="$den-document/node()">
                    <xsl:choose>
                        <xsl:when test="self::text()">
                            <xsl:if test="normalize-space(.) != ''">
                                <xsl:choose>
                                    <xsl:when test="true() = s:can-show-text-as-line(.)">
                                        <li class="net-splitcells-den-perspective">
                                            <div class="net-splitcells-den-paragraph">
                                                <xsl:for-each select="tokenize(., '\n\n')">
                                                    <xsl:value-of select="normalize-space(.)"/>
                                                </xsl:for-each>
                                            </div>
                                        </li>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <li class="net-splitcells-den-perspective">
                                            <div class="net-splitcells-den-paragraph">
                                                <xsl:value-of select="."/>
                                            </div>
                                        </li>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <!--xsl:apply-templates select="."
                                                 mode="perspective"/-->
                            <xsl:call-template name="den-ast">
                                <xsl:with-param name="den-document" select="."/>
                                <xsl:with-param name="is-root" select="'false'"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </ul>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$is-root = 'true'">
                <ul class="net-splitcells-den-perspective">
                    <xsl:copy-of select="$content"/>
                </ul>
            </xsl:when>
            <xsl:otherwise>
                <li class="net-splitcells-den-perspective">
                    <xsl:copy-of select="$content"/>
                </li>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="den-ast-disabled">
        <xsl:param name="den-document"/>
        <xsl:variable name="den-document-id" select="generate-id()"/>
        <xsl:variable name="den-document-hide-current-note-id" select="concat($den-document-id, 'hide-current-node')"/>
        <div>
            <xsl:choose>
                <xsl:when test="true() = s:can-show-children-in-row($den-document)">
                    <xsl:attribute name="class" select="'perspective show-in-row'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class" select="'perspective'"/>
                </xsl:otherwise>
            </xsl:choose>
            <input class="hide-children" type="checkbox">
                <xsl:attribute name="id" select="$den-document-id"/>
            </input>
            <input class="hide-current-node" type="checkbox">
                <xsl:attribute name="id" select="$den-document-hide-current-note-id"/>
            </input>
            <!--div>
                <div class="perspective-options">
                    <label class="perspective-hide-current-node perspective-value-prefix">
                        <xsl:attribute name="for" select="$den-document-hide-current-note-id"/>...
                    </label>
                </div>
            </div-->
            <xsl:if test="count($den-document/*)">
                <label class="perspective-menu">
                    <xsl:attribute name="for" select="$den-document-id"/>
                </label>
            </xsl:if>
            <xsl:variable name="perspective-namespace">
                <xsl:if test="name($den-document) != 'n:val'">
                    <label class="perspective-namespace">
                        <xsl:attribute name="for" select="$den-document-id"/>
                        <xsl:value-of select="name($den-document)"/>
                    </label>
                </xsl:if>
            </xsl:variable>
            <xsl:variable name="perspective-value">
                <xsl:choose>
                    <xsl:when test="$den-document/@xl:href">
                        <a>
                            <xsl:attribute name="href" select="$den-document/@xl:href"/>
                            <xsl:choose>
                                <xsl:when test="normalize-space($den-document/@name) = ''">
                                    <xsl:value-of select="$den-document/@xl:href"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$den-document/@name"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </a>
                    </xsl:when>
                    <xsl:when test="$den-document/@name">
                        <xsl:value-of select="$den-document/@name"/>
                    </xsl:when>
                    <xsl:otherwise/>
                </xsl:choose>
            </xsl:variable>
            <xsl:copy-of select="$perspective-namespace"/>
            <xsl:if test="$perspective-namespace != '' and $perspective-value != ''">
                <div class="perspective-namespace-link-to-value"/>
            </xsl:if>
            <xsl:if test="$perspective-value != ''">
                <div class="perspective-value">
                    <xsl:copy-of select="$perspective-value"/>
                </div>
            </xsl:if>
            <div class="perspective-children">
                <xsl:for-each select="$den-document/node()">
                    <xsl:choose>
                        <xsl:when test="self::text()">
                            <xsl:if test="normalize-space(.) != ''">
                                <div class="perspective">
                                    <div class="perspective-value">
                                        <xsl:choose>
                                            <xsl:when test="true() = s:can-show-text-as-line(.)">
                                                <xsl:for-each select="tokenize(., '\n\n')">
                                                    <div class="paragraph">
                                                        <xsl:value-of select="normalize-space(.)"/>
                                                    </div>
                                                </xsl:for-each>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="."/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </div>
                                </div>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="."
                                                 mode="perspective"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>