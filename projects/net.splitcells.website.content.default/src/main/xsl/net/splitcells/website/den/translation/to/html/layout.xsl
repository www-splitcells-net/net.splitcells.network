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
        SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
        SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
    -->
    <!-- Defines the standard html layout.
        Do not normalize spaces by default via a generic template,
        as not normalized space can be important for things like the default content of a textarea HTML element.
        Therefore, "xsl:output" with indent="no" is used.
    -->
    <!-- TODO s:root-relative-url(
    TODO Use only css classes with net-splitcells prefix for best portability.
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
                <s:path>
                    <xsl:value-of select="s:path.without.element.last(./s:meta/s:path/node())"/>
                </s:path>
                <s:name>
                    <xsl:value-of select="(tokenize(document-uri(/),'/'))[last()]"/>
                </s:name>
                <s:title/>
                <s:license>standard</s:license>
                <s:publication_date/>
                <s:content>
                    <xsl:apply-templates select="./node()" mode="natural-text"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/s:csv-chart">
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

                    <x:script src="/net/splitcells/website/js/chart.js"></x:script>
                    <x:script src="/net/splitcells/website/js/chartjs-plugin-datasource.js"></x:script>
                    <x:canvas id="myChart"></x:canvas>
                    <x:script type="text/javascript"><![CDATA[
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
                    </x:script>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
    <xsl:template match="/s:article">
        <xsl:variable name="layout.config">
            <s:layout.config>
                <s:path>
                    <xsl:value-of select="s:path.without.element.last(./s:meta/s:path/node())"/>
                </s:path>
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
                <xsl:if test="./s:meta/s:full-screen-by-default">
                    <s:full-screen-by-default>
                        <xsl:copy-of select="./s:meta/s:full-screen-by-default/node()"/>
                    </s:full-screen-by-default>
                </xsl:if>
                <xsl:if test="./s:meta/s:republication">
                    <s:republication>
                        <xsl:copy-of select="./s:meta/s:republication/node()"/>
                    </s:republication>
                </xsl:if>
                <xsl:if test="./s:meta/s:path.context">
                    <s:path.context>
                        <xsl:copy-of select="./s:meta/s:path.context/node()"/>
                    </s:path.context>
                </xsl:if>
                <xsl:copy-of select="./s:meta"/>
                <s:content>
                    <xsl:copy-of select="./node()"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config">
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="s:layout.config">
        <xsl:choose>
            <xsl:when test="$generation.style='minimal'">
                <html>
                    <head>
                        <meta http-equiv="Content-Type" content="application/xhtml+xml;charset=UTF-8"/>
                        <!-- Disable caching, so that CSS styling is reloading in webbrowsers on CSS updates automatically. -->
                        <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
                        <meta http-equiv="Pragma" content="no-cache"/>
                        <meta http-equiv="Expires" content="0"/>
                        <script type="text/javascript" charset="utf-8">
                            <!-- This should make it impossible, to read or write cookies in the browser. -->
                            document.__defineGetter__("cookie", function() { return '';} );
                            document.__defineSetter__("cookie", function() { return '';} );
                        </script>
                        <script type="text/javascript" charset="utf-8" src=""/>
                        <link rel="image_src" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/images/thumbnail/small/starting-to-learn-how-to-draw-a-face.jpg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="icon" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="alternate icon">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="mask-icon" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <meta name="viewport" content="width=device-width, initial-scale=1"/>
                        <link rel="apple-touch-icon">
                            <!-- Some Mobile browsers only support pngs as favicons. -->
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.png')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="stylesheet" type="text/css"
                              href="https://cdnjs.cloudflare.com/ajax/libs/tufte-css/1.8.0/tufte.min.css"/>
                        <title>
                            <xsl:value-of select="concat(./s:title, ' / ', $siteName)"/>
                        </title>
                    </head>
                    <body>
                        <header>
                            <nav>
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/dedicated-menu-page.html')"/>
                                    </xsl:attribute>
                                    Menu
                                </a>
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/premature-content.html')"/>
                                    </xsl:attribute>
                                    Tabs
                                </a>
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/legal/impressum.html')"/>
                                    </xsl:attribute>
                                    Impressum
                                </a>
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:root-relative-url('/legal/privacy-policy.html')"/>
                                    </xsl:attribute>
                                    Privacy Policy
                                </a>
                            </nav>
                        </header>
                        <main>
                            <article>
                                <h1>
                                    <xsl:value-of select="concat(./s:title, ' / ', $siteName)"/>
                                </h1>
                                <xsl:for-each select="./s:content/node()">
                                    <xsl:apply-templates select="."/>
                                </xsl:for-each>
                            </article>
                        </main>
                        <footer></footer>
                    </body>
                </html>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO HTML preemble is missing. -->
                <xsl:variable name="last-element-length"
                              select="string-length(tokenize(./s:path/text(), '/')[last()])"/>
                <xsl:variable name="folder"
                              select="substring(./s:path/text(), 1, (string-length(./s:path/text()) - $last-element-length))"/>
                <xsl:variable name="column_1">
                    <div class="net-splitcells-website-log-error net-splitcells-website-hidden-by-default">
                        <!--s:option> TODO This does not work.
                            <s:name>Page's Errors</s:name>
                            <s:url>
                                <xsl:value-of
                                        select="concat(./s:name, '.errors.txt')"/>
                            </s:url>
                        </s:option-->
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
                    <xsl:if test="./s:content/s:meta/s:notes/node() != ''">
                        <s:chapter>
                            <s:title>Notes</s:title>
                            <s:paragraph>
                                <xsl:apply-templates select="./s:content/s:meta/s:notes/node()"/>
                            </s:paragraph>
                        </s:chapter>
                    </xsl:if>
                    <xsl:if test="./s:content/s:meta/s:relevant-parent-pages">
                        <s:chapter>
                            <s:title>Parent Pages</s:title>
                            <ol>
                                <xsl:for-each select="./s:content/s:meta/s:relevant-parent-pages/*">
                                    <li>
                                        <a>
                                            <xsl:attribute name="href">
                                                <xsl:value-of
                                                        select="s:default-root-relative-url(./@path)"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="./@title"/>
                                        </a>
                                    </li>
                                </xsl:for-each>
                            </ol>
                        </s:chapter>
                    </xsl:if>
                    <xsl:if test="$column_1_tmp != ''">
                        <s:chapter>
                            <s:title>Outline</s:title>
                            <xsl:copy-of select="$column_1_tmp"/>
                        </s:chapter>
                    </xsl:if>
                    <xsl:if test="./s:content/s:meta/s:related_to">
                        <xsl:message
                                select="concat('Deprecated tag: ./s:content/s:meta/s:related_to:', document-uri(/))"/>
                    </xsl:if>
                    <xsl:variable name="relevantLocalPathContext">
                        <xsl:call-template name="file-path-environment-relevant">
                            <xsl:with-param name="path"
                                            select="./s:path/node()"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:if test="$relevantLocalPathContext != ''">
                        <s:chapter>
                            <s:title>Relevant Local Path Context</s:title>
                            <xsl:copy-of select="$relevantLocalPathContext"/>
                        </s:chapter>
                    </xsl:if>
                    <xsl:variable name="localPathContext">
                        <xsl:apply-templates select="./s:path.context"/>
                    </xsl:variable>
                    <xsl:if test="$localPathContext != ''">
                        <s:chapter>
                            <s:title>Local Path Context</s:title>
                            <xsl:copy-of select="$localPathContext"/>
                        </s:chapter>
                    </xsl:if>
                    <xsl:if test="./s:content/s:meta/rdf:RDF">
                        <s:chapter>
                            <s:title>Resources</s:title>
                            <xsl:apply-templates select="./s:content/s:meta/rdf:RDF/node()"/>
                        </s:chapter>
                    </xsl:if>
                    <xsl:if test="./s:content/s:meta/d:toDo or ./s:content/s:meta/d:todo">
                        <s:chapter>
                            <s:title id="net-splitcells-content-todo">Open Tasks</s:title>
                            <xsl:copy-of select="./s:content/s:meta/d:toDo"/>
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
                        <script type="text/javascript" charset="utf-8">
                            /* Disable all cookie functionality.
                            * This should make it impossible, to read or write cookies in the browser.
                            * This should also prevent read and write access to cookies via external frameworks.
                            */
                            document.__defineGetter__("cookie", function() { return '';} );
                            document.__defineSetter__("cookie", function() { return '';} );
                        </script>
                        <xsl:element name="script">
                            <xsl:attribute name="type">
                                <xsl:value-of select="'text/javascript'"/>
                            </xsl:attribute>
                            <xsl:attribute name="charset">
                                <xsl:value-of select="'utf-8'"/>
                            </xsl:attribute>
                            <xsl:attribute name="src">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/js/startup.js')"/>
                            </xsl:attribute>
                        </xsl:element>
                        <link rel="image_src" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/images/thumbnail/small/starting-to-learn-how-to-draw-a-face.jpg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="icon" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="alternate icon">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <link rel="mask-icon" type="image/svg+xml">
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url(net/splitcells/website/'icons/icon.svg')"/>
                            </xsl:attribute>
                        </link>
                        <meta name="viewport" content="width=device-width, initial-scale=1"/>
                        <link rel="apple-touch-icon">
                            <!-- Some Mobile browsers only support pngs as favicons. -->
                            <xsl:attribute name="href">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/icons/icon.png')"/>
                            </xsl:attribute>
                        </link>
                        <title>
                            <xsl:value-of select="concat(./s:title, ' / ', $siteName)"/>
                        </title>
                        <xsl:choose>
                            <xsl:when test="document('/net/splitcells/website/server/config/css/files.xml')">
                                <xsl:for-each
                                        select="document('/net/splitcells/website/server/config/css/files.xml')/d:val/*">
                                    <link rel="stylesheet" type="text/css">
                                        <xsl:variable name="tmp">
                                            <xsl:value-of select="."/>
                                        </xsl:variable>
                                        <xsl:attribute name="href">
                                            <xsl:value-of
                                                    select="s:default-root-relative-url($tmp)"/>
                                        </xsl:attribute>
                                    </link>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/theme.white.variables.css')"/>
                                    </xsl:attribute>
                                </link>
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/basic.themed.css')"/>
                                    </xsl:attribute>
                                </link>
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/basic.css')"/>
                                    </xsl:attribute>
                                </link>
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/den.css')"/>
                                    </xsl:attribute>
                                </link>
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/layout.default.css')"/>
                                    </xsl:attribute>
                                </link>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="./s:content/s:meta/s:pseudo-full-screen-by-default/node() = 'true'">
                            <link rel="stylesheet" type="text/css">
                                <xsl:attribute name="href">
                                    <xsl:value-of
                                            select="s:default-root-relative-url('net/splitcells/website/css/layout.pseudo.fullscreen.css')"/>
                                </xsl:attribute>
                            </link>
                        </xsl:if>
                        <xsl:choose>
                            <xsl:when test="./s:content/s:meta/s:full-screen-by-default/node() = 'true'">
                                <link rel="stylesheet" type="text/css">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/layout.column.main.fullscreen.css')"/>
                                    </xsl:attribute>
                                </link>
                            </xsl:when>
                            <xsl:otherwise>
                                <link rel="stylesheet" type="text/css" media="none">
                                    <xsl:attribute name="href">
                                        <xsl:value-of
                                                select="s:default-root-relative-url('net/splitcells/website/css/layout.column.main.fullscreen.css')"/>
                                    </xsl:attribute>
                                </link>
                            </xsl:otherwise>
                        </xsl:choose>
                        <!-- Javascript libraries are loaded, so these can be used by inline Javascript code or Javascript,
                             that is injected into elements via Javascript.

                             TODO Speed up page loading by using async and defer for Javascript in head. -->
                        <xsl:choose>
                            <xsl:when test="document('/net/splitcells/website/server/config/js/background-files.xml')">
                                <xsl:for-each
                                        select="document('/net/splitcells/website/server/config/js/background-files.xml')/d:val/*">
                                    <xsl:variable name="jsBackgroundFile">
                                        <xsl:value-of select="."/>
                                    </xsl:variable>
                                    <xsl:element name="script">
                                        <xsl:attribute name="type">
                                            <xsl:value-of select="'text/javascript'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="charset">
                                            <xsl:value-of select="'utf-8'"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="src">
                                            <xsl:value-of
                                                    select="s:default-root-relative-url($jsBackgroundFile)"/>
                                        </xsl:attribute>
                                    </xsl:element>
                                </xsl:for-each>
                            </xsl:when>
                        </xsl:choose>
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
                        <script type="text/javascript">
                            <xsl:if test="./s:content/s:meta/s:full-screen-by-default/node() = 'true'">
                                unshowByCssClass('page-column-0-full-screen');
                                unshowByCssClass('column_1');
                                showByCssClass('page-column-0-windowed');
                            </xsl:if>

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
                            checkAvailibility('net-splitcells-website-log-error');
                        </script>
                        <!-- Integration of https://www.mathjax.org. TODO Use local copy in future. -->
                        <script type="text/javascript">
                            Window.MathJax = {
                            tex: {
                            inlineMath: [['$', '$'], ["\\(", "\\)"]],
                            processEscapes: true,
                            }
                            }
                        </script>
                        <script id="MathJax-script" async=""
                                src="/net/splitcells/website/js/math-jax/es5/tex-mml-chtml.js"></script>
                        <script type="text/javascript" charset="utf-8">
                            <xsl:attribute name="src">
                                <xsl:value-of
                                        select="s:default-root-relative-url('net/splitcells/website/js/status-render.js')"/>
                            </xsl:attribute>
                        </script>
                    </head>
                    <body>
                        <header class="Standard_p5 topLightShadow">
                            <!-- TODO Move window menu to header. -->
                        </header>
                        <main id="topElement">
                            <div class="net-splitcells-content-column-left-border"></div>
                            <div class="net-splitcells-content-column">
                                <div id="content"
                                     class="net-splitcells-content-main">
                                    <article>
                                        <div class="splitcells-net-window-menu">
                                            <div class="splitcells-net-line net-splitcells-component-priority-2">
                                                <a class="net-splitcells-button net-splitcells-main-button-project-logo">
                                                    <xsl:attribute name="href">
                                                        <xsl:value-of
                                                                select="s:default-root-relative-url('net/splitcells/website/server/front-menu.html')"/>
                                                    </xsl:attribute>
                                                </a>
                                                <!-- The Menu link has no use in the desktop view, as the link and the menu are at the top of the site in this case. -->
                                                <a class="net-splitcells-button-inline minimal-only"
                                                   href="#net-splitcells-detailed-menu">Menu
                                                </a>
                                                <a class="net-splitcells-button-inline"
                                                   href="#net-splitcells-website-file-browser">Explore!
                                                </a>
                                                <div class="net-splitcells-error-status-indicator net-splitcells-button-inline"
                                                     style="visibility: hidden; display: none;">Error
                                                </div>
                                                <xsl:copy-of
                                                        select="$net-splitcells-website-server-config-window-menu"/>
                                                <div class="net-splitcells-space-filler"></div>
                                                <xsl:choose>
                                                    <xsl:when
                                                            test="./s:content/s:meta/s:full-screen-by-default/node() = 'true'">
                                                        <div class="net-splitcells-action-button net-splitcells-button-inline page-column-0-full-screen net-splitcells-minimal-not"
                                                             style="visibility: hidden; display: none;"
                                                             onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-0-full-screen');
										                unshowByCssClass('column_1');
										                showByCssClass('page-column-0-windowed');
													">
                                                            <xsl:text>☐</xsl:text>
                                                        </div>
                                                        <div class="net-splitcells-button-inline net-splitcells-action-button net-splitcells-action-text-button page-column-0-windowed optional"
                                                             onclick="javascript: fullScreenDisable();
													hide('page-column-0-windowed');
														unshowByCssClass('page-column-0-windowed');
														showByCssClass('page-column-0-full-screen');
														showByCssClass('column_1');">
                                                            <xsl:text>_</xsl:text>
                                                        </div>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <div class="net-splitcells-action-button net-splitcells-button-inline page-column-0-full-screen net-splitcells-minimal-not"
                                                             onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-0-full-screen');
										                unshowByCssClass('column_1');
										                showByCssClass('page-column-0-windowed');
													">
                                                            <xsl:text>☐</xsl:text>
                                                        </div>
                                                        <div class="net-splitcells-button-inline net-splitcells-action-button page-column-0-windowed optional"
                                                             style="visibility: hidden; display: none;"
                                                             onclick="javascript: fullScreenDisable();
													hide('page-column-0-windowed');
														unshowByCssClass('page-column-0-windowed');
														showByCssClass('page-column-0-full-screen');
														showByCssClass('column_1');">
                                                            <xsl:text>_</xsl:text>
                                                        </div>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </div>
                                            <div class="net-splitcells-structural-guide"/>
                                            <div class="splitcells-net-window-menu-line-2 net-splitcells-component-priority-1-a">
                                                <!-- This element is not a `splitcells-net-line-title`, so that it does not inherit its height. -->
                                                <div class="splitcells-net-line-title">
                                                    <xsl:if test="./s:title.detailed">
                                                        <xsl:value-of select="./s:title.detailed"/>
                                                    </xsl:if>
                                                    <xsl:if test="not(./s:title_detailed)">
                                                        <xsl:value-of select="./s:title"/>
                                                    </xsl:if>
                                                </div>
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
                                    <div class="net-splitcells-meta-column-left-border"></div>
                                    <div class="net-splitcells-meta-column column_1 contentCell Right_shadow">
                                        <!-- TODO Create complete table of content for this column. -->
                                        <article class="Standard_p2 net-splitcells-component-priority-2">
                                            <xsl:if test="$column_1 != ''">
                                                <div class="Right_shadow Standard_p2 splitcells-net-window-menu">
                                                    <div class="net-splitcells-structural-guide net-splitcells-vertical-columns-only"></div>
                                                    <div class="Standard_p2 bottomLightShadow splitcells-net-line net-splitcells-minimal-not net-splitcells-component-priority-2">
                                                        <div class="net-splitcells-space-filler"></div>
                                                        <div class="net-splitcells-action-button net-splitcells-button-inline page-column-1-full-screen optional net-splitcells-minimal-not"
                                                             onclick="javascript: fullScreenEnable();
														unshowByCssClass('page-column-1-full-screen');
										                unshowByCssClass('net-splitcells-content-main');
										                showByCssClass('page-column-1-windowed');">
                                                            <xsl:text>☐</xsl:text>
                                                        </div>
                                                        <div class="net-splitcells-action-button page-column-1-windowed optional"
                                                             style="visibility: hidden; display: none;"
                                                             onclick="javascript: fullScreenDisable();
													hide('page-column-1-windowed');
														unshowByCssClass('page-column-1-windowed');
														showByCssClass('page-column-1-full-screen');
														showByCssClass('net-splitcells-content-main');">
                                                            <xsl:text>_</xsl:text>
                                                        </div>
                                                    </div>
                                                    <div class="net-splitcells-structural-guide"/>
                                                    <div id="net-splitcells-website-file-browser"
                                                         class="net-splitcells-component-priority-2 splitcells-net-window-menu-line-2">
                                                        <xsl:choose>
                                                            <xsl:when test="./s:content/s:meta/s:description-title">
                                                                <div class="splitcells-net-line-title">
                                                                    <xsl:copy-of
                                                                            select="./s:content/s:meta/s:description-title/node()"/>
                                                                </div>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <div class="splitcells-net-line-title">Meta</div>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                    </div>
                                                </div>
                                            </xsl:if>
                                            <xsl:apply-templates select="$column_1"/>
                                        </article>
                                    </div>
                                </xsl:if>
                            </div>
                            <div id="net-splitcells-detailed-menu"
                                 class="menu Left_shadow TextCell Layout net-splitcells-component-priority-4">
                                <div class="Left_shadow net-splitcells-component-priority-0 splitcells-net-title-logo splitcells-net-window-menu">
                                    <div class="net-splitcells-structural-guide minimal-only"></div>
                                    <div class="splitcells-net-window-menu-line-1">
                                        <a class="net-splitcells-button-inline net-splitcells-main-button-project-logo">
                                            <xsl:attribute name="href">
                                                <xsl:value-of
                                                        select="s:default-root-relative-url('net/splitcells/website/server/front-menu.html')"/>
                                            </xsl:attribute>
                                        </a>
                                        <a class="net-splitcells-button-inline">
                                            <xsl:attribute name="href">
                                                <xsl:value-of
                                                        select="s:default-root-relative-url('net/splitcells/website/server/front-menu.html')"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="$siteName"/>
                                        </a>
                                    </div>
                                    <div class="net-splitcells-structural-guide"></div>
                                    <div class="splitcells-net-window-menu-line-2"></div>
                                </div>
                                <div class="net-splitcells-menu net-splitcells-priority-4">
                                    <a class="net-splitcells-button net-splitcells-action-button net-splitcells-component-priority-1 minimal-only"
                                       href="#topElement">
                                        Back to content
                                    </a>
                                    <section class="net-splitcells-website-menu-dynamic net-splitcells-component-priority-1">
                                        <!-- TODO This should probably be done via JavaScript. -->
                                        <xsl:apply-templates select="./s:content" mode="net-splitcells-gel-editor-menu"/>
                                    </section>
                                    <xsl:apply-templates mode="net-splitcells-website-menu"
                                                         select="$net-splitcells-website-server-config-menu-detailed">
                                    </xsl:apply-templates>
                                    <h3>Metadata About This Document</h3>
                                    <p>Unless otherwise noted, the
                                        content of this
                                        html file is licensed under the
                                        <a href="/net/splitcells/network/legal/licenses/EPL-2.0.html">EPL-2.0</a>
                                        OR <a href="/net/splitcells/network/legal/licenses/GPL-2.0-or-later.html">
                                            GPL-2.0-or-later</a>.
                                    </p>
                                    <p>Files and other contents, which are linked to by this
                                        HTML file, have their own rulings.
                                    </p>
                                    <div class="net-splitcells-space-filler"></div>
                                    <h3>Footer Functions</h3>
                                    <a class="net-splitcells-button net-splitcells-component-priority-3"
                                       href="#topElement">
                                        back to top
                                    </a>
                                </div>
                            </div>
                            <div class="net-splitcells-right-decoration-left-border"></div>
                            <div class="rightDecoration Right_shadow">
                                <div class="Borderless Standard_p2 Layout decorationBoxRight"
                                     style="position: relative; z-index: 2; width: 1.5em;"></div>
                                <div class="Borderless Standard_p3 Layout decorationBoxRight"
                                     style="position: relative; z-index: 3; width: 1.5em;"></div>
                                <div class="Borderless Standard_p4 Layout decorationBoxRight"
                                     style="position: relative; z-index: 4; width: 1.5em;"></div>
                            </div>
                        </main>
                        <footer class="Standard_p5 topLightShadow"/>
                    </body>
                </html>
            </xsl:otherwise>
        </xsl:choose>
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
                    <xsl:apply-templates select="./node()"/>
                </s:content>
            </s:layout.config>
        </xsl:variable>
        <xsl:apply-templates select="$layout.config"/>
    </xsl:template>
</xsl:stylesheet>