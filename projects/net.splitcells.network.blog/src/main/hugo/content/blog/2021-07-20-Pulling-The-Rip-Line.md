---
title: Pulling The Rip Line
date: 2021-07-15
author: Mārtiņš Avots
license: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
---
# The Situation
As mentioned in the previous post,
the current state of the third version of the optimization framework Generic
Allocator (Gel) is neither done nor is it in good shape.

In the previous version, a general optimization algorithm was designed
and implemented 3 times for 3 different problems.
I called this constraint group based repair.
The purpose of the successor version was to make the solving routine implementations suitable for
a wider range of problems.
As mentioned in the prior article, this is done via a kind of complete rewrite,
but this takes an unjustifiable amount of time.

So I decided to pull the rip line
and end this madness.
# The Plan
Currently, a simplified and working version of the repairer is implemented.
One of the three demonstration problems of the precursor version can be solved by the solver.
Most of the rewrite is done, and the new architecture is implemented.
I think, that this is enough of functionality for the third version.
It is certainly a step back in terms of the feature set,
but at least the architecture seems to be simplified
and the software kind of works.

So, I'm now ending the development of the third version
and focusing on cleaning up the metadata:
* Fixing the licensing info.
* Creating a contribution protocol.
* Defining the API development model.