# Implementing And Solving A N Queen Problem Instance
The following goes step-by-step through a minimal optimization implementation of
the N queen problem.
The project `net.splitcells.gel.quickstart` located in the main source code
repository contains a complete project implementing such a problem.
It can be used as a starting point for new projects,
because of its minimal content.
## Preparations
> Add dependency to your project in order to use the Generic Allocator. 

If you want to use the bleeding edge version of this project, you need
to [build it locally](../../../../../../../../../../CONTRIBUTING.md).
After this you can add the corresponding dependency to your project:
```
<dependency>
    <groupId>net.splitcells</groupId>
    <artifactId>sep</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
## Specifying The Problem
> At the start, we define the problem, that we want to optimize.

In the following we'll go through the implementation and solving of a
[N queen problem instance](n-queen-problem.md),
in order to get familiar with Gel's API.

The preferred starting point for the framework is the class
`net.splitcells.gel.Gel`.
It contains references to all entry points to all standardized workflows.
In this case we use the `defineProblem` function,
in order to implement our problem.
After that, we'll later apply some optimization functions,
in order to create a solution:
```
import static net.splitcells.gel.Gel.defineProblem;
[...]
final var problemBuilder = Gel.defineProblem();
```
### Creating A Data Model
> First, we define the values, we are working with.

First, the data of the problem needs to be defined,
otherwise there is nothing that can be optimized.
Every problem is modeled as a set of allocations between demands and supplies.
Every allocation pairs one demand and one supply.
The set of all allocations is the actual solution.

The demands and supplies are the data of the problem.
Both sets are modeled as tables with explicitly typed columns,
which later allows one to access the values of the table without a downcast
by the API caller.
Basically, one has to state the columns of each table first and then
assign the values to the respective table.

Each column of a table is represented by an Attribute,
that associates the name and the type of the corresponding column.
No two attributes are allowed to have the same name.

This does not only apply to the demand and supply tables on their own.
An Attribute's name needs to be unique across the demands and supplies.
The reason for this, is the fact,
that the set of all allocations is also modeled as a table and
represents the assigned values of the demand and supply set.
In other words, the allocations table works like a join of 2 tables in SQL.
The following code defines the data format,
but uses the not yet defined variables `demands` and `suipplies` for the
actual values for an overview with less clutter:
```
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
[...]
final var column = attribute(Integer.class, "column");
final var row = attribute(Integer.class, "row");

final var problemData = Gel.defineProblem()
    .withDemandAttributes(column)
    .withDemands(demands)
    .withSupplyAttributes(row)
    .withSupplies(supplies);
```
The type of both table values is the same and corresponds to a 2 dimensional
matrix of objects.

It is constructed via nesting of Lists (`net.splitcells.dem.data.set.Lists`).
In this case columns (demands) are allocated to rows (supplies).
Every allocation corresponds to the placing of one queen:
```
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static java.util.stream.IntStream.rangeClosed;
[...]
final int columns = 8;
final int rows = 8; 
final var demands = listWithValuesOf(
    rangeClosed(1, columns)
        .mapToObj(i -> list((Object) i))
        .collect(toList()));

final var supplies = listWithValuesOf(
    rangeClosed(1, rows)
        .mapToObj(i -> list((Object) i))
        .collect(toList()));
```
### Defining A Constraint Model
> Second, we state, what an optimal solution looks like.

Previously, we defined the data,
by specifying a demand and supply table.
Now we define the constraints of the problem.

The preferred way of doing this is by using the query interface.
In this case a function is used as an argument, that takes a builder object.
The builder represents the constraints of the problem.
By calling methods on the builder one adds constraints to the problem
definition.

This is called the query interface,
because function call chains can be used in order to define complex constraints.
Such complex constraints have a tendency to look like database queries.

The constraint definition ends, when the modified builder is returned.

Keep in mind, that the constraint defines rules,
that apply to the allocations table:
```
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.raterBasedOnLineValue;
[...]
private static Rater ascDiagonals(int rows, int columns) {
    return raterBasedOnLineValue("ascDiagonals", line -> line.value(ROW) - line.value(COLUMN));
}

/**
* The descending diagonal with the number 0 represents the diagonal in the middle.
*/
private static Rater descDiagonals(int rows, int columns) {
    return raterBasedOnLineValue("descDiagonals", line -> line.value(ROW) - Math.abs(line.value(COLUMN) - columns - 1));
}
[...]
final var solution = Gel.defineProblem()
    .withDemandAttributes(column)
    .withDemands(demands)
    .withSupplyAttributes(row)
    .withSupplies(supplies)
    .withConstraint(
        r -> {
            r.forAll(row).forAll(column).then(hasSize(1));
            r.forAll(row).then(hasSize(1));
            r.forAll(column).then(hasSize(1));
            r.forAll(ascDiagonals(rows, columns)).then(hasSize(1));
            r.forAll(descDiagonals(rows, columns)).then(hasSize(1));
            return r;
        })
    .toProblem()
    .asSolution();
