----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Solution
## Optimality
A solution is optimal, if there is no other solution with a better rating.

\\[
\\begin{aligned}
    &optimal(X) =
    \\begin{cases}
        \\;\\;wahr, & \text{if} X = (x_{11},...,x_{DS})\\\\
        &best \\ rating(X)\\\\
        \\;\\;false, & \\text{otherwise}.
        \\end{cases}
    \\end{aligned}
\\]
