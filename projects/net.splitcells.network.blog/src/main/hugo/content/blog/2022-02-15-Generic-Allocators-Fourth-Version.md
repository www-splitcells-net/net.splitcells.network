---
title: Generic Allocator's Fourth Version
date: 2022-02-15
---
# Original Plan For The Next Version
Initially it was planned to solve all school problems for the next version:
* The Oral Exam Problem (solved)
* [The School Course Scheduling Problem](https://github.com/www-splitcells-net/net.splitcells.network/issues/8)
* [The Sport Lesson Assignment](https://github.com/www-splitcells-net/net.splitcells.network/issues/9)

Unfortunately, this seemed to be a too big task for the next version.
Other than the oral exam management problem the school scheduling problem
consists of multiple subproblems,
which depend on each other.

This on itself is not an issue,
but when attempting to tackle this challenge many small imperfections surfaced.
For instance, the low performance made the runtime painfully long.
Each additional complication made it obvious,
that the code needs a lot of polishing for productive usage.

The school problems were all already solved with an older experimental version of
the project and therefore this is a reimplementation.
So, it's not a surprise,
that the code quality is the main issue here,
as there were no practical usages of this project yet.
# Change of Plan
Instead of going on solving the scheduling problem,
I decided to tackle the N queen problem,
in order to polish the code a bit beforehand.
The school challenges would be approached in the next releases.
Thereby, the coming release would also be a lot smaller,
as it otherwise would get bigger and bigger,
while other side tasks are being done.

Regular releases are important,
in order to not get the main tasks out of sight.
# Results
The N queen problem can now be solved via the backtracking algorithm or the
constraint based repair algorithm.
The polishing improved the overhaul quality only a bit.
For example, the performance got a lot better,
but solving the 8 queen problem in 25 seconds is not really a grandios feat.
So, there's still a large room for improvement.
# Future Plans
In order to reduce the size of future releases,
the next 2 releases will solve one school problem each.