<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:ns="http://splitcells.net/namespace.xsd">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR MIT
    -->
    <!-- Defines the standard html layout. -->
    <!-- TODO s:root-relative-url(
    TODO PERFORMANCE Do not import libraries which are not needed.
    TODO Minimize number of elements with css classes,
         in order to improve compatibility with external css stylesheet.-->
    <xsl:template match="/ns:*">
        <!-- TODO Make complete. -->
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:value-of select="./@full-name"/>
                </s:title>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:copy-of select="./node()"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/d:*">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:value-of select="./@full-name"/>
                </s:title>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:apply-templates select="." mode="perspective"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/n:*">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:value-of select="./@full-name"/>
                </s:title>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:apply-templates select="." mode="perspective"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="s:html-body-content">
        <xsl:value-of select="./node()"/>
    </xsl:template>
    <xsl:template match="/n:text">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:value-of select="./@full-name"/>
                </s:title>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:apply-templates select="./node()" mode="natural-text"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/s:choice">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:apply-templates select="./s:meta/s:title/node()"/>
                </s:title>
                <s:license>
                    <xsl:copy-of select="./s:meta/s:license/node()"/>
                </s:license>
                <s:publication_date>
                    <xsl:value-of select="./s:meta/s:publication_date"/>
                </s:publication_date>
                <xsl:if test="./s:meta/s:redirect">
                    <s:redirect>
                        <xsl:copy-of select="./s:meta/s:redirect/node()"/>
                    </s:redirect>
                </xsl:if>
                <xsl:if test="./s:meta/s:republication">
                    <s:republication>
                        <xsl:copy-of select="./s:meta/s:republication/node()"/>
                    </s:republication>
                </xsl:if>
                <s:content>
                    <xsl:copy-of select="./s:option"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/s:article">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title>
                    <xsl:apply-templates select="./s:meta/s:title/node()"/>
                </s:title>
                <s:license>
                    <xsl:copy-of select="./s:meta/s:license/node()"/>
                </s:license>
                <s:publication_date>
                    <xsl:value-of select="./s:meta/s:publication_date"/>
                </s:publication_date>
                <xsl:if test="./s:meta/s:redirect">
                    <s:redirect>
                        <xsl:copy-of select="./s:meta/s:redirect/node()"/>
                    </s:redirect>
                </xsl:if>
                <xsl:if test="./s:meta/s:republication">
                    <s:republication>
                        <xsl:copy-of select="./s:meta/s:republication/node()"/>
                    </s:republication>
                </xsl:if>
                <s:content>
                    <xsl:copy-of select="./node()"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config">
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="s:layout.config">
        <!-- TODO HTML preemble is missing. -->
        <xsl:variable name="last-element-length" select="string-length(tokenize(./s:path/text(), '/')[last()])"/>
        <xsl:variable name="folder"
                      select="substring(./s:path/text(), 1, (string-length(./s:path/text()) - $last-element-length))"/>
        <xsl:variable name="column_1">
            <div class="net-splitcells-website-log-error net-splitcells-website-hidden-by-default">
                <s:option>
                    <s:name>Page's Errors</s:name>
                    <s:url>
                        <xsl:value-of
                                select="concat(./s:name, '.errors.txt')"/>
                    </s:url>
                </s:option>
            </div>
            <xsl:variable name="column_1_tmp">
                <xsl:call-template name="content.outline">
                    <xsl:with-param name="content" select="./s:content"/>
                    <xsl:with-param name="style" select="'Standard_p2'"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:if test="./s:content/s:meta/s:description/node() != ''">
                <s:chapter>
                    <s:title>Description</s:title>
                    <s:paragraph>
                        <xsl:apply-templates select="./s:content/s:meta/s:description/node()"/>
                    </s:paragraph>
                </s:chapter>
            </xsl:if>
            <xsl:if test="$column_1_tmp != ''">
                <s:chapter>
                    <s:title>Outline</s:title>
                    <xsl:copy-of select="$column_1_tmp"/>
                </s:chapter>
            </xsl:if>
            <xsl:if test="./s:content/s:meta/s:related_to">
                <xsl:message select="concat('Deprecated tag: ./s:content/s:meta/s:related_to:', document-uri(/))"/>
            </xsl:if>
            <s:chapter>
                <s:title>Local Path Context</s:title>
                <xsl:variable name="file-path-environment">
                    <xsl:call-template name="file-path-environment">
                        <xsl:with-param name="path"
                                        select="./s:path/node()"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="file-path-environment">
                    <xsl:with-param name="path"
                                    select="./s:path/node()"/>
                </xsl:call-template>
            </s:chapter>
            <xsl:if test="./s:content/s:meta/d:toDo or ./s:content/s:meta/d:todo">
                <s:chapter>
                    <s:title>Open Tasks</s:title>
                    <xsl:copy-of select="./s:content/s:meta/d:toDo"/>
                </s:chapter>
            </xsl:if>
            <xsl:if test="./s:content/s:meta/rdf:RDF">
                <s:chapter>
                    <s:title>Resources</s:title>
                    <xsl:apply-templates select="./s:content/s:meta/rdf:RDF/node()"/>
                </s:chapter>
            </xsl:if>
        </xsl:variable>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8"/>
                <!-- Disable caching, so that CSS styling is reloading in webbrowsers on CSS updates automatically. -->
                <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
                <meta http-equiv="Pragma" content="no-cache"/>
                <meta http-equiv="Expires" content="0"/>
                <link rel="stylesheet" type="text/css"
                      href="https://cdnjs.cloudflare.com/ajax/libs/tufte-css/1.8.0/tufte.min.css"/>
            </head>
            <body>
                <main>
                    <article>
                        <section>
                            <xsl:for-each select="./s:content/node()">
                                <xsl:apply-templates select="."/>
                            </xsl:for-each>
                        </section>
                    </article>

                </main>
                <xsl:element name="script">
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/javascript'"/>
                    </xsl:attribute>
                    <xsl:attribute name="charset">
                        <xsl:value-of select="'utf-8'"/>
                    </xsl:attribute>
                    <xsl:attribute name="src">
                        <!-- TODO Multiple theme support -->
                        <xsl:value-of select="s:root-relative-url('/js/syntaxhighlighter.white/syntaxhighlighter.js')"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:if test="./s:redirect">
                    <xsl:variable name="redirect.target">
                        <xsl:call-template name="link.target">
                            <xsl:with-param name="linkNode" select="./s:redirect"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:element name="script">
                        <xsl:attribute name="type">
                            <xsl:value-of select="'text/javascript'"/>
                        </xsl:attribute>
                        <xsl:value-of
                                select="concat('window.location.href=', $apostroph, $redirect.target, $apostroph)"/>
                    </xsl:element>
                </xsl:if>
                <xsl:if test="./s:republication">
                    <xsl:variable name="redirect.target">
                        <xsl:call-template name="link.target">
                            <xsl:with-param name="linkNode" select="./s:republication"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:element name="script">
                        <xsl:attribute name="type">
                            <xsl:value-of select="'text/javascript'"/>
                        </xsl:attribute>
                        <xsl:value-of
                                select="concat('window.location.href=', $apostroph, $redirect.target, $apostroph)"/>
                    </xsl:element>
                </xsl:if>
                <script id="MathJax-script" src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
                <script type="text/x-mathjax-config">
                    MathJax = {
                    tex: {
                    inlineMath: [['$', '$'], ["\\(", "\\)"]],
                    processEscapes: true,
                    }
                    }
                </script>
                <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
                <script id="MathJax-script" async=""
                        src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="/*" priority="-1">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <xsl:call-template name="s:path-of">
                    <xsl:with-param name="document" select="."/>
                </xsl:call-template>
                <s:title/>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:call-template name="den-ast">
                        <xsl:with-param name="den-document" select="."/>
                    </xsl:call-template>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
</xsl:stylesheet>