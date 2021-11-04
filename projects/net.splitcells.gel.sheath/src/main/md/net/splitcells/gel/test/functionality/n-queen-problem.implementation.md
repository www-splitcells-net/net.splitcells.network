# Implementing And Solving A N Queen Problem Instance
## Preparations
If you want to use the bleeding edge version of this project, you need
to [install it locally](../../../../../../../../../../CONTRIBUTING.md).
After this you can add the corresponding dependency to your project:
```
<dependency>
    <groupId>net.splitcells</groupId>
    <artifactId>sep</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
## Specifying The Problem
In the following we'll go through the implementation and solving of a
[N queen problem instance](n-queen-problem.md),
in order to get familiar with Gel's API.

The preferred starting point for the framework is the class
`net.splitcells.gel.Gel`.
It contains references to all entry points to all standardized workflows.
In this case we use the `defineProblem` function,
in order to implement our problem.
After that, we'll apply some optimization functions,
in order to create a solution:
```
import static net.splitcells.gel.Gel.defineProblem;

final var problemBuilder = Gel.defineProblem();
```
### Creating A Data Model
First, the data of the problem needs to be defined,
otherwise there is nothing that can be optimized.
Every problem is modeled as allocations of demands to supplies.
Every allocation pairs one demand and one supply.
The set of all allocations is the actual solution.

The demands and supplies are the data of the problem.
Both sets are modeled as tables with explicitly typed columns,
which later allows one to access the values of the table without downcasting
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
actual values for a better focus:
```
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
Previously, we defined the data,
by specifying a demand and supply table.
Now we define the constraints of the problem.

The preferred way of doing this is by using the query interface.
In this case a function is used, that takes a builder object,
that represents the constraints of the problem.
By calling methods on the builder one adds constraints to the problem.
The constraint definition ends, when the modified builder is returned.

Keep in mind, that the constraint defines rules,
that apply to allocations table:
```
final var solution = Gel.defineProblem()
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
## Using A Simple Hill Climber As A Stepping Stone
Applying solvers to the solutions is done by calling the `optimize` method.
The `functionalHillClimber` needs a solution,
where all variables are set,
so `linearInitialization` is used set all variables in the following example:
```
solution.optimize(linearInitialization());
solution.optimize(functionalHillClimber(1000));
solution.createStandardAnalysis();
```
These optimizers do not solve the problem in question,
so we can use `createStandardAnalysis` to create an analysis.
This creates reports explaining and visualizing the issues of the solutions. 
These files are stored at `src/main/xml/net/splitcells/gel/GelEnv`.
It is a mix of XML, text and LibreOffice spreadsheets.
Try it out!