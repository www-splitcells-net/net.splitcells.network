---
title: On Creating The First Public Release
date: 2021-07-07
author: Mārtiņš Avots
---
# Human's Software Development
Have you heard of phrases like
`You cannot measure productivity by the lines of code written.`?
Did you read, that
`It is considered to be a viable practice ship code as soon as the updater works.`?
Have you heard of the big problems of indefensible resource usage?
Did you gape at the horrible number of dependencies?
Did you pay attention to the humongous security problems in software and of its unreliability?

So, what do you as a software developer, maintainer, admin, tester, designer, manager etc., what do you do in order to fix this?
Phrases like `We have to write better code.`
come to one's mind.
Alternatively, mottos like `We have to invest more time into code.`
and `We have to write less code`
are signaled in the vast space of the internet.
Others propose `better management` or `superior paradigms`.

There are many criticized solutions to these problems.
Some say, that `Docker is bad, because of its massive use of storage.`,
but what's the alternative?
More often than not,
I see such critics as (Let me use a problematic term for the fun of it.) toxic.
Why, you may ask?

Let's be frank:
this is an emotional situation.
People often propose diverse solutions,
in order to get rid of the evil ones,
but not all of us have the luxury of doing things right.
Not all of us are gifted with minds suited for creating good solutions.
You cannot just get better developers or better decision makers in general,
because this is an emotional problem and
emotional problems are never just solved.
I would also say, that problems are not solved by making people more skilled, historically.

On a personal note,
I also sometimes get the impression, that people get mad of solutions,
because it is not done in their way or on their terms.
This is not a condemnation of the critics
or a blessing of every piece of new software.

So, as you probably guessed by now,
this post is an excuse for the development practices done in this project.
Welcome to the cringe party 🎉
# Developing The Universal Allocation Program
The first version of this project was done during an internship at university with a friend of mine
and was called the Universal Allocation Program or Nap.
It took an Excel file as input containing an optimization problem.
As output, the program also produced an Excel file, holding the best found solution to the problem.
It had a simple web page, where one could drop the input file and retrieve the output as a download.
The version had limited capabilities.
Nap was probably badly coded, as it was my first complex program.
Nap did its job and that is the most important part of it.

In hindsight, I noticed an emerging harmful pattern of mine during the development of Nap:
I searched for a good way, how to implement, organize and document program options
in Java, which was used for Nap.
I did not find any, and frankly I probably did not know where to look.
In Nap, there is no special configuration framework used.
Instead, a CLI library and some static final variables are used.

The desire for a configuration framework, was the starting point for feature creep in the following versions.
As always, the evil is paved with good intentions.
# Developing The Generic Allocator
Thereafter, I created a successor for my master thesis and named it humbly the Generic Allocator or short Gal.
For this project, I definitely wanted to create a good configuration library
and named it Dependency Manager (`net.splitcells.dem`).

One can see, how this may have been a starting point for feature creep in a lot of projects of mine:
* Use a framework with fundamental flaws and combine it with the desire for a better one.
* Furthermore, searches for good frameworks with no results, also seed the desire for creating further feature creep.

That's basically, what happened a lot of times.
Even with Gal itself: there are limited options, because of one's limited perspective
and therefore one constructs completely new programs, instead of using existing ones, in order to comply with demands.
Each new subproject has its own feature creep, which does sidetrack one from the
goal that really matters.

Gal, the second version of the optimization program,
was created in a simple way.
I created a new empty source code project.
For every file of the old version, I decided, what part could be reused and what needed to be changed.
Only the constraint system, was completely rewritten from the ground up,
because the solving algorithm relied on a new constraint model.

This way of migrating code basically worked.
My new configuration library, was the basis for many experiments.
It was therefore a nutritious soil for feature creep,
but was somewhat contained in this version.
# Developing The Third Version
Starting from this point, a very destructive path started.
After my master thesis I decided, to further work on this project and to create a third version.
The development plan was similar to the second version:
* Create a new empty project.
* Go through each file of the precursor and decide what parts should be adopted.

This seems to be fine on the first look
and a lot of code was migrated from the precursor to the successor.
So, everything is fine?

It took 4 years of implementation in order to even come close to finishing the third version,
and it does not have one test case, where the new functionality is really used.
Of course, I did not work non-stop on the project.
In matter of fact, I did not invest that much time into it,
but still: 4 years for nothing?

That is the toll of feature creep: being slowly killed by a thousand cuts.
# Migration Based Development and the Tower of Babel
When I kind of started to understand the problem and perceive its extent,
it was too late, as the new version already started to show its potential.
This does not mean, that deleting code is not an option,
but the trade-off of doing it, did not seem to be worth it anymore.

I started attempts to compensate for these problems:
* I started publishing the project, in order to have some skin in the game.
* A new development paradigm was adopted: migration based development
  or in other words `Do not rewrite or break functionality.`
* Make many commits and try to make 20 of them each day, in order
  to have continuous progress.

Does this sound familiar to the opening point?
It certainly feels so.
# A New Adventure
Now there is a blog for this project,
where I want to write down related emotional thoughts,
so one get a grip of its history, progress and future.
It may newer become a truly open source project with a community.
The gods know, how bad I am in social and many other settings.
On the other hand, this creates uncertainty, which in turn makes this whole
journey a lot more exciting.

We'll see, what will happen...