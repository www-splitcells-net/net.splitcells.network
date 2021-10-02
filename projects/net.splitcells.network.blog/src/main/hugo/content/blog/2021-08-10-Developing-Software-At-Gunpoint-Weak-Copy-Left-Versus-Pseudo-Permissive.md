---
title: "Developing Software at Gunpoint: Weak Copy Left Versus Pseudo Permissive"
date: 2021-08-10
author: Mārtiņš Avots
license: EPL-2.0 OR MIT
---
I am not a lawyer: This is no legal advice,
the following only represents some of my opinions and understandings.
Make sure to consult someone, who knows what they're talking about.

Don't take this article too seriously,
you probably have better ways to waste your time. 🍺

This just summarizes the key distinction between the Apache 2.0 and the EPL 2.0
from my point of view,
because both were considered as a license for this project.
# Comparing Apache 2.0 with EPL 2.0
Also, the Apache 2.0 is considered to be a permissive license,
it defines the term `Derivative Works` and places additional demands on these.
It does not require modifications to be licensed under the Apache 2.0 as well,
but the original code cannot be sublicensed, except for binary distributions.
Off-topic: it also seems to have a pretty clear definition of
`Derivative Works`, when compared to other licenses.

Weak copyleft licenses like the EPL 2.0 on the other hand can, under certain
conditions, be pretty similar in this regard.
To summarize it very shortly and very incorrectly:
> If a work is not a direct modification of the original, it works similar to
> the Apache 2.0 in terms of licensing.

Both the EPL 2.0 and the Apache 2.0 allow binary distributions under a different
license and both require, that the original work stays under the original
license.
Of course, there are differences regarding i.e. attribution,
but these do not seem to be relevant in this context.
# Initial Situation
The most relevant parties considered in this practice seem to be companies
with proprietary code, which will be called secrets in this context.
Let's consider the simplest case:
we have 2 parties working on a common open source project (OSP) and both of them
have secrets.

Some secret code is not worth being protected/hidden.
Code always needs some maintenance and that binds resources, which in terms
of developer time can be worth a tremendous amount.
If the code is also not related to the core business, others may not even be
able to use such secrets against the authoring company.
Additionally, some software may even be too complex to be created by one
company.

Both parties make some of the secrets public in order to share the cost for
common functionalities.
Nobody want's to reimplement their own archive file formats,
just to be sure, that they exclusively possess the code and can control
its binary distribution (just like you hopefully did not buy WinRAR).
Buying a license for such code/program in order to use it as a dependency does
also not seem to be worth it most of the time.

In order for both parties to cooperate in a predictable manner a framework
for the group work is being created, where licensing is our biggest concern.
## Pseudo Permissive via Apache 2.0
I call Apache 2.0 pseudo permissive in this context.
Sublicensing clauses like the one
of MIT allows one in theory to add additional distribution restrictions to the
source code. One can make changes to the source code and make it completely
proprietary or license it under the GPL, which also adds new restrictions.

The Apache 2.0 does not allow this.
Source code licensed under the Apache 2.0 stays under this license,
also this does not apply to modifications of such source code.

Especially, code licensed just under Apache 2.0 cannot be combined with code
licensed under the GPL licenses.
# Paradigms of Keeping Everybody in Check
Maybe this metaphor does not make sense, but let's try it, shall we? 🤪
## Apache 2.0
Metaphorically speaking, the 2 parties point at each other with guns and state,
that all shared secrets put into the open source project,
shall stay there:
> You let me use your public parts, and I let you use my public parts.
## EPL 2.0
Metaphorically speaking, the 2 parties point at each other with guns and state,
that all shared secrets put into the open source project,
shall stay open and pledge, that these secrets are up-to-date.
> We both publish our parts, and ensure, that these are up-to-date.

The article is licensed under the [EPL-2.0](http://splitcells.net/net/splitcells/network/legal/licenses/EPL-2.0.txt)
OR [MIT](http://splitcells.net/net/splitcells/network/legal/licenses/MIT.txt).