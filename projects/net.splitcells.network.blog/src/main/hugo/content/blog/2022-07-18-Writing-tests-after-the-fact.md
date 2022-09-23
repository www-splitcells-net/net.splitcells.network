---
title: Writing tests after the fact.
date: 2022-07-18
---
# Migrating The Webserver
> It's temporary. We'll make it nice later... Well, that was a lie.

The website server is the most often migrated piece of the project's software.
It started with Jekyll, which was abandoned in favor of an XML/XSL based static website renderer,
because it's easier to generate arbitrary output formats,
when one has full control of the input format and can therefore limit it as much as possible.
Than the software was refactored, because the quality was very poor.
Afterwards, support for rendering multiple per project websites as one website and
support for CommonMark was added.
This makes it easy to have documentation for each project independent of each other.
At last, the support for special pages, like the [global changelog](https://splitcells.net/net/splitcells/CHANGELOG.global.html) was added.

During every step, the software was migrated gracefully.
When Jekyll was started to be replaced,
both Jekyll and the successor were used simultaneously in order to render the complete website.
This made it possible to migrate one document after another,
without having the need, to do it all at once.
# Biggest Source Of Migration Errors
One might think, that building and deploying the website is a sufficient test on its own,
but this is not always the case.
On the one hand one has to ensure, that all links between pages are still valid,
which is easy to do,
if the website is a monolith.
This becomes harder to ensure,
when the website is a unification of multiple sub websites (1 project = 1 sub website),
where links between sub websites are quite common.

Another issue is the fact, that the website's renderer consists of multiple sub renderers.
One for each file format.
Here the biggest issue is the mapping between URLs and the appropriate renderers.
Big spaghetti-if-else-hells without tests were just asking for trouble.
Every time a new sub renderer was introduced or a bug in the URL/renderer mapping was fixed,
a new bug was created, because nothing was tested.
# Conclusion
> You know what's coming.

For ones own sanity, if one migrates a functionality, prefer automated tests for the feature in question.