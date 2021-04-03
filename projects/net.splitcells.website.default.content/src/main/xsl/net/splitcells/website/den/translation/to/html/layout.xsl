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
                                select="concat('/net/splitcells/martins/avots/website/log/', ./s:path, '/', ./s:name, '.errors.txt')"/>
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
                                select="s:root-relative-url('/css/theme.white.variables.css')"/>
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
                        <xsl:value-of select="s:root-relative-url('/css/basic.themed.css')"/>
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
                        <xsl:value-of select="s:root-relative-url('/css/basic.css')"/>
                    </xsl:attribute>
                </xsl:element>
                <link rel="stylesheet" type="text/css" media="none">
                    <xsl:attribute name="href">
                        <xsl:value-of
                                select="s:root-relative-url('/css/layout.column.main.fullscreen.css')"/>
                    </xsl:attribute>
                </link>
                <link rel="stylesheet" type="text/css">
                    <xsl:attribute name="href">
                        <xsl:value-of select="s:root-relative-url('/css/den.css')"/>
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
                                select="s:root-relative-url('/css/layout.default.css')"/>
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
                                select="s:root-relative-url('/js/syntaxhighlighter.white/theme.css')"/>
                    </xsl:attribute>
                </xsl:element>
            </head>
            <body>
                <main id="topElement"
                      class="Standard_highlighted topElement beginningOfContent background_not_important topLightShadow mainElement Layout mainElement_Container">
                    <div class="splitcells-net-background-window minimal-only net-splitcells-website-column-background-1">
                        <div class="splitcells-net-window-menu splitcells-net-background-window-menu"></div>
                    </div>
                    <div class="menu Left_shadow TextCell Layout Standard_p4"
                         style="width: 13em; vertical-align: top; visibility: inherit; display: flex; flex-direction: column; padding-bottom: 1em; order: 2;">
                        <div class="title_logo Left_shadow structural_guide splitcells-net-title-logo splitcells-net-window-menu"
                             style="width: 13em; padding-top: .5em;">
                            <a class="NonStandard underlined_shadow page_header_sitename_normal">
                                <span style="font-size: 1.5em;">
                                    <xsl:value-of select="$siteName"/>
                                </span>
                            </a>
                            <div class="Text"
                                 style="font-weight: normal; position: relative; left: 1em;">by
                                Mārtiņš Avots
                            </div>
                            <div class="Text"
                                 style="font-weight: normal; font-size: 0.75em; position: relative; right: .5em;">
                                N O S
                            </div>
                        </div>
                        <br></br>
                        <a class="linkButton MainButton MainButton_S" href="#content">
                            Content
                        </a>
                        <a class="linkButton MainButton MainButton_S premature">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2016/10/10/premature-content.html')"/>
                            </xsl:attribute>
                            Tabs
                        </a>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/01/impressum.html')"/>
                            </xsl:attribute>
                            Impressum
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/01/privacy-policy.html')"/>
                            </xsl:attribute>
                            Privacy Policy
                        </xsl:element>
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
                        <h3 style="padding-left: .25em;">Sections</h3>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/04/about-this-site.html')"/>
                            </xsl:attribute>
                            About
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of select="s:root-relative-url('/2015/03/03/contact.html')"/>
                            </xsl:attribute>
                            Contact
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of select="s:root-relative-url('/2015/03/06/gallery.html')"/>
                            </xsl:attribute>
                            Gallery
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/01/impressum.html')"/>
                            </xsl:attribute>
                            Impressum
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/02/licensing.html')"/>
                            </xsl:attribute>
                            Licensing
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2017/12/31/updated-main-page.html')"/>
                            </xsl:attribute>
                            Main Page
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/05/post-archive.html')"/>
                            </xsl:attribute>
                            Post Archive
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/2015/03/01/privacy-policy.html')"/>
                            </xsl:attribute>
                            Privacy Policy
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/programs/programs.html')"/>
                            </xsl:attribute>
                            Programs
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/projects.html')"/>
                            </xsl:attribute>
                            Projects
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:root-relative-url('/programs/search.html')"/>
                            </xsl:attribute>
                            Search
                        </xsl:element>
                        <h3 style="padding-left: .25em;">Instances</h3>
                        <a class="linkButton MainButton MainButton_S" href="http://splitcells.net">
                            Public Site
                        </a>
                        <a class="linkButton MainButton MainButton_S" href="https://localhost:8443">
                            Local Public Site
                        </a>
                        <a class="linkButton MainButton MainButton_S user-specific"
                           href="https://localhost:8444">Local Private Site
                        </a>
                        <h3 style="padding-left: .25em;">Mārtiņš Avots's Support System</h3>
                        <a class="linkButton MainButton MainButton_S user-specific"
                           href="https://localhost:8444/net/splitcells/martins/avots/support/system/index.html">
                            Root
                        </a>
                        <a class="linkButton MainButton MainButton_S user-specific"
                           href="https://localhost:8444/net/splitcells/martins/avots/support/system/projects.html">
                            Discovery
                        </a>
                        <div style="display: none; visibility: hidden">
                            <h3 style="padding-left: .25em;">Additional Functions</h3>
                            <xsl:element name="a">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                                </xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="s:root-relative-url('/feed.atom.xml')"/>
                                </xsl:attribute>
                                Atom feed
                            </xsl:element>
                            <xsl:element name="a">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="'linkButton MainButton MainButton_S'"/>
                                </xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="s:root-relative-url('/feed.rss.xml')"/>
                                </xsl:attribute>
                                Rss feed
                            </xsl:element>
                        </div>
                        <h3 style="padding-left: .25em;">Layouts</h3>
                        <a class="linkButton MainButton MainButton_S"
                           href="/net/splitcells/martins/avots/website/experimental/browser.layout.html">
                            Experimental Browser
                        </a>
                        <a class="linkButton MainButton MainButton_S"
                           href="/net/splitcells/martins/avots/website/experimental/desktop.layout.html">
                            Experimental Desktop
                        </a>
                        <h3 style="padding-left: .25em;">Interesting Third Party Sites</h3>
                        <div class="advertise-one-of">
                            <xsl:for-each select="$advertisement-object-social/rdf:RDF/rdf:Description">
                                <a class="linkButton">
                                    <xsl:if test="position() != 1">
                                        <xsl:attribute name="style"
                                                       select="'display: none; visibility: hidden;'"/>
                                    </xsl:if>
                                    <xsl:attribute name="href" select="@rdf:resource"/>
                                    <div class="MainButton MainButton_S">
                                        <xsl:value-of select="./rdf:label"/>
                                        <div class="MainButtonDescription Standard_p4"
                                             style="text-indent: 0em; display: block; font-weight: normal; padding-left: .25em;">
                                            <xsl:value-of select="./rdf:comment"/>
                                        </div>
                                    </div>
                                </a>
                            </xsl:for-each>
                        </div>
                        <div class="advertise-one-of">
                            <xsl:for-each
                                    select="$advertisement-object-technical/rdf:RDF/rdf:Description">
                                <a class="linkButton">
                                    <xsl:if test="position() != 1">
                                        <xsl:attribute name="style"
                                                       select="'display: none; visibility: hidden;'"/>
                                    </xsl:if>
                                    <xsl:attribute name="href" select="@rdf:resource"/>
                                    <div class="MainButton MainButton_S">
                                        <xsl:value-of select="./rdf:label"/>
                                        <div class="MainButtonDescription Standard_p4"
                                             style="text-indent: 0em; display: block; font-weight: normal; padding-left: .25em;">
                                            <xsl:value-of select="./rdf:comment"/>
                                        </div>
                                    </div>
                                </a>
                            </xsl:for-each>
                        </div>
                        <h4 style="padding-left: .25em;">This Site Uses Amongst Others</h4>
                        <a class="linkButton" href="https://alexgorbatchev.com/SyntaxHighlighter/">
                            <div class="MainButton MainButton_S">
                                SyntaxHighlighter
                                <div class="MainButtonDescription Standard_p4"
                                     style="text-indent: 0em; display: block; font-weight: normal; padding-left: .25em;">
                                    Code Syntax Highlighter
                                </div>
                            </div>
                        </a>
                        <h3 style="padding-left: .25em;">Metadata About This Document</h3>
                        <xsl:if test="./s:license">
                            <xsl:if test="./s:license/node() = 'standard'">
                                <a class="linkButton" href="/license-CC-BY-SA-4.txt">
                                    <div class="MainButtonDescription Standard_p3"
                                         style="text-indent: 0em; display: block; font-weight: normal;">
                                        <div style="padding-left: 0.25em;">Unless otherwise noted, the
                                            content of this
                                            html file is licensed under the
                                        </div>
                                        <div class="MainButtonDescription Standard_p2"
                                             style="padding-left: 0.25em;">
                                            Creative Commons
                                            Attribution-ShareAlike 4.0
                                            International Public
                                            License.
                                        </div>
                                    </div>
                                </a>
                            </xsl:if>
                        </xsl:if>
                        <div class="MainButtonDescription Standard_p3 Text"
                             style="text-indent: 0em; display: block; font-weight: normal; padding-left: 0.25em;">
                            Files and other contents, which are linked to by this
                            HTML file, have their own rulings.
                        </div>
                        <div class="MainButtonDescription Standard_p3 Text"
                             style="text-indent: 0em; display: block; font-weight: normal; padding-left: 0.25em;">
                            Publication Date:
                            <xsl:value-of
                                    select="concat(./s:publication_date/@year, '.', ./s:publication_date/@month, '.', ./s:publication_date/@day_of_month)"/>
                        </div>
                        <xsl:if test="./s:arbitrary_publication_date">
                            <h3 style="padding-left: .25em;">Notes</h3>
                            <div class="MainButtonDescription Standard_p3 Text">This document has an
                                arbitrary creation date.
                            </div>
                        </xsl:if>
                        <a class="linkButton"
                           href="https://html.spec.whatwg.org/multipage/microdata.html">
                            <div class="MainButtonDescription Standard_p3"
                                 style="text-indent: 0em; display: block; font-weight: normal;">
                                <div style="padding-left: 0.25em;">This site uses Microdata a bit.
                                </div>
                            </div>
                        </a>
                        <div style="flex: auto;"></div>
                        <h3 style="padding-left: .25em;">Footer Functions</h3>
                        <a class="linkButton" style="margin-bottom: .5em;" href="#topElement">
                            <div class="MainButton MainButton_S">back to top</div>
                        </a>
                    </div>
                    <div class="content-column" style="order: 3;">
                        <div id="content" class="column_0 contentCell Standard_content Right_shadow"
                             style="order: 3;">
                            <div class="optional_structural_guide structural_guide"
                                 style="width:.25em;"></div>
                            <article>
                                <div class="Right_shadow splitcells-net-window-menu"
                                     style="display: flex; flex-direction: column; width: 100%;">
                                    <div class="Standard_p3 bottomLightShadow "
                                         style="display: flex; flex-direction: row; z-index: 1; flex-wrap: wrap;">
                                        <a class="HeaderButton_structure HeaderButton main-logo-button minimal-only">
                                            <xsl:attribute name="href">
                                                <xsl:value-of
                                                        select="s:root-relative-url('/2017/12/31/updated-main-page.html')"/>
                                            </xsl:attribute>
                                        </a>
                                        <div class="HeaderButton_structure HeaderButton_p2">
                                            <xsl:element name="a">
                                                <xsl:attribute name="class">
                                                    <xsl:value-of select="'NonStandard'"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of
                                                            select="s:root-relative-url('/2018/01/11/dedicated-menu-page.html')"/>
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
                                                            select="s:root-relative-url('/2016/10/10/premature-content.html')"/>
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
                                                            select="s:root-relative-url('/2015/03/01/impressum.html')"/>
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
                                                            select="s:root-relative-url('/2015/03/01/privacy-policy.html')"/>
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
                                <article class="Standard_p2">
                                    <xsl:if test="$column_1 != ''">
                                        <div class="Right_shadow Standard_p2 splitcells-net-window-menu"
                                             style="display: flex; flex-direction: column;  width: 100%;">
                                            <div class="Standard_p3 bottomLightShadow "
                                                 style="display: flex; flex-direction: row; z-index: 1; flex-wrap: wrap; height: 2em;">
                                                <div style="flex: auto;"></div>
                                                <div class="HeaderButton_structure HeaderButton_p2 page-column-1-full-screen optional minimal-not"
                                                     onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-1-full-screen');
										                unshowByCssClass('column_0');
										                showByCssClass('page-column-1-windowed');">
                                                    wide screen
                                                </div>
                                                <div class="HeaderButton_structure HeaderButton_p2 page-column-1-windowed optional"
                                                     style="visibility: hidden; display: none;"
                                                     onclick="javascript: fullScreenDisable();
													hide('page-column-1-windowed');
														unshowByCssClass('page-column-1-windowed');
														showByCssClass('page-column-1-full-screen');
														showByCssClass('column_0');">
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