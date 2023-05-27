----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Multidimensional Assignment Problems
In multidimensional assignment problems multiple supply sets are present
and each demand is allocated to at least one supply in each set.
This primarily allows one to reduce the number of available supplies
and enables the optimizer to omit assigning values to a demand,
that it does not need to.
## One-dimensional Assignment Problem Chaining
One-dimensional assignment problems can be chained in order to get
multidimensional ones.
Thereby, the assignments of the problem \\(N\\) is used as the demand set for
the problem \\(N+1\\).
For the first assignment problem in the chain,
there are no special requirements.