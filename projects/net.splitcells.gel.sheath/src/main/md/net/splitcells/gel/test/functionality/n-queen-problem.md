# Constraint System Demonstration Via The N Queen Problem
![N Queen Problem](../../../../../../../../src/main/svg/net/splitcells/gel/test/functionality/n-queen-problem/illustration.svg)

The \\(N\\) queens puzzle requires \\(N\\) queens to be placed on a
\\(N \\times N\\) chessboard so that there is no row,
column or diagonal with more than one queen.
## Rating Function
The rating function of the N Queen Problem returns the number of queen pairs,
that are in same row, column or diagonal.
If there are no such pairs, the solution in question is optimal,
because no rules are violated.
The smaller the number of such pairs, the better one can consider a given
solution of the puzzle.
## Constraint Tree
Many pieces of optimization software have a system for modeling the rating
function of the problem in question and
this is also the case with the Generic Allocator (Gel).

Gel's constraint model is based on queries.
In other words, Gel's rating functions consist of queries,
whose results are added together.

Before making the model, it's best to transform the puzzle's natural
description,
which makes it easier to recreate the rating function based on Gel's model:
> For all columns, diagonals and rows, there should be only one queen.

The first sub clause can be simplified,
by splitting the description into multiple sentences:
> For all columns, there should be only one queen.
> For all diagonals, there should be only one queen.
> For all rows, there should be only one queen.

Now it's easy to see, that the rater can be described with a series of simple
queries.
A Gel's rater function is modeled as one query,
that can be build out of multiple ones,
but the rater itself has to be one query.
For this we use the phrase `For all`, as the starting point for all sub queries.
With this root clause one can create a simple tree,
which also represents the grammar of the cost function's natural description: 

![Constraint Tree](../../../../../../../../src/main/svg/net/splitcells/gel/test/functionality/n-queen-problem/constraint.tree.svg)
## Constraint Graph
In principle constraint trees can get so complicated,
that it makes sense to simplify them.

When observing the previous constraint tree, one might notice,
that some constraint nodes are identical.
In this example the removal of duplicate nodes transforms the tree to a graph,
because there are 3 paths in order to reach the leaf node:
* `For All` -> `Column` -> `At Most One Queen`
* `For All` -> `Diagonal` -> `At Most One Queen`
* `For All` -> `Row` -> `At Most One Queen`

On the other hand, it's easy to see, that the rater is still build out of
the same sub queries and that it is not possible to create circles in the graph,
when one makes each edge uni directional.
In other words, the following graph has the same meaning as the previous
constraint tree.

![Constraint Graph](../../../../../../../../src/main/svg/net/splitcells/gel/test/functionality/n-queen-problem/constraint.graph.svg)

## Note On Creating Constraint Models
It's possible to start the modeling process with a constraint graph instead of a
tree, but this can make the process harder.
Starting with a tree has the advantage, that one can just formulate a set of
independent queries, which unambiguous map to a specific constraint tree. 

In a constraint graph every node has a dependency to any other node,
so beginning with a tree reduces the number of node dependencies during the
initial modeling phase,
which can ease the creation phase.