<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:r="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- TODO Make advertisment object data part of support system.-->
    <xsl:variable name="advertisement-object-social">
        <r:RDF>
            <r:Description r:resource="https://makroskop.eu/">
                <r:label>MAKROSKOP</r:label>
                <r:comment>German Page about Economy and Politics</r:comment>
            </r:Description>
            <r:Description r:resource="https://www.flassbeck-economics.com/">
                <r:label>https://www.flassbeck-economics.com/</r:label>
                <r:comment>International Page about Economy and Politics</r:comment>
            </r:Description>
            <r:Description r:resource="http://slatestarcodex.com">
                <r:label>Slate Star Codex</r:label>
                <r:comment>A Blog About Science, Medicine, Philosophy, Politics, and Futurism</r:comment>
            </r:Description>
            <r:Description r:resource="https://alternativlos-aquarium.blogspot.com/2017/09/ueber-mich.html">
                <r:label>Das Alternativlos-Aquarium</r:label>
                <r:comment>Political Blog</r:comment>
            </r:Description>
            <r:Description r:resource="https://danisch.de">
                <r:label>Danisch</r:label>
                <r:comment>Challenging Political Blog</r:comment>
            </r:Description>
            <r:Description r:resource="https://www.youtube.com/user/LianaK/videos">
                <r:label>Liana K</r:label>
                <r:comment>Lady Bits</r:comment>
            </r:Description>
            <r:Description r:resource="https://www.youtube.com/user/rossmanngroup/videos">
                <r:label>Louis Rossmann</r:label>
                <r:comment>Repair Group</r:comment>
            </r:Description>
            <r:Description r:resource="https://www.youtube.com/channel/UCEh7V-I77Cs9st5gtH4wd7w/videos">
                <r:label>Roma Army</r:label>
                <r:comment>Equality Advocate</r:comment>
            </r:Description>
        </r:RDF>
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