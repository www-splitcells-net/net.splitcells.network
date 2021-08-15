# Linear Assignment Problem
## Linear Sum Assignment Problem (LSAP)
All complete assignments are valid solutions.
Every possible assignment has a constant rating,
that is independent of other assignments:

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

It has the same rating function as LSAPs:

\\[
\\begin{align*}
    \\begin{aligned}
        &R(x_{11}, ...,x_{NA}) = \\sum\\limits_{n,a}^{N,A} c_{n,a} \\times x_{na}\\\\
        &c_{na} \\in \\mathbb{R}
        \\end{aligned}
    \\end{align*}
\\]

On the other hand, not all assignments valid.
The validity criteria can be described via a set of linear equality and
inequalities as following
[[DISKI 86](../../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/1995.DISKI.86.md#pages-12-to-14)]:

\\[
\\begin{align*}
    \\begin{aligned}
        &b_{11} \\times x_{11} + ... + b_{1N} \\times x_{1N} \\leq c_1\\\\
        &...\\\\
        &b_{a1} \\times x_{a1} + ... + b_{aN} \\times x_{aN} \\leq c_a\\\\
        &d_{11} \\times x_{11} + ... + d_{1N} \\times x_{1N} = e_1\\\\
        &...\\\\
        &d_{a1} \\times x_{a1} + ... + d_{aN} \\times x_{aN} = e_a\\\\
        \\\\
        &b_{na},c_{na},d_{na},e_{na} \\in \\mathbb{R}
        \\end{aligned}
    \\end{align*}
\\]