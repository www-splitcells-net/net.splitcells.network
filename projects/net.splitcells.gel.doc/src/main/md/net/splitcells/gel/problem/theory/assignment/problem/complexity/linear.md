# Linear Assignment Problem
## Linear Sum Assignment Problem (LSAP)
All complete assignments are valid solutions.
Every possible assignment has a constant cost,
that is independent of other assignments.

\\[
\\begin{align*}
    \\begin{aligned}
        &R(x_{11}, ...,x_{NA}) = \\sum\\limits_{n,a}^{N,A} c_{n,a} \\times x_{na}\\\\
        &c_{na} \\in \\mathbb{R}
        \\end{aligned}
    \\end{align*}
\\]

LSAPs can be solved in \\(O(N^3)\\) via the Hungarian method.
The Busacker Goven algorithm can solve LSAPs in \\(O(N 3 × log(N ))\\),
but is often faster than the Hungarian method
[[DISKI 86](../../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/1995.DISKI.86.md#page-22)].
## Level 1