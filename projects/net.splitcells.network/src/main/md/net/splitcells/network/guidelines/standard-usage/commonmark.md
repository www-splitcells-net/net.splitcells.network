----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# CommonMark/Markdown Guidelines
[CommonMark](https://commonmark.org/) is used as the definitive Markdown syntax by default.

Only titles consisting of simple text without special symbols are considered linkable.
Blank lines before and after the start of lists are required.
Blank lines after and before headings seem not to be needed.
Minimizing blank lines makes the document more compact without making the source code less readable.
## Mathematical Formula Via LaTex
For LaTex math formulas [MathJax](https://www.mathjax.org) is used.
For inline math `\\(` and `\\)` are used in order to indicated Latex formulas.
For math blocks `\\[` and `\\]` are used as delimiters.
Keep in mind that in CommonMark double backslash (`\\`) has to be used,
in order to describe a single backslash.

Use inline math in order to quote math formulas and do not use normal
CommonMark quoting.