```
When you are developing a new problem,
don't worry about getting lost in this workflow.
`Gel.defineProblem()` guides its users through the process,
by requiring a strict way of specifying the problem.
By using `Gel.defineProblem()` the developer can just ask the object,
what the next possible steps are in defining the problem.
Just create the initial builder via `Gel.defineProblem()` and look at what
methods are provided.

When calling a method of the builder it is important to just use the returned
object and to not reuse a returned object,
by storing it in a variable.
At each step, the builder will only suggest methods,
that are needed for the next step.

In the best case scenario, if you are using an IDE with autocompletion you can
just do the 'dot'-'choose'-'type' loop.
## Find A Solution
> Now, we do the actual optimization.
## Using A Simple Hill Climber As A Stepping Stone
> Let's try the most commonly known solver.

Applying solvers to the solutions is done by calling the `optimize` method.
The `functionalHillClimber` is an optimizer,
that needs an initialized solution,
where all demands or all supplies are allocated.
So we use `linearInitialization` to set all variables in the following example:
```
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
[...]
solution.optimize(linearInitialization());
solution.optimize(functionalHillClimber(1000));
solution.createStandardAnalysis();
```
These optimizers do not solve the problem in question,
so we can use `createStandardAnalysis` to create an analysis.
This creates reports explaining and visualizing the issues of the calculated
solution. 
These files are stored at `src/main/xml/net/splitcells/gel/GelEnv`.
It is a mix of XML, text and LibreOffice spreadsheets.
Try it out!
## Use backtracking in order to solve the Problem.
> Now we try a solver, that actually solves the problem.

One of the reasons, why the hill climber is not enough to create an optimal
solution,
is the fact,
that the hill climber is not able to get out of a local optima.
So, we need an optimizer that can reject its current solution,
even it is a local optima.

The widely known backtracking algorithm is such an optimizer,
that can get out of any local optima,
so let's try it out!
```
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static org.assertj.core.api.Assertions.assertThat;
[...]
backtracking().optimize(solution);
assertThat(solution.isOptimal()).isTrue();
```
Run it and check the results with the standard analysis!
# Complete Source Code
> So, what are the commands required in order to model and solve the problem?

So in essence the complete source code of solving the source code looks like
this:
```
/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.quickstart;

import net.splitcells.gel.Gel;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.Rater;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.raterBasedOnLineValue;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.LinearDeinitializer.linearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>This class solves the widely known 8 queen problem with the commonly known
 * backtracking algorithm.
 * It provides for newcomers an intro to the API of the framework,
 * where they can focus on the API instead of the problem.
 * By implementing something familiar,
 * it should be easier for the beginner to understand the API.</p>
 * <p>This class also show the bare minimum,
 * that should be done, in order to use this project.</p>
 */
public class NQueenProblemTest {
    public static final Attribute<Integer> COLUMN = attribute(Integer.class, "column");
    public static final Attribute<Integer> ROW = attribute(Integer.class, "row");

    @Test
    public void test() {
        final var testSubject = nQueenProblem(8, 8).asSolution();
        testSubject.optimize(linearInitialization());
        testSubject.optimizeOnce(functionalHillClimber(100));
        testSubject.createAnalysis(Path.of("./target/analysis-hill-climber/"));
        testSubject.optimize(linearDeinitializer());
        backtracking().optimize(testSubject);
        assertThat(testSubject.isOptimal()).isTrue();
        testSubject.createAnalysis(Path.of("./target/analysis-backtracking/"));

    }

    private static Problem nQueenProblem(int rows, int columns) {
        final var demands = listWithValuesOf(
                rangeClosed(1, columns)
                        .mapToObj(i -> list((Object) i))
                        .collect(toList()));
        final var supplies = listWithValuesOf(
                rangeClosed(1, rows)
                        .mapToObj(i -> list((Object) i))
                        .collect(toList()));
        return Gel.defineProblem()
                .withDemandAttributes(COLUMN)
                .withDemands(demands)
                .withSupplyAttributes(ROW)
                .withSupplies(supplies)
                .withConstraint(
                        r -> {
                            r.forAll(ROW).forAll(COLUMN).then(hasSize(1));
                            r.forAll(ROW).then(hasSize(1));
                            r.forAll(COLUMN).then(hasSize(1));
                            r.forAll(ascDiagonals(rows, columns)).then(hasSize(1));
                            r.forAll(descDiagonals(rows, columns)).then(hasSize(1));
                            return r;
                        })
                .toProblem();
    }

    /**
     * The ascending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater ascDiagonals(int rows, int columns) {
        return raterBasedOnLineValue("ascDiagonals", line -> line.value(ROW) - line.value(COLUMN));
    }

    /**
     * The descending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater descDiagonals(int rows, int columns) {
        return raterBasedOnLineValue("descDiagonals", line -> line.value(ROW) - Math.abs(line.value(COLUMN) - columns - 1));
    }
}

```