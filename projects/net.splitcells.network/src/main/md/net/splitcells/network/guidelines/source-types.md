----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Source Type Guidelines
## Primary Used File Formats
* Changelogs use the [keepachangelog.com](https://keepachangelog.com/en/1.0.0/)
  format.
* Version numbering are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
* SVG is used for images, where possible. Alternatively, JPEG is used.
* Den XML (`http://splitcells.net/den.xsd`) is used for AST like structures with namespaces.
* Articles and flowing text should be written in Sew XML (`http://splitcells.net/sew.xsd`),
  if complex functionality is required or simple flowing text does not support the requirements for the document.
  Normally one would expect, that a common format like CommonMark
  would be used.
  The problem with such formats is the fact, that common formats support too
  many features or that not enough features are provided.
  It is also hard to predict, what features will be needed in the future.
  An own formats has the advantage, that only features are supported,
  that are required, and that the future set can be adjusted easily. .
  The fact, that XML provides an easy way to translate such code to any target
  language makes such a custom format compelling.
* Presentations based on HTML and `reveal.js`.
* XML schemas based on XSD.
* Mermaid is used in order to visualize graphs,
  where only the relative arcs between 2 nodes (node a points to node b) is stored.
  It is preferred to visualize every graph as flowcharts (`graph **` in Mermaid notation),
  if possible,
  as this is generally the most widely by software supported graph type.
  For more complex graphs it is harder to find software.
  In this case pure SVGs should be used instead.
## Secondary Supported File Formats
### Reasons For Secondary File Formats
These formats are primarily used for compatibility reasons.
For example, it is a good idea to write READMEs in the CommonMark format,
so they can easily be read on git hosters like [sourcehut](https://sourcehut.org/)
and [Github](https://github.com/).

Secondary supported file formats should be omitted,
if there is not a good reason for using them.
The reason for this is the fact,
that primary supported formats are better supported than secondary supported
formats.

Secondary formats in general often require a lot of additional code in order to handle their Syntax,
which makes it hard to support these, convert these or port these.

### CommonMark
[CommonMark](https://spec.commonmark.org/0.29) itself is not suitable as a primary file format,
[as does not seem to have a formal grammar](https://talk.commonmark.org/t/commonmark-formal-grammar/46/26)
and it seems there is no intention to have one.
This reduces the portability of CommonMark converter implementations
(which is also a problem for XSL in this project).
In other words, it is easier to convert a generic tree structure file format to CommonMark,
than the other way around.

Limit the usage of CommonMark features as much as possible for complex documents.
For documents only containing images, flowing text or nested lists CommonMark is suitable format,
because of its simplicity and portability.

All inlined artifacts of the flowing text should be integrated via links.
Inline LaTex that is compatible to MathJax is used for mathematics formulas
and is the only exception to this rule.

Metadata can be stored at the beginning of the document via a special section:

```
---
attribute-name: attribute value
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
---
```

This format is supported by many static blog generators like Jekyll, Hugo and next.js,
and therefore supported by this project.
The reason for such, is the fact, that [there does not seem to be any intent to support meta data in in CommonMark anytime soon](https://talk.commonmark.org/t/standardized-metadata-layer/2429).

Note: Contrary to popular believe Mārtiņš Avots believes it should be possible,
to create a simplified CommonMark grammar via BNF,
in order to make CommonMark a primary supported format.
This should be attempted at some point.
2 text formats could be useful,
in order to have different software for complex and for simple documents,
as this would make simple documents more easily portable.
This could help to avoid mixing simple and complex data structures and
thereby reduce overhaul complexity.
On the other hand, a complex format supporting both via a simplified subset grammar,
solves the same problem as well.
As long as CommonMark stays popular, having 2 text formats may be the best solutions,
where CommonMark is used for flowing text and
the complex format is used for storing data inside trees like XML and JSON.
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
