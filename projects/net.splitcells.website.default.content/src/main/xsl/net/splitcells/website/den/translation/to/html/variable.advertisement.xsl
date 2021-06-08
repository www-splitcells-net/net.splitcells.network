<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:r="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- TODO Make advertisment object data part of support system.-->
    <xsl:variable name="advertisement-object-social">
    </xsl:variable>
    <xsl:variable name="advertisement-object-technical">
        <r:RDF>
            <r:Description r:resource="https://www.phoronix.com/">
                <r:label>Phoronix</r:label>
                <r:comment>Benchmarking Platform</r:comment>
            </r:Description>
            <r:Description r:resource="https://languagetool.org/">
                <r:label>LanguageTool</r:label>
                <r:comment>Open Source Proofreading Software</r:comment>
            </r:Description>
            <r:Description r:resource="https://getfedora.org/">
                <r:label>Fedora</r:label>
                <r:comment>Operation System</r:comment>
            </r:Description>
            <r:Description r:resource="https://ubuntu.com/">
                <r:label>Ubuntu</r:label>
                <r:comment>Operation System</r:comment>
            </r:Description>
            <r:Description r:resource="https://gimp.org/">
                <r:label>GIMP</r:label>
                <r:comment>Open Source Image Editor</r:comment>
            </r:Description>
            <r:Description r:resource="https://yacy.net">
                <r:label>YaCy</r:label>
                <r:comment>Decentralized Web Search</r:comment>
            </r:Description>
            <r:Description r:resource="https://swaywm.org/">
                <r:label>Sway</r:label>
                <r:comment>Linux Window Manager</r:comment>
            </r:Description>
            <r:Description r:resource="http://www.scottaaronson.com/blog/">
                <r:label>Shtetl-Optimized</r:label>
                <r:comment>The Blog of Scott Aaronson</r:comment>
            </r:Description>
        </r:RDF>
    </xsl:variable>
</xsl:stylesheet>