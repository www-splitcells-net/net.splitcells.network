# Assignment Problems
An assignment problem requires that demands are assigned to supplies.
For a given assignment set \\(X\\) the assignment problem's \\(valid\\) function
returns \\(1\\) ,
if the assignment set is an valid solution, and \\(0\\) otherwise.

\\[
valid(X) \\rightarrow \\{0,1\\}
\\]
## One-Dimensional Assignment Problems
![One-Dimensional Assignment Problems](../../../../../../../../../../src/main/svg/net/splitcells/gel/problem/theory/assignment/problem/index.illustration.svg)
> Solution For An One-Dimensional Assignment Problem

As shown above in an one-dimensional assignment problem
elements of the demand set \\(D\\) are assigned to elements of the supply set
\\(S\\) [1].
Every possible assignment of a demand \\(d\\) to a supply \\(s\\) is represented by the
variable \\(x_{na}\\) .
The actual assignments for a given solution are represented by the set \\(X\\)
as following [[DISKI 86](../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/1995.DISKI.86.md#pages-12-to-14)]:

\\[
x_{na} \in X: x_{na} =
\\begin{cases}
    1, & \\text{if $d$ is assigned to $s$},\\\\
    0, & otherwise.
    \\end{cases}
\\]

A complete solution is an assignment set \\(X\\),
where each demand has assigned at least one supply.
Often it is also required that each demand has at most one supply.
If there has to be exactly one supply for each demand,
then it can be called an 1 on 1 problem.
In this case, the set of demands and the set of supplies has to have the same
size.
If one demand are allowed to be assigned to N supplies,
this can be called an 1 on N Problem.
# Assignment Rating
Often it is the case, that there are multiple valid solutions to a problem.
Simultaneously, often these valid solutions have not the same worth,
so a way is needed to differentiate between these.
This is done by calculating a rating \\(R\\) for the assignment of a solution,
which in turn are can be sorted by worth.

\\[
X \\rightarrow R
\\]
# A Semantic Extension of the Assignment Problem
The assignment problem itself does not model the semantic meaning of the problem
at hand.
It just states that a set of demands and a set of supplies are assigned to each
other.
Each demand and each supply has an inherit identity and
there is no inherent semantic meaning of an identity.

When the N-Queen puzzle is modeled as an assignment problem,
each demand and supply is just an identity.
The problem also restricts the set of valid assignments via the \\(valid\\)
function.
The function itself does describe, if a given solution is incorrect.
The model does not state which row corresponds to which elements of the
supplies.

The semantic extension is implemented via a set of attribute functions
\\(A\\).
Each attribute function \\(a \\in A\\) returns a value,
for all supplies \\(S\\),
demands \\(D\\) and assignments \\(X\\).
The value returned by the attribute, describes or relates to some meaning and
can be of any type \\(N\\).
An easy to handle type \\(N\\) is \\(\mathbb{N}\\),
that itself can for instance represent the set of all strings and is therefore
compatible with a lot of other types.

\\[
\\forall a \\in A: \\forall o \\in ( D \\cup S \\cup X ) =
\\begin{cases}
    a(o) \\in N, \\text{if defined},\\\\
    \\text{undefined, otherwise}.
    \\end{cases}
\\]
> Generic Attribute Function Set Signature
 
\\[
\\forall a \\in A_\mathbb{N}: \\forall o \\in ( D \\cup S \\cup X ) =
\\begin{cases}
a(o) \\in \mathbb{N}, \\text{if defined},\\\\
\\text{undefined, otherwise}.
\\end{cases}
\\]
> Naturally Numbered Attribute Function Set Signature

In most cases it does not make sense to provide values by an attribute function
\\(a\\) for supplies and demands.
In most cases it makes sense, that an attribute function
has only defined values for supplies or only for demands.
The queens in the N-Queen problem,
do in general not have an inherent row and column,
because otherwise the solution of the problem would be already present.
The row and column belong to the supplies,
because these are the optimization variables.

\\[
A_{limited} =
\\left\\{a \\in A |
\\begin{array}{l}
    \\forall d \\in D: a(d) \\in N \\land \\forall s \\in S: a(s) = \\text{undefined}\\\\
    \\lor \\forall s \\in S: a(s) \\in N \\land \\forall d \\in D: a(d) = \\text{undefined}\\\\
    \\lor \\forall x \\in X: a(x) \\in N
    \\end{array}
\\right\\}
\\]