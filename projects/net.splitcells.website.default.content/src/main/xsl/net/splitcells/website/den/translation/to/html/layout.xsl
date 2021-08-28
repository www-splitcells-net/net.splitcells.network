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
    <!-- Defines the standard html layout. -->
    <!-- TODO s:root-relative-url( -->
    <!-- TODO PERFORMANCE Do not import libraries which are not needed. -->
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
                <link rel="image_src" type="image/svg+xml">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:root-relative-url('images/license.standard/thumbnail/small/starting-to-learn-how-to-draw-a-face.jpg')"/>
                    </xsl:attribute>
                </link>
                <link rel="icon" type="image/svg+xml">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/images/icon.svg')"/>
                    </xsl:attribute>
                </link>
                <link rel="alternate icon">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/images/icon.svg')"/>
                    </xsl:attribute>
                </link>
                <link rel="mask-icon" type="image/svg+xml">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/images/icon.svg')"/>
                    </xsl:attribute>
                </link>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <xsl:element name="link">
                    <!-- Some Mobile browsers only support pngs as favicons. -->
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'apple-touch-icon'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/images/icon.png')"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="title">
                    <xsl:value-of select="concat(./s:title, ' / ', $siteName)"/>
                </xsl:element>
                <xsl:element name="link">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('feed.rss.xml')"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'application/rss+xml'"/>
                    </xsl:attribute>
                    <xsl:attribute name="ref">
                        <xsl:value-of select="'alternate'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'Latest 10 blog posts (RSS)'"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="link">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('feed.atom.xml')"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'application/atom+xml'"/>
                    </xsl:attribute>
                    <xsl:attribute name="ref">
                        <xsl:value-of select="'alternate'"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'Latest 10 blog posts (Atom)'"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="link">
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/css'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:default-root-relative-url('/css/theme.white.variables.css')"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="link">
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/css'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:default-root-relative-url('/css/basic.themed.css')"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="link">
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/css'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:default-root-relative-url('/css/basic.css')"/>
                    </xsl:attribute>
                </xsl:element>
                <link rel="stylesheet" type="text/css" media="none">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:default-root-relative-url('/css/layout.column.main.fullscreen.css')"/>
                    </xsl:attribute>
                </link>
                <link rel="stylesheet" type="text/css">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:default-root-relative-url('/css/den.css')"/>
                    </xsl:attribute>
                </link>
                <xsl:element name="link">
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/css'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:default-root-relative-url('/css/layout.default.css')"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="link">
                    <!-- TODO Support for different themes. -->
                    <xsl:attribute name="rel">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'text/css'"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:default-root-relative-url('/js/syntaxhighlighter.white/theme.css')"/>
                    </xsl:attribute>
                </xsl:element>
            </head>
            <body>
                <main id="topElement">
                    <div class="splitcells-net-background-window minimal-only net-splitcells-website-column-background-1">
                        <div class="splitcells-net-window-menu splitcells-net-background-window-menu"></div>
                    </div>
                    <div class="menu Left_shadow TextCell Layout Standard_p4"
                         style="width: 13em; vertical-align: top; visibility: inherit; display: flex; flex-direction: column; padding-bottom: 1em; order: 2;">
                        <div class="Left_shadow structural_guide splitcells-net-title-logo splitcells-net-window-menu">
                            <div class="splitcells-net-window-menu-line-1">
                                <a class="net-splitcells-button net-splitcells-main-button-project-logo">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/index.html')"/>
                                    </xsl:attribute>
                                </a>
                                <a class="net-splitcells-button">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/index.html')"/>
                                    </xsl:attribute>
                                    <xsl:value-of select="$siteName"/>
                                </a>
                                <a class="net-splitcells-button" href="#">nos</a>
                            </div>
                            <div class="structural_guide"></div>
                            <div class="splitcells-net-window-menu-line-2">
                                <input type="text" class="net-splitcells-search-bar"
                                       placeholder="search (TODO)"></input>
                            </div>
                        </div>
                        <br></br>
                        <a class="net-splitcells-button net-splitcells-component-priority-3" href="#content">
                            Content
                        </a>
                        <a class="net-splitcells-button premature">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/premature-content.html')"/>
                            </xsl:attribute>
                            Tabs
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/legal/impressum.html')"/>
                            </xsl:attribute>
                            Impressum
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/legal/privacy-policy.html')"/>
                            </xsl:attribute>
                            Privacy Policy
                        </a>
                        <div class="messages">
                            <h3>Messages</h3>
                            <div class="noScriptMessage TextCell text_error"
                                 style="padding-left: 1em; font-weight: bold;">- Activate Javascript in
                                order to enable all
                                functions
                                of this site.
                            </div>
                            <br/>
                        </div>
                        <h3>Sections</h3>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/info/about-this-site.html')"/>
                            </xsl:attribute>
                            About
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of select="s:root-relative-url('/info/contact.html')"/>
                            </xsl:attribute>
                            Contact
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/legal/impressum.html')"/>
                            </xsl:attribute>
                            Impressum
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/legal/licensing.html')"/>
                            </xsl:attribute>
                            Licensing
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/index.html')"/>
                            </xsl:attribute>
                            Main Page
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/legal/privacy-policy.html')"/>
                            </xsl:attribute>
                            Privacy Policy
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/programs/index.html')"/>
                            </xsl:attribute>
                            Programs
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/projects/index.html')"/>
                            </xsl:attribute>
                            Projects
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/programs/search.html')"/>
                            </xsl:attribute>
                            Search
                        </a>
                        <h3>Local Instances</h3>
                        <a class="net-splitcells-button net-splitcells-component-priority-3"
                           href="javascript:void(0);"
                           onclick="const Http = new XMLHttpRequest(); Http.open('GET', '/net/splitcells/website/layout/build'); Http.send();">
                            Refresh layout.
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3"
                           href="http://splitcells.net">
                            Public Site
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3"
                           href="https://localhost:8443">
                            Local Public Site
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3 user-specific"
                           href="https://localhost:8444">Local Private Site
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3 user-specific"
                           href="http://localhost:1313/">
                            Local Hugo Site
                        </a>
                        <h3>Mārtiņš Avots's Support System</h3>
                        <a class="net-splitcells-button net-splitcells-component-priority-3 user-specific"
                           href="https://localhost:8444/net/splitcells/martins/avots/support/system/index.html">
                            Root
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3 user-specific"
                           href="https://localhost:8444/net/splitcells/martins/avots/support/system/projects.html">
                            Discovery
                        </a>
                        <div style="display: none; visibility: hidden">
                            <h3>Additional Functions</h3>
                            <a class="net-splitcells-button net-splitcells-component-priority-3">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="s:root-relative-url('/feed.atom.xml')"/>
                                </xsl:attribute>
                                Atom feed
                            </a>
                            <a class="net-splitcells-button net-splitcells-component-priority-3">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="s:root-relative-url('/feed.rss.xml')"/>
                                </xsl:attribute>
                                Rss feed
                            </a>
                        </div>
                        <h3>Interesting Third Party Sites</h3>
                        <div class="advertise-one-of">
                            <xsl:for-each
                                    select="document('/net.splitcells.website/current/xml/net/splitcells/martins/avots/website/advertisement-object-social.xml')/rdf:RDF/rdf:Description">
                                <div>
                                    <xsl:if test="position() != 1">
                                        <xsl:attribute name="style"
                                                       select="'display: none; visibility: hidden;'"/>
                                    </xsl:if>
                                    <a class="net-splitcells-button net-splitcells-component-priority-3">
                                        <xsl:attribute name="href" select="@rdf:resource"/>
                                        <xsl:value-of select="./rdf:label"/>
                                    </a>
                                    <a class="net-splitcells-button net-splitcells-component-priority-4">
                                        <xsl:attribute name="href" select="@rdf:resource"/>
                                        <xsl:value-of select="./rdf:comment"/>
                                    </a>
                                </div>
                            </xsl:for-each>
                        </div>
                        <div class="advertise-one-of">
                            <xsl:for-each
                                    select="document('/net.splitcells.website/current/xml/net/splitcells/martins/avots/website/advertisement-object-technical.xml')/rdf:RDF/rdf:Description">
                                <div>
                                    <xsl:if test="position() != 1">
                                        <xsl:attribute name="style"
                                                       select="'display: none; visibility: hidden;'"/>
                                    </xsl:if>
                                    <a class="net-splitcells-button net-splitcells-component-priority-3">
                                        <xsl:attribute name="href" select="@rdf:resource"/>
                                        <xsl:value-of select="./rdf:label"/>
                                    </a>
                                    <div class="net-splitcells-button net-splitcells-component-priority-4">
                                        <xsl:attribute name="href" select="@rdf:resource"/>
                                        <xsl:value-of select="./rdf:comment"/>
                                    </div>
                                </div>
                            </xsl:for-each>
                        </div>
                        <h3>This Site Uses Amongst Others</h3>
                        <a class="net-splitcells-button net-splitcells-component-priority-3"
                           href="https://alexgorbatchev.com/SyntaxHighlighter/">
                            SyntaxHighlighter
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-4"
                           href="https://alexgorbatchev.com/SyntaxHighlighter/">
                            for Code Syntax Highlighting
                        </a>
                        <h3>Metadata About This Document</h3>
                        <p>Unless otherwise noted, the
                            content of this
                            html file is licensed under the
                            <a href="/net/splitcells/network/legal/licenses/EPL-2.0.html">EPL-2.0</a>
                            OR <a href="/net/splitcells/network/legal/licenses/MIT.html">MIT</a>.
                        </p>
                        <p>Files and other contents, which are linked to by this
                            HTML file, have their own rulings.
                        </p>
                        <h4>Validation</h4>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, '/', ./s:path/text(), '/', substring(./s:name, 1, string-length(./s:name) - 4), '.html')"/>
                            HTML Validation
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/basic.themed.css'))"/>
                            basic.themed.css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/basic.css'))"/>
                            basic.css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/theme.white.variables.css'))"/>
                            theme.white.variables .css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/layout.column.main.fullscreen.css'))"/>
                            layout.column.main. fullscreen.css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/den.css'))"/>
                            den.css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/layout.default.css'))"/>
                            layout.default.css
                        </a>
                        <a class="net-splitcells-button net-splitcells-component-priority-3">
                            <xsl:attribute name="href"
                                           select="concat('http://jigsaw.w3.org/css-validator/validator?uri=', $site.url, s:default-root-relative-url('/css/theme.css'))"/>
                            theme.css
                        </a>
                        <div style="flex: auto;"></div>
                        <h3>Footer Functions</h3>
                        <a class="net-splitcells-button net-splitcells-component-priority-3" href="#topElement">
                            back to top
                        </a>
                    </div>
                    <div class="net-splitcells-content-column">
                        <div id="content"
                             class="net-splitcells-content-main Standard_content Right_shadow">
                            <div class="optional_structural_guide structural_guide"></div>
                            <article>
                                <div class="Right_shadow splitcells-net-window-menu">
                                    <div class="Standard_p3 bottomLightShadow splitcells-net-line">
                                        <a class="HeaderButton_structure HeaderButton net-splitcells-main-button-project-logo">
                                            <xsl:attribute name="href">
                                                <xsl:value-of
                                                        select="s:root-relative-url('/index.html')"/>
                                            </xsl:attribute>
                                        </a>
                                        <div class="HeaderButton_structure HeaderButton_p2">
                                            <xsl:element name="a">
                                                <xsl:attribute name="class">
                                                    <xsl:value-of select="'NonStandard'"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                            select="s:root-relative-url('/dedicated-menu-page.html')"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="style">
                                                    <xsl:value-of select="'z-index: 1;'"/>
                                                </xsl:attribute>
                                                Menu
                                            </xsl:element>
                                        </div>
                                        <div class="optional HeaderButton_structure premature">
                                            <xsl:element name="a">
                                                <xsl:attribute name="class">
                                                    <xsl:value-of select="'NonStandard'"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                            select="s:root-relative-url('/premature-content.html')"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="style">
                                                    <xsl:value-of select="'z-index: 1;'"/>
                                                </xsl:attribute>
                                                Tabs
                                            </xsl:element>
                                        </div>
                                        <div class="HeaderButton_structure HeaderButton_p2">
                                            <xsl:element name="a">
                                                <xsl:attribute name="class">
                                                    <xsl:value-of select="'NonStandard'"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                            select="s:root-relative-url('/legal/impressum.html')"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="style">
                                                    <xsl:value-of select="'z-index: 1;'"/>
                                                </xsl:attribute>
                                                Impressum
                                            </xsl:element>
                                        </div>
                                        <div class="HeaderButton_structure HeaderButton_p2">
                                            <xsl:element name="a">
                                                <xsl:attribute name="class">
                                                    <xsl:value-of select="'NonStandard'"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                            select="s:root-relative-url('/legal/privacy-policy.html')"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="style">
                                                    <xsl:value-of select="'z-index: 1;'"/>
                                                </xsl:attribute>
                                                Privacy Policy
                                            </xsl:element>
                                        </div>
                                        <div class="page_header_backToContent_1 HeaderButton_structure HeaderButton_highlighted"
                                             style="visibility: hidden; display: none;"
                                             onclick="javascript:
                                                        unshowByCssClass('page_header_backToContent_2');
										                unshowByCssClass('page_header_backToContent_1');
										                showByCssClass('menu');
										                showByCssClass('contentCell');
										                showByCssClass('rightDecoration');">
                                            back to content
                                        </div>
                                        <div style="flex: auto;"></div>
                                        <div class="HeaderButton_structure HeaderButton_p2 page-column-0-full-screen minimal-not"
                                             onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-0-full-screen');
										                unshowByCssClass('column_1');
										                showByCssClass('page-column-0-windowed');
													">
                                            wide screen
                                        </div>
                                        <div class="HeaderButton_structure HeaderButton_p2 page-column-0-windowed optional"
                                             style="visibility: hidden; display: none;"
                                             onclick="javascript: fullScreenDisable();
													hide('page-column-0-windowed');
														unshowByCssClass('page-column-0-windowed');
														showByCssClass('page-column-0-full-screen');
														showByCssClass('column_1');">
                                            windowed
                                        </div>
                                    </div>
                                    <div class="structural_guide" style="height: .25em;"></div>
                                    <div class="Standard_highlighted"
                                         style="display: flex; flex-direction: row; padding-left: .25em; flex: auto;">
                                        <div style="font-size: 1.75em; text-align: left; padding: 0.1em; font-weight: bold;">
                                            <xsl:if test="./s:title.detailed">
                                                <xsl:value-of select="./s:title.detailed"/>
                                            </xsl:if>
                                            <xsl:if test="not(./s:title_detailed)">
                                                <xsl:value-of select="./s:title"/>
                                            </xsl:if>
                                        </div>
                                        <div style="flex: auto;"></div>
                                    </div>
                                </div>
                                <!-- TODO IDEA xsl:if test="./s:content/s:meta/s:descriptive_imagery">
                                    <div class="standardborder Standard_highlighted highlightedShortSummary
                                        highlightedShortSummary"
                                         style="padding: 0em; margin-bottom: 1.4em; display: flex; flex-direction: row;">
                                        <div>
                                            <xsl:attribute name="style">
                                                <xsl:text>width: 50%; height: 10em; background-size: 100% auto; background-repeat: no-repeat; background-image: url('</xsl:text>
                                                <xsl:copy-of
                                                        select="s:image_thumbnail_medium_location(./s:content/s:meta/s:descriptive_imagery/*[1]/@license , ./s:content/s:meta/s:descriptive_imagery/*[1])"/>
                                                <xsl:text>');</xsl:text>
                                            </xsl:attribute>
                                            <xsl:text> </xsl:text>
                                        </div>
                                        <div style="width: 50%; padding-top: .5em;">
                                            <xsl:if test="./s:content/s:meta/s:description">
                                                <div class="IndexPostHeader">
                                                    <div class="highlighted_paragraph"
                                                         style="font-weight: 400; text-align: justify; margin-top: .5em;">
                                                        <xsl:text> </xsl:text>
                                                        <xsl:apply-templates
                                                                select="./s:content/s:meta/s:description/node()"/>
                                                    </div>
                                                </div>
                                            </xsl:if>
                                        </div>
                                    </div>
                                </xsl:if-->
                                <xsl:for-each select="./s:content/node()">
                                    <xsl:apply-templates select="."/>
                                </xsl:for-each>
                            </article>
                        </div>

                        <xsl:if test="$column_1 != ''">
                            <div class="column_1 contentCell Standard_content Right_shadow"
                                 style="order: 4;">
                                <div class="optional_structural_guide structural_guide"
                                     style="width:.25em;"></div>
                                <article class="Standard_p2 net-splitcells-component-priority-2">
                                    <xsl:if test="$column_1 != ''">
                                        <div class="Right_shadow Standard_p2 splitcells-net-window-menu">
                                            <div class="Standard_p3 bottomLightShadow splitcells-net-line">
                                                <div style="flex: auto;"></div>
                                                <div class="HeaderButton_structure HeaderButton_p2 page-column-1-full-screen optional minimal-not"
                                                     onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-1-full-screen');
										                unshowByCssClass('net-splitcells-content-main');
										                showByCssClass('page-column-1-windowed');">
                                                    wide screen
                                                </div>
                                                <div class="HeaderButton_structure HeaderButton_p2 page-column-1-windowed optional"
                                                     style="visibility: hidden; display: none;"
                                                     onclick="javascript: fullScreenDisable();
													hide('page-column-1-windowed');
														unshowByCssClass('page-column-1-windowed');
														showByCssClass('page-column-1-full-screen');
														showByCssClass('net-splitcells-content-main');">
                                                    windowed
                                                </div>
                                            </div>
                                            <div class="structural_guide" style="height: .25em;"></div>
                                            <div class="Standard_highlighted"
                                                 style="display: flex; flex-direction: row; padding-left: .25em; flex: auto;">
                                                <div style="font-size: 1.75em; text-align: left; padding: 0.1em;">
                                                </div>
                                                <div style="flex: auto;"></div>
                                            </div>
                                        </div>
                                    </xsl:if>
                                    <xsl:apply-templates select="$column_1"/>
                                </article>
                            </div>
                        </xsl:if>
                    </div>
                    <div class="rightDecoration Right_shadow"
                         style="width: 4.5em; display: flex; flex-direction: row; z-index: 90; order: 5;">
                        <div class="Borderless Standard_p2 Layout decorationBoxRight"
                             style="position: relative; z-index: 2; width: 1.5em;"></div>
                        <div class="Borderless Standard_p3 Layout decorationBoxRight"
                             style="position: relative; z-index: 3; width: 1.5em;"></div>
                        <div class="Borderless Standard_p4 Layout decorationBoxRight"
                             style="position: relative; z-index: 4; width: 1.5em;"></div>
                    </div>
                    <div class="splitcells-net-background-window minimal-only" style="order: 6;">
                        <div class="splitcells-net-window-menu splitcells-net-background-window-menu"></div>
                    </div>
                </main>
                <footer class="Standard_p5 topLightShadow"/>
                <script type="text/javascript" charset="utf-8">
                    <xsl:attribute name="src">
                        <xsl:value-of select="s:root-relative-url('/js/basic.js')"/>
                    </xsl:attribute>
                </script>
                <script type="text/javascript" charset="utf-8">
                    <xsl:attribute name="src">
                        <xsl:value-of select="s:root-relative-url('/js/basic.default.js')"/>
                    </xsl:attribute>
                </script>
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
                <script type="text/javascript">
                    function enableStylesheet (node) {
                    node.media =
                    '';
                    }

                    function disableStylesheet (node) {
                    node.media
                    = 'none';
                    }
                    function fullScreenEnable() {
                    document
                    .querySelectorAll("link[href$='/css/layout.column.main.fullscreen.css']")
                    .forEach(enableStylesheet);
                    }
                    function
                    fullScreenDisable() {
                    document
                    .querySelectorAll("link[href$='/css/layout.column.main.fullscreen.css']")
                    .forEach(disableStylesheet);
                    }
                    /*TODO
                    document
                    .querySelectorAll("link[href='/white/css/theme.white.yellow.variables.css']")
                    .forEach(enableStylesheet);
                    document
                    .querySelectorAll("link[href='/white/css/theme.white.variables.css']")
                    .forEach(disableStylesheet);*/
                </script>
                <script type="text/javascript">
                    apply_to_elements_of('advertise-one-of', function(element){unshowAllChildren(element);});
                    apply_to_elements_of('advertise-one-of', function(element){showOneOfChildren(element);});
                    checkAvailibility('net-splitcells-website-log-error');
                </script>
                <!-- Integration of https://www.mathjax.org. TODO Use local copy in future. -->
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