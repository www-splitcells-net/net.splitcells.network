# Source Type Guidelines
## Used Standards
### Primary Used File Formats
* Changelogs use the [keepachangelog.com](https://keepachangelog.com/en/1.0.0/)
  format.
* Version numbering are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
* SVG is used for images, where possible. Alternatively, JPEG is used.
* Den XML (`http://splitcells.net/den.xsd`) is used for AST like structures with namespaces.
* Articles and flowing text should be written in Sew XML (`http://splitcells.net/sew.xsd`).
  Normally one would expect, that a common format like CommonMark
  would be used.
  The problem with such formats is the fact, that common formats support too
  many features or that not enough features are provided.
  It is also hard to predict, what features will be needed in the future.
  An own formats has the advantage, that only features are supported,
  that are required, and that the future set can be adjusted easily.

  Also, keep in mind, that some features are very specific to Splitcells
  Network (i.e. linking between documents).
  The fact, that XML provides an easy way to translate such code to any target
  language makes such a custom format compelling.
* Presentations based on HTML and `reveal.js`.
* XML schemas based on XSD.
### Secondary Supported Used File Formats
These formats are primarily used for compatibility reasons.
For example, it is a good idea to write READMEs in the CommonMark format,
so they can easily be read on git hosters like [sourcehut](https://sourcehut.org/)
and [Github](https://github.com/).

Secondary supported file formats should be omitted,
if there is not a good reason for using them.
The reason for this is the fact,
that primary supported formats are better supported than secondary supported
formats.
* [CommonMark](https://spec.commonmark.org/0.29) is used for flowing text.
  All artifacts of the flowing text should be integrated via links.
  Inline LaText that is compatible to MathJax is used for mathematics formulas
  and is the only exception to this rule.
### Programming Languages
* Java is used as a primary language.
* Python 3, sh and Bash(deprecated) is used for scripts and programs where
  integration and managing different piece of software is of primary concern.
* XSL is used for transforming XML files and rendering the website's layout,
  which is deprecated.
## Organization of Source Types
The number of formats/protocols should be minimized.

Avoid creating new own protocols and formats,
if possible,
because these create maintenance burdens.
Prefer create new pseudo protocols by using a restricted version of
existing and established protocols instead.
Restricted version of established protocols often increase portability.
The own article schema for the website based on XML is deprecated.

Minimize the number of protocols inside one files.
In the best case only one format is present per file.
If multiple formats are needed for one file,
try creating multiple files with the
same file name suffix and one format for each such file.