---
title: Reprioritization
date: 2022-10-11
---
# Reprioritization
> At least to oneself, everybody should admit ones mistakes. 

So, basically I'm coming to the realization,
that the implementation of the [school course scheduling optimization](https://github.com/www-splitcells-net/net.splitcells.network/issues/8) takes too much time.
The first time in university, when I implemented it,
I had some initial performance problems,
but they were not that hard to solve.
At that time, I got that and a lot more working in less than a year.

Now I'm on my second year, and I'm still not done with this version.
Partly, because I am investing less time, compared to back than.
Partially, because I am introducing 'a ton' of secondary functionality,
like the webserver.

I'm starting to see an end to the tunnel,
and the biggest problems right now are relatively little pieces of code,
which are causing a ton of performance issues.
The code to correct these are not the most complicated,
but require relatively much time to be fixed and
unfortunately, there are many such pieces.
Most of the performance issues are caused by scaling issues.

And just don't look at the runtime performance for the [8-Queen via backtracking](https://splitcells.net/net/splitcells/gel/test/functionality/NQueenProblemTest/test_8_queen_problem_with_backtracking/splitcells-XPS-15-9570.csv.html).

In other words: not so much time investment right now +
much time invested in secondary tasks +
unexpected required amount of performance optimizations =>
slow grind

Simultaneously, I noticed that I have a usability and advertisement problem.
How do I enthrall people for this topic? By showing them Excel sheets for school courses?
Hardly!

# Gamification
> Dear reader meet Cin, Cin meet dear reader.

Another issue, that I noticed, was the fact, that each interesting optimization problem, that I was working on,
was isolated from each other.
Which is not bad in of itself,
but this does not lead to an awe effect,
because there is nothing big being present.
Nothing that people can touch, feel, think and interact with.
Nothing where regular people can get emotionally invested into.
No team to cheer, to support, to join and to weep for.
Nothing to fight for.

Simultaneously, I was also searching for a method to create a lot of test data and problems with meaning,
because solving the problems without meaning can lead to situations,
where one does not notice obvious flaws in calculated solutions for given problems.
This led to the idea to create some kind of mechanism to connect isolated problems to each other
and thereby creating bigger problems with meaning,
where double-checking the data regarding the practicality of a given solution is easier.

Such a mechanism could be seen as creating a network,
where problems, data and solvers can be registered to.
A network where a shared space connects each object and
thereby creates a world.
Wait a moment: I have a world and I want people to cheer and care about it?
A world that people can interact with?
Isn't this basically a game?

# Crisis Network (Cin)
> Making the school course scheduling a secondary task.

My current work style works like this:
I have a set of tasks which I'm mostly only working on the weekend.
I call these service tasks, because I work on these secondary issues on a very regular basis.
It's like providing a well-defined service, the service being to work a limited time on these tasks each weekend (~4 hours).
The main goal of these tasks are to get things done, where there is no concrete time goal,
but where I want to be sure, that these indeed will be done in some time.

Apart from that, I have 1 task, which is my current main goal.
I work on it primarily on any other day.
The school course scheduling was this primary task and
I decided to convert it to a secondary task (and thereby work only a bit on the weekend).

Instead, my one primary task now becomes the crisis network.
The main goal of the project, is to create a game,
where problems, data and solvers can be inserted into a world.
Making it bigger and more alive each time.
In other words a game, that is based on my optimization framework.

A big upside of this task is the fact,
that I am encouraged to improve and extend the accessibility and usability of the optimization framework's general GUI as well.

The game's GUI is planned to be implemented via HTML/Javascript on the web server and
the main audience is meant to be Steam Deck users (like myself),
also, the game will be distributed via Flatpak and not the Steam store.