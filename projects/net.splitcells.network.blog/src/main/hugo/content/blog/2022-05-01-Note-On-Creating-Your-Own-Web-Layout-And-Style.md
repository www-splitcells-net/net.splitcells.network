---
title: Note On Creating Your Own Web Layout And Style
date: 2022-05-01
---
# Sometimes one has to learn by pain.
There is a known advice for new bloggers,
that recommends to use existing web layouts,
because making one from the start is a very time-consuming effort.
As one can expect, this was exactly, what I did not do for [splitcells.net](https://splitcells.net).
I could not find a satisfying web layout,
because I was too ***** to google good ones.
Another problem was the fact,
that I did not know what type of content would need to be supported by the layout.

So, I created one for `splitcells.net` based on Jekyll,
and transformed it over time.
This incrementally worked on layout is still in use at `splitcells.net`,
even though Jekyll was abandoned.

Luckily, the designing itself did not take a lot of time,
but bug fixing and refactoring the code took plenty of time.
# Cleaning up the mess.
By now, I'm at the point, where the state of the site is ok and
advantages of a custom layout and styling are present.
For instance, lines in text paragraphs have alternating background colors,
making it a lot easier for me personally,
to read text.

Another milestone is the basic support of arbitrary input formats and output styling ([#142](https://github.com/www-splitcells-net/net.splitcells.network/issues/142)).
The first one enables to write documents in different formats,
while maintaining a uniformed rendering for these formats.
For instance, one cannot differentiate pages written in CommonMark from
pages written in the website's own document format,
except for the fact,
that CommonMark documents do not have a table of content.
This probably will be implemented for CommonMark documents in the future as well.

The second one allows to support multiple web layouts simultaneously.
Currently, there are two web layouts and styles,
that are rendered by a single XSL.
One layout is the main current one.
The second layout, which uses [Tufte CSS](https://edwardtufte.github.io/tufte-css/),
is an experimental one and uses nearly no css classes.
Basically, I am implementing and testing style and layout simplifications on the experimental layout and
migrate the successful results to the main layout.

The end goal of [#142](https://github.com/www-splitcells-net/net.splitcells.network/issues/142)
is to find out,
which content features are really needed to be supported and
to create a migration path,
where the feature demands are being met with a code base that is reasonable.

Nevertheless, this whole ordeal took a tremendous amount of time,
which I will not get back.
# You're going to do, what you have to do.
So, what went wrong?
Was it really wrong to create my own web layout?

After going through the painful process of creating my own web layout,
I would argue, that it was not wrong to create your own web layout.
Keep in mind, that the web layout relates to HTML document and
therefore to the elements and its attributes.
This does not relate to the CSS files linked by the layout.

So what went really wrong?
I did not know which features would have needed to be supported semantically by the web layout,
and  so I created a layout where nearly every element had at least one css class.
Managing this styling was a nightmare and this was the main problem:
* I should have tried to use only HTML elements without classes.
* I should minimize the number of element types used.

This would enable me to use a simple external ready to go CSS style,
which I could replace completely or where parts could be overridden by additional custom CSS files.
By doing this, the cost of creating an initial OK looking layout would be a lot lower.
Incremental improvements would be easier too,
as part of it could be easily be replaced over time.

> In other words, I should have focused more on the data structure of a page and not it's styling and
> simplify the semantic meaning of its parts.

Put in other words, classless CSS for the win,
if ones styling has no special requirements or if one is starting a new web layout.
If one really needs CSS classes, one will know it sooner or later.
Starting a web layout from scratch with many CSS classes just begs for problems,
if one is not ready for it.