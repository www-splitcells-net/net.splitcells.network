<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ns="http://splitcells.net/namespace.xsd"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:t="http://splitcells.net/text.xsd"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
    <xsl:template match="n:val">
        <xsl:apply-templates select="." mode="perspective"/>
    </xsl:template>
    <xsl:template match="n:text">
        <xsl:apply-templates select="./node()" mode="natural-text"/>
    </xsl:template>
    <xsl:template match="text()" mode="natural-text">
        <xsl:for-each select="tokenize(., '\n\n')">
            <xsl:variable name="tmp">
                <s:paragraph>
                    <xsl:value-of select="."/>
                </s:paragraph>
            </xsl:variable>
            <xsl:apply-templates select="$tmp"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>