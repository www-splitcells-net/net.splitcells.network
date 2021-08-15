# Quadratic Assignment Problem (QAP)
All solutions are valid
The rating function has quadratic terms,
that models the rating for the presence of 2 specific assignments
in the assignment set.
Therefore, the rating of one assignment depends on the presence of
an other assignment in the solution
[[DISKI 86](../../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/1995.DISKI.86.md#page-26)].

\\[
\\begin{align*}
\\begin{aligned}
&\\min (\\sum\\limits_i^N\\sum\\limits_j^A\\sum\\limits_k^N\\sum\\limits_l^A x_{ij} \\times x_{kl} \\times c_{ijkl}) + (\\sum\\limits_{i,j}^{N,A} x_{i,j} \\times b_{i,j})\\\\
&c_{ijkl}, b_{ij} \\in \\mathbb{R}
\\end{aligned}
\\end{align*}
\\]

QAPs are \\(NP\\) complete and even an \\(\epsilon\\)-approximation
[[10.1145/321958.321975](../../../../../../../../../../../src/main/md/net/splitcells/gel/problem/theory/assignment/problem/bibliography/10.1145.321958.321975.md#pages-555-565)].