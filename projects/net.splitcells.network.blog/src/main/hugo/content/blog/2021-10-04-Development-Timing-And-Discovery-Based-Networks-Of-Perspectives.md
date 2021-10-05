---
title: "Development Timing And Discovery Based Networks Of Perspectives"
date: 2021-10-04
author: Mārtiņš Avots
license: EPL-2.0 OR MIT
---
# Development Timing And Discovery Based Networks Of Perspectives (Den)
One has always more ideas or needs,
than time needed to implement or satisfy such.
discovery based networks of perspectives or short Den are such an example.

It's basically a concept for an abstract syntax tree (AST)
and related semantics and paradigms.
I have spent quite a time formalising and working with them,
but other than one would expect,
there is actually not much written down regarding this topic.

There is a very short, incomplete and incorrect
[documentation](http://splitcells.net/net/splitcells/dem/lang/perspective/index.html),
that probably nobody understands and is of no use to other people.
There is a private XML schema used for storing such ASTs,
which I am extensively using in private documents.
Schema validation is rarely applied,
also it was finally integrated into the webserver somewhat last year.
There is a Java class used for creating and handling such trees.
A very basic, incomplete and incorrect implementation of it for my logging
system is also present.

The poor state of the whole system is caused by the fact,
that the returns of a complete implementation are low
and its costs are non-zero.

Also, I'm using many of the concepts daily,
I'm rarely working on the documentation,
because I'm really REALLY bad at writing.
XML schema validation is currently not the top priority,
because new much needed features and related bugfixes
have currently a higher priority.
Integration of CommonMark files into the webserver is vastly more important,
because the CommonMark format is vastly more used than the private Den
XML format.

The situation related to the Java code on the other hand is changing currently,
at least somewhat.
Ironically, the urgent need for CSV like export for logging,
which can be done via Den,
created the possibility to work on Den in a meaningful way.
Den can also be used in order to get a better console output for the program,
which was XML and therefore very verbose.
It also provides a way in order to get rid much of the XML code of the Java
standard library,
which is very poor and causes problems because of it.

In other words, I had better deals,
but the situation is changing a little bit.
This creates an opportunity for quality improvements.
# Current State Of Implementation
Currently, there is the Java class `net.splitcells.dem.lang.perspective.Perspective`,
that implements the AST.
A node just consists of a namespace, a string value, child references
and some helper functions.
There is a simplified serialization,
but it is just a simple hack,
which needs improvements.

There are no tests and code quality is expectably bad.
# Den Grammar Draft By Example
I have currently no formal grammar,
because there is currently no urgent need for it.
The current draft is the following:
```
dictionary:
    a key.a value
    ':'.Colons with indentation are used in order to mark dictionaries
    '.'.Dots are used in order to mark single dictionary entries and can be chained
    a.'String with no escape sequences.'
    b.'A simple string
        with multiple lines.'
    c."A string with escape sequences based on slashes.
        These are more complex to implement,
        but makes it possible to use special characters used by this format for other purposes.
        Following escape sequences are supported: \\ = backslash, \\" = quote symbol and \\n = new line symbol"
    "There is no distinction between keys"."and values."
    d.delimiter"
#!/usr/bin/env bash
echo This is a bash script inside a Den AST.
"delimiter
A.long.path.with.multiple.elements.can.be.placed.in.one.line
    .or
    .in
    .multiple
    .lines
Tables can be modeled as well:
    header.column name.column name
    row.value.value
    row.value.value
```
The main focus of this syntax is to highlight hierarchies and make it easy
to traverse these by looking at them.
The syntax should also be able to represent a workflow,
how one creates the hierarchy in question.
In other words, when someone reads out loud such a document,
others should be able to understand it:
```
dictionary
<chapter>
<entry>
a key
<has>
a value
<entry>
:
<has>
Colons with indentation are used in order to mark dictionary
```
Entering such documents with text editors providing automated indention
should be possible with a similar workflow.
# Namespaces and Schemas
Things like namespaces and schemas are not part of the format,
but can be implemented without changing the syntax.
Currently, the idea is to use something similar to XML by using a prefix
and a dash (`-`) as a delimiter.
There are currently no ideas for a dedicated schema format
as schema checking could be done easily with normal code and no
use by third parties is expected at the time.

Help from outside is always welcome 😊
## Namespace Recommendations
Complex namespace constellations inside one document should be omitted,
if possible,
because it can lead to complex code for processing.
Unfortunately, complex namespace constellations cannot be prohibited in general.
There are valid use cases for such like storing all information/state of system
into one document.

I would recommend using one namespace per document,
if this does not create too many costs.
If one writes a blog article for example,
one can use a dedicated file for associated tasks:
```
article-name.md
article-name.todo.den
```
If multiple namespaces are used nesting the same namespace multiple times
should be omitted,
if possible.
References between such namespace layers should also be omitted.
```
a-dictionary:
    b-dictionary:
        a-dictionary:
```
> This should be omitted.
# CSV Export
CSV documents with dots as the main delimiters can be compatible to Den
documents.
```
column.column
value.value
value.value
```
> This is a Den compatible CSV file.
# Future Development Plan
The optimization project Gel is in need for an import/input and export/output
language.
In order to minimize the number of grammars and file formats,
Den will be probably used for this goal,
but before this will be attempted 2 tasks are scheduled beforehand at the moment:
* [Solve school scheduling problem.](https://github.com/www-splitcells-net/net.splitcells.network/issues/8)
* [Solve sport lesson assignment.](https://github.com/www-splitcells-net/net.splitcells.network/issues/9)

So extended Den development is scheduled for next year.
Tickets for this project will be processed on [sourcehut](https://todo.sr.ht/~splitcells-net/net.splitcells.network).

This project will be used in order to improve code quality as well,
but is not the only such project.
The following task currently improves code quality as well,
a bit: [Create minimal Java grammar for this project in order to make code less complex.](https://github.com/www-splitcells-net/net.splitcells.network/issues/10)

The article is licensed under the [EPL-2.0](http://splitcells.net/net/splitcells/network/legal/licenses/EPL-2.0.txt)
OR [MIT](http://splitcells.net/net/splitcells/network/legal/licenses/MIT.txt).