----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# CommonMark/Markdown Guidelines
[CommonMark](https://commonmark.org/) is used as the definitive Markdown syntax by default.

Only titles consisting of simple text without special symbols are considered linkable.
Blank lines before and after the start of lists are required.
Minimizing blank lines makes the document more compact without making the source code less readable.
## Mathematical Formula Via LaTex
For LaTex math formulas [MathJax](https://www.mathjax.org) is used.
For inline math `\\(` and `\\)` are used in order to indicated Latex formulas.
For math blocks `\\[` and `\\]` are used as delimiters.
Keep in mind that in CommonMark double backslash (`\\`) has to be used,
in order to describe a single backslash.

Use inline math in order to quote math formulas and do not use normal
CommonMark quoting.

## Note on CommonMark's Grammar

A general problem with CommonMark is, that it's grammar is not unambiguous.
[CommonMark does not have a formal grammar, because people don't want one.](https://talk.commonmark.org/t/commonmark-formal-grammar/46)
Consider creating a formal Grammar via ANTLR for CommonMark,
in order to fully support CommonMark and to be able to migrate from CommonMark to any replacement format.
This would effectively be a subset of CommonMark.

Furthermore, this could be the basis for creating CommonMark files more compactly,
by avoiding blank lines between elements,
while still supporting rare renderers, that cannot handle the absence of such blank lines
(i.e. blank line between title and paragraph).
