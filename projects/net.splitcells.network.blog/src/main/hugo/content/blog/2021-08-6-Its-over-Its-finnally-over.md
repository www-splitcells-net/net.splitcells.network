---
title: It's over. Its finally over
date: 2021-08-05
author: Mārtiņš Avots
license: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
---

# Emotional Relief

I created the first public release of the network project.
I pulled the rip line.
It's over. Its finally over
**Insert Frodo meme.**
**Insert** `I'm somewhat an open source developer myself` **Meme.**

**It ain't much, but it's honest work.**

# New Epoch

The third version is the first one,
that's publicly available,
where people can contribute
and breaking changes and other crazy shit is tried to be
omitted.

Functionality wise it is a partial downgrade,
also, some new functionalities are present as well.
The downgrade only affects the Generic Allocator,
but it is the most important project:

* The constraint system was simplified, and a useful
  query system was added.
  On the other, hand the set of ratings
  and constraint types are currently reduced.
  This will be fixed, when the school scheduling problem and the sport lesson
  assignment problem are modeled and solved again, but it will take some time.
* The constraint based repair optimizer is now a lot more generic.
  There is no need to reimplement it for each problem again,
  but the repairer lacks problem specific optimization capabilities
  and has therefore currently worse performance.
  This should be possible to reimplement with justifiable amount of work,
  but it's not present right now.

In short, the problem space which can be modeled is smaller,
but the optimizer became problem independent.

