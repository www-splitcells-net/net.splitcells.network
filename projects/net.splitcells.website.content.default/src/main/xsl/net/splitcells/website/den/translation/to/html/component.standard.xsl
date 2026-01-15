<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:d="http://splitcells.net/den.xsd" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--
        SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
        SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <xsl:template name="s:site.file.link">
        <xsl:param name="file"/>
        <xsl:variable name="file.input.name" select="tokenize(document-uri(.), '/')[last()]"/>
        <xsl:variable name="file.input.name.length" select="string-length($file.input.name)"/>
        <xsl:variable name="file.output.name"
                      select="substring($file.input.name,12,($file.input.name.length - 15))"/>
        <xsl:copy-of
                select="concat($site.url, '/', $file/s:article/s:meta/s:publication_date/@year, '/', $file/s:article/s:meta/s:publication_date/@month, '/', $file/s:article/s:meta/s:publication_date/@day_of_month, '/', $file.output.name, '.html')"/>
    </xsl:template>
    <xsl:template match="s:tableOfContents">
        <xsl:message>TOREMOVE This will be implemented via local path context.</xsl:message>
        <div style="padding-top: 1.4em;">
            <xsl:for-each select="s:chapter">
                <xsl:apply-templates select="." mode="tableOfContents">
                    <xsl:with-param name="relativeIdPrefix" select="concat(position(), '.')"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </div>
    </xsl:template>
    <xsl:template match="s:related">
        <xsl:variable name="related-content">
            <s:link>
                <s:url>
                    <xsl:value-of select="concat('#net-splitcells-website-description-', ./s:id/@xl:href)"/>
                </s:url>
                <s:text>[related]</s:text>
            </s:link>
        </xsl:variable>
        <xsl:apply-templates select="$related-content"/>
    </xsl:template>
    <xsl:template match="s:chapter" mode="tableOfContents">
        <xsl:param name="relativeIdPrefix" select="''"/>
        <xsl:variable name="tmpUrl">
            <xsl:choose>
                <xsl:when test="./s:relatedTo">
                    <!-- TODO DEPRECATE -->
                    <xsl:message select="'TODO relatedTo is deprecated inside tableOfContents.'"/>
                    <xsl:copy-of select="$site_instance_purl"/>
                    <xsl:apply-templates select="s:relatedTo/node()"/>
                    .html
                </xsl:when>
                <xsl:when test="./s:absolute-path">
                    <!-- TODO DEPRECATE -->
                    <xsl:apply-templates select="s:absolute-path/node()"/>
                    .html
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$site_instance_purl"/>
                    premature-content.html
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="url" select="replace(normalize-space($tmpUrl), ' ', '')"/>
        <div style="display: flex; flex-direction: row; overflow: hidden; flex-wrap: wrap; width: 100%;">
            <div class="optional" style="width: 1em;">&#160;</div>
            <div style="flex: auto">
                <xsl:element name="div">
                    <xsl:attribute name="class">Standard_highlighted toc_chapter_1
                        <xsl:if test="not(s:relatedTo) and not(s:absolute-path)">premature</xsl:if>
                    </xsl:attribute>
                    <xsl:attribute name="style">display: block; transform: skewx(12deg);
                    </xsl:attribute>
                    <div style="transform: skewx(-12deg); display: flex; flex-direction: row;">
                        <xsl:element name="a">
                            <xsl:attribute name="class">NonStandard
                                <xsl:if test="not(s:relatedTo) and not(s:absolute-path)">premature</xsl:if>
                            </xsl:attribute>
                            <xsl:attribute name="style">font-weight: bold; flex: auto;
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of select="$url"/>
                            </xsl:attribute>
                            <xsl:apply-templates select="s:title/node()"/>
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">NonStandard
                                <xsl:if test="not(s:relatedTo) and not(s:absolute-path)">premature</xsl:if>
                            </xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of select="$url"/>
                            </xsl:attribute>
                            &#160;
                        </xsl:element>
                        <xsl:element name="a">
                            <xsl:attribute name="class">NonStandard
                                <xsl:if test="not(s:relatedTo) and not(s:absolute-path)">premature</xsl:if>
                            </xsl:attribute>
                            <xsl:attribute name="style">font-weight: bold;</xsl:attribute>
                            <xsl:attribute name="href">
                                <xsl:value-of select="$url"/>
                            </xsl:attribute>
                            <xsl:value-of select="$relativeIdPrefix"/>
                        </xsl:element>
                    </div>
                </xsl:element>
                <xsl:for-each select="s:chapter">
                    <xsl:apply-templates select="." mode="tableOfContents">
                        <xsl:with-param name="relativeIdPrefix" select="concat($relativeIdPrefix, position(), '.')"/>
                    </xsl:apply-templates>
                </xsl:for-each>
            </div>
        </div>
    </xsl:template>
    <!-- -->
    <xsl:template match="s:private_info">
        <!-- Private information is not published. -->
    </xsl:template>
    <xsl:template match="s:republication">
        <!-- Republications redirections are used in order to keep up link compatiblity. -->
        <xsl:variable name="tmp">
            <s:paragraph>
                <s:link>
                    <s:text>This article is republished here.</s:text>
                    <s:post>
                        <xsl:apply-templates select="./node()"/>
                    </s:post>
                </s:link>
                <xsl:text>Some posts turn out to be unsatisfying and in need of a revision.
                    In these cases I sometimes decide to depublish such articles
                    and later publish the updated versions.
                </xsl:text>
            </s:paragraph>
        </xsl:variable>
        <xsl:apply-templates select="$tmp"/>
    </xsl:template>
    <xsl:template match="s:image_gallery">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="s:tree">
        <!-- FIXME This does not work if the result is inserted multiple times somewhere (see page columns). -->
        <!-- HACK "width: 96%;" is used in order to ensure that child elements can use "width: 100%;" in order to fill parents. -->
        <xsl:variable name="tmp_id" select="generate-id()"/>
        <div class="tree">
            <xsl:element name="input">
                <xsl:attribute name="type">checkbox</xsl:attribute>
                <xsl:attribute name="style">display: none; position: absolute;</xsl:attribute>
                <xsl:attribute name="id" select="$tmp_id"/>
                <xsl:attribute name="checked"></xsl:attribute>
            </xsl:element>
            <xsl:element name="label">
                <xsl:attribute name="class">tree_name</xsl:attribute>
                <xsl:attribute name="style">display: flex; flex-direction: row;</xsl:attribute>
                <xsl:attribute name="for" select="$tmp_id"/>
                <div style="font-weight: bold; margin-right: 1em;">-</div>
                <div style="width: 96%;">
                    <xsl:apply-templates select="s:name/node()"/>
                </div>
            </xsl:element>
            <xsl:apply-templates select="s:tree"/>
        </div>
    </xsl:template>
    <xsl:template match="s:image">
        <xsl:element name="img">
            <xsl:choose>
                <xsl:when test="./@xl:href">
                    <xsl:attribute name="src">
                        <xsl:copy-of select="./@xl:href"/>
                    </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="src">
                        <xsl:copy-of select="s:image_location(@license, node())"/>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:attribute name="alt"></xsl:attribute>
            <xsl:attribute name="style">
                <xsl:copy-of select="'width: 100%; vertical-align: middle; margin-bottom: 2em;'"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template match="s:content">
        <xsl:apply-templates select="node()"/>
    </xsl:template>
    <xsl:template match="s:paragraph">
        <!-- TODO why is margin used? -->
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    <xsl:template match="s:line">
        <div>
            <xsl:apply-templates/>
            <br/>
        </div>
    </xsl:template>
    <xsl:template match="s:table">
        <div class="table">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="s:row">
        <div class="row">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="s:cell">
        <xsl:variable name="tmp">
            <xsl:apply-templates/>
        </xsl:variable>
        <div class="cell">
            <xsl:choose>
                <xsl:when test="$tmp = ''">
                    <xsl:text></xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$tmp"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
    <xsl:template match="s:url">
        <xsl:variable name="tmp">
            <xsl:apply-templates/>
        </xsl:variable>
        <xsl:copy-of select="replace(normalize-space($tmp), ' ', '')"/>
    </xsl:template>
    <xsl:template match="s:text">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="s:form">
        <form method="post" enctype="multipart/form-data">
            <xsl:attribute name="id" select="./@id"/>
            <!-- TODO Action paths are a hack, because the server does not support the root path concept,
                which is actively used for documents hosted be the server. -->
            <xsl:attribute name="action" select="./@action"/>
            <xsl:apply-templates select="node()[not(self::s:library)]"/>
        </form>
        <xsl:for-each select="./s:library">
            <script type="text/javascript" charset="utf-8">
                <xsl:attribute name="src" select="concat(s:default-root-relative-url(./@path), '.js')"/>
            </script>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="s:library" mode="net-splitcells-website-form-editor-tab-content"></xsl:template>
    <xsl:template match="s:no-code-editor" mode="net-splitcells-website-form-editor-tab-content">
        <div>
            <xsl:attribute name="class" select="concat('net-splitcells-website-form-editor-tab ', @form-id)"/>
            <xsl:attribute name="id" select="concat(./@id, '-tab-content')"/>
            <xsl:if test="not(./@main-tab = 'true')">
                <xsl:attribute name="style" select="'display: none; visibility: hidden;'"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="contains(@content-types, ' /net/splitcells/gel/ui/no-code-editor-language ')">
                    <div class="net-splitcells-component-priority-0 net-splitcells-webserver-form-no-code-editor">
                        <xsl:attribute name="net-splitcells-syncs-to" select="./@id"/>
                        <xsl:apply-templates select="./node()"/>
                    </div>
                    <textarea
                            class="net-splitcells-component-priority-0 net-splitcells-webserver-form-no-code-editor-backend"
                            style="display: none; visibility: hidden;">
                        <xsl:attribute name="id" select="./@id"/>
                        <xsl:attribute name="name" select="./@id"/>
                        <xsl:attribute name="content-types" select="@content-types"/>
                        <xsl:apply-templates select="./node()"/>
                    </textarea>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="yes">
                        Unknown content-types of no-code-editor:
                        <xsl:value-of select="@content-types"/>
                    </xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </div>
        <xsl:if test="./@initial-content-at">
            <!-- Listening on DOMContentLoaded is required, so that is ensured, that the textarea is available, before writing its default content. -->
            <script type="text/javascript"><![CDATA[
document.addEventListener('DOMContentLoaded', function(){
    var httpRequest = new XMLHttpRequest();
    httpRequest.open("GET", "]]><xsl:value-of select="./@initial-content-at"/><![CDATA[", true);
    function listener() {
        document.getElementById(']]><xsl:value-of select="./@id"/><![CDATA[').innerHTML = this.responseText;
        net_splitcells_gel_ui_editor_no_code_enhance();
    }
    httpRequest.addEventListener("load", listener);
    httpRequest.send(null);
});]]>
            </script>
        </xsl:if>
    </xsl:template>
    <xsl:template match="s:form-editor">
        <form class="net-splitcells-form-editor" method="post" enctype="multipart/form-data">
            <xsl:attribute name="id" select="./@id"/>
            <!-- TODO Action paths are a hack, because the server does not support the root path concept,
                which is actively used for documents hosted be the server. -->
            <xsl:attribute name="action" select="./@action"/>
            <xsl:variable name="form-id" select="./@id"/>
            <xsl:variable name="quote">'</xsl:variable>
            <div class="net-splitcells-website-form-editor-tab-holder">
                <xsl:for-each select="./s:no-code-editor">
                    <xsl:variable name="bar-button">
                        <s:no-code-editor>
                            <xsl:attribute name="form-id" select="$form-id"/>
                            <xsl:attribute name="name" select="./@name"/>
                            <xsl:attribute name="id" select="./@id"/>
                            <xsl:if test="./@main-tab">
                                <xsl:attribute name="main-tab" select="./@main-tab"/>
                            </xsl:if>
                            <xsl:if test="./@initial-content-at">
                                <xsl:attribute name="initial-content-at" select="./@initial-content-at"/>
                            </xsl:if>
                            <xsl:if test="./@content-types">
                                <xsl:attribute name="content-types" select="./@content-types"/>
                            </xsl:if>
                            <xsl:choose>
                                <xsl:when test="@content-types">
                                    <xsl:attribute name="content-types" select="@content-types"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="content-types" select="' default '"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:copy-of select="./node()"/>
                        </s:no-code-editor>
                    </xsl:variable>
                    <xsl:apply-templates select="$bar-button" mode="net-splitcells-website-form-editor-tab-content"/>
                </xsl:for-each>
                <xsl:for-each select="./s:text-area">
                    <div>
                        <xsl:attribute name="class"
                                       select="concat('net-splitcells-website-form-editor-tab ', $form-id, '-', @name, '-tab-content ', $form-id, '-tab-content')"/>
                        <xsl:if test="not(./@main-tab = 'true')">
                            <xsl:attribute name="style" select="'display: none; visibility: hidden;'"/>
                        </xsl:if>
                        <div class="net-splitcells-component-priority-0 net-splitcells-webserver-form-text-editor">
                            <xsl:attribute name="net-splitcells-syncs-to" select="concat($form-id, '-', @name)"/>
                            <xsl:apply-templates select="./text()"/>
                        </div>
                        <textarea
                                class="net-splitcells-component-priority-0 net-splitcells-webserver-form-text-editor-backend">
                            <xsl:attribute name="id" select="concat($form-id, '-', @name)"/>
                            <xsl:attribute name="name" select="@name"/>
                            <xsl:attribute name="content-types" select="@content-types"/>
                            <xsl:apply-templates select="./text()"/>
                        </textarea>
                    </div>
                    <xsl:if test="./@initial-content-at">
                        <script type="text/javascript">
                            <![CDATA[net_splitcells_webserver_form_input_set_initial_content(']]><xsl:value-of
                                select="concat($form-id, '-', @name)"/><![CDATA[', ']]><xsl:value-of
                                select="./@initial-content-at"/><![CDATA[');]]>
                        </script>
                    </xsl:if>
                </xsl:for-each>
            </div>
        </form>
        <xsl:for-each select="./s:library">
            <script type="text/javascript" charset="utf-8">
                <xsl:attribute name="src" select="concat(s:default-root-relative-url(./@path), '.js')"/>
            </script>
        </xsl:for-each>
        <xsl:if test="./@init">
            <script type="text/javascript">
                <xsl:value-of select="./@init"/>
            </script>
        </xsl:if>
    </xsl:template>
    <xsl:template match="s:form-submit-button">
        <div class="net-splitcells-button net-splitcells-action-button">
            <xsl:attribute name="id" select="./@id"/>
            <xsl:choose>
                <xsl:when test="ends-with(@command, ')')">
                    <xsl:attribute name="onclick" select="concat('javascript: ', ./@command)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="onclick" select="concat('javascript: ', ./@command, '()')"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="./node()"/>
        </div>
    </xsl:template>
    <xsl:template match="s:rendering-target">
        <div>
            <xsl:attribute name="id" select="./@id"/>
            No data present yet.
        </div>
    </xsl:template>
    <xsl:template match="s:only-for-general-public">
        <!-- This flag is not rendered by default. -->
    </xsl:template>
    <xsl:template match="s:accept-notification">
        <!-- This button is not rendered by default. -->
    </xsl:template>
    <xsl:template match="s:inline-resource">
        <xsl:value-of select="unparsed-text(concat('/net.splitcells.website.server/source-code/', @path))"/>
    </xsl:template>
    <xsl:template match="s:notification">
        <xsl:if test="document('/net/splitcells/website/server/config/is-server-for-general-public.xml')/d:val/node() = 'true'">
            <xsl:if test="./s:only-for-general-public/node() = 'true'">
                <xsl:variable name="quote">'</xsl:variable>
                <div class="net-splitcells-website-pop-up">
                    <xsl:attribute name="id" select="generate-id(.)"/>
                    <xsl:choose>
                        <xsl:when test="./s:default-content">
                            <xsl:choose>
                                <xsl:when
                                        test="doc-available(concat('/net.splitcells.website.server/source-code/', ./s:default-content/node(), '-customization.xml'))">
                                    <xsl:apply-templates
                                            select="document(concat('/net.splitcells.website.server/source-code/', ./s:default-content/node(), '-customization.xml'))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates
                                            select="document(concat('/net.splitcells.website.server/source-code/', ./s:default-content/node(), '.xml'))"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="./*"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="./s:accept-notification">
                        <div class="net-splitcells-website-pop-up-confirmation-button net-splitcells-button net-splitcells-action-button net-splitcells-component-priority-0">
                            <xsl:attribute name="onclick"
                                           select="concat('unshowElement(document.getElementById(', $quote, generate-id(.), $quote, '))')"/>
                            <xsl:value-of select="./s:accept-notification"/>
                        </div>
                    </xsl:if>
                </div>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    <xsl:template match="s:chapter">
        <xsl:call-template name="chapter-with-priority">
            <xsl:with-param name="node" select="."/>
            <xsl:with-param name="priority" select="'none'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="s:chapter" mode="net-splitcells-website-menu">
        <xsl:call-template name="chapter-with-priority">
            <xsl:with-param name="node" select="."/>
            <xsl:with-param name="priority" select="'4'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="chapter-with-priority">
        <xsl:param name="node"/>
        <xsl:param name="priority"/>
        <xsl:choose>
            <xsl:when test="$generation.style='minimal'">
                <section>
                    <h2>
                        <xsl:attribute name="href" select="concat('#', generate-id(.))"/>
                        <xsl:attribute name="id" select="generate-id(.)"/>
                        <xsl:apply-templates select="./s:title/node()"/>
                    </h2>
                    <xsl:apply-templates select="node()[not(self::s:title)]"/>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="id">
                    <xsl:choose>
                        <xsl:when test="@id">
                            <xsl:value-of select="@id"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="generate-id(.)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <section>
                    <xsl:attribute name="id" select="$id"/>
                    <xsl:attribute name="class" select="'chapter'"/>
                    <div class="heading">
                        <xsl:attribute name="id" select="./s:title/@id"/>
                        <a style="margin-right: .5em;" class="net-splitcells-heading-title">
                            <xsl:attribute name="href" select="concat('#', $id)"/>
                            <xsl:apply-templates select="./s:title/node()"/>
                        </a>

                        <div class="net-splitcells-action-button">
                            <xsl:choose>
                                <xsl:when test="$priority!='none'">
                                    <xsl:attribute name="class"
                                                   select="concat('net-splitcells-action-button net-splitcells-component-priority-', $priority)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="class" select="'net-splitcells-action-button'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <a href="#topElement">↑</a>
                        </div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="$priority!='none'">
                                    <xsl:attribute name="class"
                                                   select="concat('net-splitcells-action-button net-splitcells-component-priority-', $priority)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="class" select="'net-splitcells-action-button'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:attribute name="id" select="concat($id, '-button-hide')"/>
                            <xsl:attribute name="style">
                                <xsl:choose>
                                    <xsl:when test="./@minimized='true'">display: none; visibility: hidden;
                                    </xsl:when>
                                    <xsl:otherwise></xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="onclick">
                                <xsl:text>unshowById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-body');</xsl:text>
                                <xsl:text>showById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-button-show');</xsl:text>
                                <xsl:text>unshowById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-button-hide');</xsl:text>
                            </xsl:attribute>
                            -
                        </div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="$priority!='none'">
                                    <xsl:attribute name="class"
                                                   select="concat('net-splitcells-action-button net-splitcells-component-priority-', $priority)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="class" select="'net-splitcells-action-button'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:attribute name="id" select="concat($id, '-button-show')"/>
                            <xsl:attribute name="style">
                                <xsl:text>float: right;</xsl:text>
                                <xsl:choose>
                                    <xsl:when test="./@minimized='true'"></xsl:when>
                                    <xsl:otherwise>display: none; visibility: hidden;</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="onclick">
                                <xsl:text>showById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-body');</xsl:text>
                                <xsl:text>unshowById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-button-show');</xsl:text>
                                <xsl:text>showById('</xsl:text>
                                <xsl:value-of select="$id"/>
                                <xsl:text>-button-hide');</xsl:text>
                            </xsl:attribute>
                            +
                        </div>

                    </div>
                    <div>
                        <xsl:attribute name="id" select="concat($id, '-body')"/>
                        <xsl:attribute name="style">
                            <xsl:choose>
                                <xsl:when test="./@minimized='true'">display: none; visibility: hidden;
                                </xsl:when>
                                <xsl:otherwise></xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="node()[not(self::s:title)]"/>
                    </div>
                </section>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="s:premature_announcement">
        <!-- TODO make onclick target link generic -->
        <xsl:element name="div">
            <xsl:attribute name="class" select="'premature premature_paragraph'"/>
            <xsl:attribute name="style" select="'text-align: center;'"/>
            <xsl:attribute name="onclick"
                           select="concat('document.location.href=', $apostroph, '/2016/10/10/premature-content.html', $apostroph)"/>
            <br/>
            <xsl:apply-templates select="node()"/>
            <br/>
            <br/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="s:premature_text">
        <!-- RENAME ? -->
        <!-- TODO make onclick target link generic -->
        <xsl:if test="node() = ''">
            <xsl:message terminate="yes"/>
        </xsl:if>
        <xsl:variable name="tmp">
            <xsl:element name="div">
                <xsl:attribute name="class" select="'premature premature_paragraph'"/>
                <xsl:attribute name="style" select="'padding: .5em;'"/>
                <xsl:attribute name="onclick"
                               select="concat('document.location.href=', $apostroph, '/2016/10/10/premature-content.html', $apostroph)"/>
                <div>
                    <xsl:apply-templates select="node()"/>
                </div>
            </xsl:element>
        </xsl:variable>
        <xsl:apply-templates select="$tmp"/>
        <!-- REMOVE <div class="premature premature_paragraph" onclick="document.location.href='/{{{{page.style}}}}/2016/10/10/premature-content.html'"
            style="text-align: center;"> <br /> <xsl:text>Under Construction</xsl:text> <br /> <br /> </div> -->
    </xsl:template>
    <xsl:template match="s:premature_block">
        <xsl:element name="div">
            <xsl:attribute name="class" select="'premature premature_paragraph'"/>
            <xsl:attribute name="onclick"
                           select="concat('document.location.href=', $apostroph, '/2016/10/10/premature-content.html', $apostroph)"/>
            <br/>
            <br/>
            <br/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="s:premature">
        <xsl:element name="div">
            <xsl:attribute name="class" select="'premature premature_paragraph'"/>
            <xsl:attribute name="onclick"
                           select="concat('document.location.href=', $apostroph, '/2016/10/10/premature-content.html', $apostroph)"/>
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="s:meta">
        <!-- ./s:description is not rendered as part of the article's main content, because the description of the article is not
            the start of the article necessarily. Also some articles have a description but do not start with this description. -->
    </xsl:template>
    <xsl:template match="s:deck">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="s:card">
        <xsl:param name="detailed_tags" select="'false'"/>
        <xsl:variable name="title">
            <xsl:if test="./s:abbreviation">
                <xsl:value-of select="./s:abbreviation/node()"/>
                <xsl:value-of select="' - '"/>
            </xsl:if>
            <xsl:call-template name="strip_tag">
                <xsl:with-param name="input_node" select="./s:name/node()"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <!-- REMOVE code duplication -->
            <xsl:when test="./s:status = 'premature'">
                <xsl:element name="div">
                    <xsl:attribute name="class"
                                   select="'standardborder premature highlightedShortSummary highlightedShortSummary'"/>
                    <xsl:attribute name="style" select="'padding: 0em; margin-top: 1.4em; margin-bottom: 1.4em;'"/>
                    <xsl:attribute name="onclick"
                                   select="concat('document.location.href=', $apostroph, '/2016/10/10/premature-content.html', $apostroph)"/>
                    <div style="display: flex; flex-direction: row;">
                        <div style="width: 100%;">
                            <div style="padding: .5em .5em .5em .5em;">
                                <xsl:element name="div">
                                    <xsl:attribute name="class">NonStandard IndexPostHeader
                                        text_standrad_very_low_priority
                                    </xsl:attribute>
                                    <xsl:attribute name="style">font-weight: bold; margin-left: 1em;
                                    </xsl:attribute>
                                    <xsl:value-of select="$title"/>
                                </xsl:element>
                            </div>
                            <div class="Text text_standrad_very_low_priority"
                                 style="font-weight: 400; text-align: justify; padding-right: .5em; padding-left: .5em; padding-top: .5em;">
                                <xsl:call-template name="strip_tag">
                                    <xsl:with-param name="input_node" select="./s:description/node()"/>
                                </xsl:call-template>
                            </div>
                        </div>
                    </div>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO what to do when no status -->
                <!-- REMOVE duplicate onclick but do not let whole area react on click? -->
                <xsl:variable name="link">
                    <xsl:choose>
                        <xsl:when test="./s:location">
                            <xsl:apply-templates
                                    select="concat($site_instance_purl, s:string-remove-whitespace(./s:location), '.html')"/>
                        </xsl:when>
                        <xsl:when test="./s:path">
                            <xsl:copy-of
                                    select="replace(concat($site-instance-host-root-path, s:string-remove-whitespace(./s:path), '.html'), '//', '/')"/>
                        </xsl:when>
                        <xsl:when test="./s:url">
                            <xsl:copy-of
                                    select="./s:url"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:message terminate="true"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="div">
                    <xsl:attribute name="class">standardborder Standard_highlighted highlightedShortSummary
                        highlightedShortSummary
                    </xsl:attribute>
                    <xsl:attribute name="style">
                        <xsl:text>padding: 0em; margin-top: 1.4em; margin-bottom: 1.4em; display: flex; flex-direction:
                            row; border-radius: var(--border-radius-for-gentle-emphasis);
                        </xsl:text>
                        <xsl:choose>
                            <xsl:when test="./s:logos">
                                <xsl:text>min-height: 12em;</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>min-height: 4em;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="onclick">
                        <xsl:text>document.location.href='</xsl:text>
                        <xsl:copy-of select="translate(normalize-space($link), ' ', '')"/>
                        <xsl:text>'</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="div">
                        <xsl:choose>
                            <xsl:when test="./s:logos">
                                <xsl:attribute name="style">width: 50%; padding-top: .5em;
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="style">width: 100%; padding-top: .5em;
                                </xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <div style="padding-left: .5em;">
                            <xsl:element name="a">
                                <xsl:attribute name="class">NonStandard IndexPostHeader</xsl:attribute>
                                <xsl:attribute name="style">font-size: 125%;
                                    font-weight: bold;
                                    margin-left: .5em;
                                </xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="$link"/>
                                </xsl:attribute>
                                <xsl:value-of select="$title"/>
                            </xsl:element>
                        </div>
                        <div class="IndexPostHeader">
                            <xsl:text></xsl:text><!-- REMOVE Used in order to ommit empty 'a' elements which break browsers. -->
                            <!-- FIXME Prevent creation of empty tags with other means. -->
                            <xsl:if test="./s:description">
                                <div class="highlighted_paragraph"
                                     style="font-weight: 400; text-align: justify; margin-top: .5em;">
                                    <xsl:text></xsl:text>
                                    <xsl:apply-templates select="./s:description/node()"/>
                                </div>
                            </xsl:if>
                        </div>
                        <xsl:choose>
                            <xsl:when test="$detailed_tags = 'true'">
                                <xsl:if test="./s:description">
                                    <div class="summary_footer"
                                         style="z-index: 2; width: 100%; margin: 0em; padding: 0em;">
                                        <div style="width: 100%; display: flex; flex-direction: row-reverse;">
                                            <div class="cell">
                                                <!-- TODO date to string function -->
                                                <xsl:value-of
                                                        select="concat(./s:publication_date/@year,'.', ./s:publication_date/@month, '.', ./s:publication_date/@day_of_month)"/>
                                            </div>
                                        </div>
                                    </div>
                                </xsl:if>
                            </xsl:when>
                            <xsl:otherwise/>
                        </xsl:choose>
                    </xsl:element>
                    <xsl:if test="./s:logos">
                        <xsl:element name="div">
                            <!-- Randomize logo selection if multiple logos are available. -->
                            <xsl:attribute name="style">
                                <xsl:text><![CDATA[width: 50%; overflow: hidden; background-size: 100% auto; background-repeat:
                                    no-repeat; border-radius: var(--border-radius-for-gentle-emphasis); background-image: url(']]></xsl:text>
                                <xsl:choose>
                                    <xsl:when test="./s:logos/*[1]/@license">
                                        <!-- TODO REMOVE This is legacy code. -->
                                        <xsl:copy-of
                                                select="s:image_thumbnail_medium_location(./s:logos/*[1]/@license , ./s:logos/*[1])"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:copy-of
                                                select="replace(concat($site-instance-host-root-path, ./s:logos/*[1]), '//', '/')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:text>');</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onclick">
                                <xsl:value-of select="translate(normalize-space($link), ' ', '')"/>
                            </xsl:attribute>
                            <xsl:text></xsl:text>
                        </xsl:element>
                    </xsl:if>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="s:div">
        <xsl:text></xsl:text>
        <xsl:apply-templates/>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="s:strong">
        <strong>
            <xsl:text></xsl:text>
            <xsl:apply-templates/>
            <xsl:text></xsl:text>
        </strong>
    </xsl:template>
    <xsl:template match="s:quote">
        <xsl:text></xsl:text>
        <q>
            <xsl:apply-templates/>
        </q>
        <xsl:text></xsl:text>
    </xsl:template>
    <xsl:template match="s:link">
        <!-- See <xsl:template name="link.target"> -->
        <!-- TODO Remove unecessary whitespaces before and after an link. -->
        <xsl:choose>
            <xsl:when test="@url">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="@url"/>
                    </xsl:attribute>
                    <xsl:value-of select="node()"/>
                </a>
            </xsl:when>
            <xsl:when test="./s:url">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="./s:url"/>
                    </xsl:attribute>
                    <xsl:value-of select="./s:name"/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:choose>
                            <xsl:when test="s:url">
                                <xsl:apply-templates select="s:url"/>
                            </xsl:when>
                            <xsl:when test="@post">
                                <xsl:variable name="postLink">
                                    <xsl:copy-of select="$site-instance-root-path-default"/>
                                    <xsl:text></xsl:text>
                                    <xsl:apply-templates
                                            select="@post"/>
                                    <xsl:text>.html</xsl:text>
                                </xsl:variable>
                                <xsl:value-of select="replace($postLink, '//', '/')"/>
                            </xsl:when>
                            <xsl:when test="s:file">
                                <xsl:variable name="fileLink">
                                    <xsl:copy-of select="$site-instance-root-path-default"/>
                                    <xsl:text></xsl:text>
                                    <xsl:apply-templates select="s:file/node()"/>
                                </xsl:variable>
                                <xsl:value-of select="replace($fileLink, '//', '/')"/>
                            </xsl:when>
                            <xsl:when test="s:post">
                                <xsl:variable name="postLink">
                                    <xsl:copy-of select="$site-instance-root-path-default"/>
                                    <xsl:text></xsl:text>
                                    <xsl:apply-templates
                                            select="s:post/node()"/>
                                    <xsl:text>.html</xsl:text>
                                </xsl:variable>
                                <xsl:value-of select="replace($postLink, '//', '/')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:message terminate="yes"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:text>&#160;</xsl:text>
                    <xsl:choose>
                        <xsl:when test="s:text">
                            <xsl:apply-templates select="s:text"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="node()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>&#160;</xsl:text>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="link.target">
        <!-- See <xsl:template match="s:link"> -->
        <xsl:param name="linkNode"/>
        <xsl:choose>
            <xsl:when test="$linkNode/s:url">
                <xsl:apply-templates select="$linkNode/s:url"/>
            </xsl:when>
            <xsl:when test="$linkNode/s:post">
                <xsl:copy-of select="$site_instance_purl"/>
                <xsl:text>/</xsl:text>
                <xsl:apply-templates select="$linkNode/s:post/node()"/>
                <xsl:text>.html</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="true" select="$linkNode"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="s:list">
        <ol>
            <xsl:choose>
                <xsl:when test="./@name">
                    <div>
                        <xsl:value-of select="./@name"/>
                    </div>
                </xsl:when>
                <xsl:when test="./s:name">
                    <div>
                        <xsl:value-of select="./s:name"/>
                    </div>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="./node()" mode="list"/>
        </ol>
    </xsl:template>
    <xsl:template match="s:item" mode="list">
        <li>
            <xsl:apply-templates select="./node()"/>
        </li>
    </xsl:template>
    <xsl:template match="s:link" mode="list">
        <li>
            <xsl:apply-templates select="."/>
        </li>
    </xsl:template>
    <xsl:template match="s:list" mode="list">
        <li>
            <xsl:choose>
                <xsl:when test="./@name">
                    <span>
                        <xsl:value-of select="./@name"/>
                    </span>
                    <ol>
                        <xsl:apply-templates select="./node()" mode="list"/>
                    </ol>
                </xsl:when>
                <xsl:when test="./s:name">
                    <span>
                        <xsl:value-of select="./s:name"/>
                    </span>
                    <ol>
                        <xsl:apply-templates select="./node()" mode="list"/>
                    </ol>
                </xsl:when>
                <xsl:otherwise>
                    <ol>
                        <xsl:apply-templates select="./node()" mode="list"/>
                    </ol>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>
    <xsl:template match="s:name" mode="list">
    </xsl:template>
    <xsl:template match="s:post">
        <xsl:copy-of select="$site_instance_purl"/>
        <xsl:text>/</xsl:text>
        <xsl:apply-templates select="./node()"/>
        <xsl:value-of select="'.html'"/>
    </xsl:template>
    <xsl:template match="s:marked">
        <xsl:text></xsl:text>
        <xsl:element name="strong">
            <xsl:apply-templates/>
        </xsl:element>
        <xsl:text></xsl:text>
    </xsl:template>

    <xsl:template name="strip_tag">
        <xsl:param name="input_node"/>
        <xsl:for-each select="$input_node">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="s:blockquote">
        <blockquote>
            <xsl:apply-templates/>
        </blockquote>
    </xsl:template>
    <xsl:template match="s:code_block">
        <!-- "overflow: auto;"is used because some syntax highlighter do not use scrollbars by default. -->
        <xsl:if test="@asset!=''">
            <xsl:message select="'asdasd'" terminate="true"/>
        </xsl:if>
        <xsl:variable name="tmp">
            <xsl:element name="s:tree">
                <xsl:element name="s:name">
                    <xsl:value-of select="''"/>
                </xsl:element>
                <xsl:element name="s:tree">
                    <xsl:element name="s:name">
                        <xsl:element name="r:text">
                            <xsl:element name="div">
                                <xsl:attribute name="style">overflow: auto;</xsl:attribute>
                                <xsl:element name="pre">
                                    <xsl:attribute name="style">overflow: auto;</xsl:attribute>
                                    <xsl:attribute name="class">brush:
                                        <xsl:copy-of select="./@language"/>
                                    </xsl:attribute>
                                    <xsl:value-of
                                            select="node()"/>
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:variable>
        <xsl:variable name="tmp_id" select="generate-id()"/>
        <div class="net-splitcells-quote-display">
            <xsl:element name="input">
                <xsl:attribute name="type">checkbox</xsl:attribute>
                <xsl:attribute name="style">display: none; position: absolute;</xsl:attribute>
                <xsl:attribute name="id" select="$tmp_id"/>
                <xsl:attribute name="checked"></xsl:attribute>
            </xsl:element>
            <div class="label">
                <xsl:element name="label">
                    <xsl:attribute name="for" select="$tmp_id"/>
                    <xsl:value-of select="./@asset"/>
                </xsl:element>
            </div>
            <div>
                <xsl:element name="pre">
                    <xsl:attribute name="style">overflow: auto;</xsl:attribute>
                    <xsl:attribute name="class">brush:
                        <xsl:copy-of select="./@language"/>
                    </xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="s:inline-resource">
                            <xsl:apply-templates select="s:inline-resource"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of
                                    select="node()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
            </div>
        </div>
    </xsl:template>
    <!-- TODO consolidate 2 source code definitions -->
    <xsl:template match="s:source_code">
        <xsl:variable name="tmp">
            <s:tree>
                <s:name>
                    <xsl:element name="pre">
                        <xsl:attribute name="class">
                            brush:
                            <xsl:copy-of select="./@language"/>
                        </xsl:attribute>
                        <xsl:copy-of select="normalize-space(./node())"/>
                    </xsl:element>
                </s:name>
            </s:tree>
        </xsl:variable>
        <xsl:apply-templates select="$tmp"/>
    </xsl:template>
    <xsl:template match="*/text()[not(normalize-space())]">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="s:option">
        <a class="net-splitcells-button">
            <xsl:attribute name="href">
                <xsl:choose>
                    <xsl:when test="s:url">
                        <xsl:apply-templates select="s:url"/>
                    </xsl:when>
                    <xsl:when test="s:page">
                        <xsl:variable name="postLink">
                            <xsl:copy-of select="$site-instance-root-path-default"/>
                            <xsl:text></xsl:text>
                            <xsl:apply-templates
                                    select="s:page/node()"/>
                            <xsl:text>.html</xsl:text>
                        </xsl:variable>
                        <xsl:value-of select="replace($postLink, '//', '/')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message terminate="yes"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="./s:name/node()"/>
        </a>
    </xsl:template>
    <xsl:template match="s:choice">
        <xsl:apply-templates select="./s:option"/>
    </xsl:template>
    <xsl:template match="s:path.context">
        <ol>
            <xsl:apply-templates select="./node()" mode="path.context"/>
        </ol>
    </xsl:template>
    <xsl:template match="d:val" mode="path.context">
        <li>
            <xsl:choose>
                <xsl:when test="./d:link/d:url">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="./d:link/d:url"/>
                        </xsl:attribute>
                        <xsl:value-of select="./d:name"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="./d:name"/>
                </xsl:otherwise>
            </xsl:choose>
        </li>
        <xsl:if test="./d:val">
            <ol>
                <xsl:apply-templates select="./d:val" mode="path.context"/>
            </ol>
        </xsl:if>
    </xsl:template>
    <xsl:template match="*" mode="path.context">
        <xsl:value-of select="name(.)"/>
    </xsl:template>
    <xsl:template match="s:*">
        <xsl:message terminate="true">
            <xsl:value-of select="'Unknown tag:'"/>
            <xsl:copy-of select="."/>
        </xsl:message>
    </xsl:template>
    <xsl:template match="r:*">
        <xsl:copy-of select="./node()[1]"/>
    </xsl:template>
    <xsl:template match="p:*"/>
    <xsl:template match="svg:*">
        <!-- xhtml -->
        <!-- Identity transformation as described in: https://en.wikipedia.org/wiki/Identity_transform -->
        <!-- TODO Create prefix translating identity template and use this in order to avoid code duplication. -->
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="x:*">
        <!-- xhtml -->
        <!-- Identity transformation as described in: https://en.wikipedia.org/wiki/Identity_transform -->
        <!-- TODO Create prefix translating identity template and use this in order to avoid code duplication. -->
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="x:*" mode="net-splitcells-website-menu">
        <!-- xhtml -->
        <!-- Identity transformation as described in: https://en.wikipedia.org/wiki/Identity_transform -->
        <!-- TODO Create prefix translating identity template and use this in order to avoid code duplication. -->
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="m:*">
        <!-- xhtml -->
        <!-- Identity transformation as described in: https://en.wikipedia.org/wiki/Identity_transform -->
        <!-- TODO Create prefix translating identity template and use this in order to avoid code duplication. -->
        <xsl:text>&#032;</xsl:text>
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
        <xsl:text>&#032;</xsl:text>
    </xsl:template>
    <xsl:template match="d:toDo">
        <xsl:call-template name="todo">
            <xsl:with-param name="todo" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="d:todo">
        <xsl:call-template name="todo">
            <xsl:with-param name="todo" select="."/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="todo">
        <xsl:param name="todo"/>
        <ol class="premature">
            <li>
                <xsl:value-of select="local-name($todo)"/>
            </li>
            <!-- TODO Strictly speaking this form of list nesting is not correct,
            as any child element of an ol, has to be an li.
            Unfortunately, the current CSS relies on that, so this has to be adjusted as well. -->
            <ol>
                <xsl:for-each select="$todo/node()">
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
                            <xsl:call-template name="todo">
                                <xsl:with-param name="todo" select="."/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </ol>
        </ol>
    </xsl:template>
    <xsl:template match="s:site_domain">
        <xsl:value-of select="$site_domain"/>
    </xsl:template>
    <xsl:template match="s:site_instance_purl">
        <xsl:value-of select="$site_instance_purl"/>
        <!-- If copy-of would be used a whitespace would be appended. -->
    </xsl:template>
    <xsl:template match="s:site_generic_asset_purl">
        <xsl:value-of select="$site_generic_asset_purl"/>
    </xsl:template>
    <xsl:template match="s:siteName">
        <xsl:value-of select="concat(' ', $siteName)"/>
    </xsl:template>
    <xsl:template match="s:site_generic_asset_burl">
        <xsl:value-of select="$site_generic_asset_burl"/>
    </xsl:template>
    <xsl:template match="s:site_instance_burl">
        <xsl:value-of select="$site_instance_burl"/>
    </xsl:template>
    <xsl:template match="s:csv-chart-lines">
        <!-- TODO This hack assumes 2 rows. In future additional columns should create new lines. -->
        <script src="/net/splitcells/website/js/chart.js"></script>
        <script src="/net/splitcells/website/js/chartjs-plugin-datasource.js"></script>
        <canvas id="myChart"></canvas>
        <script type="text/javascript"><![CDATA[
let chartColors = [];
for (var i = 0; i != 37; ++i) {
    chartColors.push('hsl(' + (i *10) + ', 100%, 50%)');
}
function createDatasets(numberOfColumns) {
    let datasets = [];
    for (var i = 0; i != numberOfColumns - 1; ++i) {
        datasets.push({
            type: 'line',
            backgroundColor: 'transparent',
            borderColor: chartColors[i],
            pointBackgroundColor: chartColors[i],
        });
    }
    return datasets;
}
let csvPath = ']]><xsl:value-of select="./s:path"/><![CDATA[';
var request = new XMLHttpRequest();
request.onload = function() {
    let numberOfColumns = (request.responseText.split('\n')[0].match(/,/g) || []).length + 1;
    let color = Chart.helpers.color;
    let config = {
        type: 'line',
        data: {
            datasets: createDatasets(numberOfColumns)
        },
        plugins: [ChartDataSource],
        labels: [],
        options: {
            scales: {
                xAxes: [{id: 'xAxes'}],
                yAxes: [{id: 'yAxes'}]
            },
            plugins: {
                datasource: {
                    type: 'csv',
                    url: csvPath,
                    delimiter: ',',
                    rowMapping: 'index',
                    datasetLabels: true,
                    indexLabels: true
                }
            }
        }
    };

    window.onload = function() {
        let ctx = document.getElementById('myChart').getContext('2d');
        window.myChart = new Chart(ctx, config);
    };
}
request.open("GET", csvPath);
request.send();]]>
        </script>
    </xsl:template>
    <xsl:template match="s:import">
        <xsl:choose>
            <xsl:when test="s:from-variable">
                <xsl:variable name="fromVariable">
                    <xsl:value-of select="s:from-variable"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$fromVariable='net-splitcells-website-server-config-menu-detailed'">
                        <xsl:apply-templates select="$net-splitcells-website-server-config-menu-detailed"/>

                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message terminate="true" select="'Unknown variable.'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="true" select="'Import parameters are unknown.'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
