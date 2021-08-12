# Assignment Problems
An assignment problem requires that demands are assigned to supplies.
For a given assignment set \\(X\\) the assignment problem's \\(acceptable\\) function
returns \\(1\\) ,
if the assignment set is an acceptable solution, and \\(0\\) otherwise.

\\[
acceptable(X) \\rightarrow \\Set{0,1}
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
as following \[[related](../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/1995.DISKI.md#pages-12-to-14)\]:

\\[
x_{na} \in X: x_{na} =
\begin{cases}
    1, & \text{if $d$ is assigned to $s$},\\\\
    0, & otherwise.
    \end{cases}
\\]
