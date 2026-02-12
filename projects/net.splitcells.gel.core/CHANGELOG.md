----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
## [Unreleased]
### Major Changes
* **2025-11-08 \#37** Require `Rater#rating_before_removal` to be implemented.
* **2024-12-04 \#55**
  1. Rename `LookupComponents#lookup` to `LookupComponents#persistedLookup`.
  2. Define new `LookupComponents#lookup` methods, that returns a lookup,
    that may be persisted or not.
  3. Delete LookupFactory and LookupIFactory, as it was not needed.
  4. Rename Lookup related implementations to PersistedLookup implementations.
* **2024-10-28 \#51** Make Table naming more logical and consistent with public communities like SQL.
  1. Rename Table to View in order to avoid confusion.
  2. Rename Database to Table in order to avoid confusion.
* **2024-09-20 \#26** Migrate `Table#toFods`, `SolutionView#toFodsTableAnalysis` and `History#toAnalysisFods` from XML to Perspective.
* **2024-07-25 \#24** Rename `Query#constraint` to `Query#parseConstraint` in order to mark the intent better.  
* **2024-04-01 #170** Deprecate ResourceOptionI.
  Implement the ResourceOption API directly instead,
  by implementing the `Option#defaulValue()` instead of implementing this via the default constructor.
* **2023-06-27 #170** `Line#values()` are now read only, in order to allow more performant code,
  by not requiring lines to provide a new copy of line values for every `Line#values()` call. 
* **2023-06-21 #170** `Table#columnsView` now only returns a read-only access to the columns,
  in order to enable faster alternative Database implementations,
  that do not have writeable columns.
  Also, writeable access to the columns via the public interface was never planned.
  Especially, in the Table interface, that is thought to be a read-only interface.
* **2023-06-15 #170**
  1. Rename `net.splitcells.gel.data.allocation.Allocation*`
    to `net.splitcells.gel.data.assignment.Assignment*`,
    which is more in line with the name of the core model problem the assignment problem.
  2. Provide new Allocations interface, that is the foundation of the Assignments interface.
    The key difference is, that the Allocations only allows one assignment for each demand and
    for each supply.
    This interface can be used instead, in order to improve the performance,
    if an appropriate usage is possible.
* **2023-06-08 #170** `Table#columnsView` now returns a view of the list,
  instead of a writeable copy,
  in order to improve the runtime performance.
* **2023-04-12 #170** Rename `Table#line(int)` to `Table#orderedLine(int)` in order to avoid misunderstandings by avoiding ambiguity.
* **2023-04-07**
  1. Rename `Table#lines` to `Table#unorderedLines` in order to avoid programmer errors.
  2. Rename `Table#linesStream` to `Table#unorderedLinesStream` in order to avoid programmer errors.
  3. Rename `LinearInitialization` to `OfflineLinearInitialization` in order to discourage its usage.
* **2023-03-30 \#170** Require raters to implement toSimpleDescription in order always have a working report,
  for a given optimization problem.
* **2023-03-25 ** Deprecate `Databases` without a name,
  because such `Database` instances make it harder to understand these compared to a named `Database`.
* **2023-03-07 ##170**: New `GroupId` now require an parent `GroupId`,
  if the new `GroupId` is explicitly stated to be a root `GroupId`.
  This makes it easier to get the meaning of a `GroupId` during debugging,
  if Constraints with overlapping `GroupId` are present.
* **2022-11-27 ##8**: `OnlineLinearInitialization` now ensures,
  demands and supplies are allocated in the same order,
  as it is present in their respective tables. 
* **2022-11-20 \#170**: Constraint nodes (i.e. `ForAll` and `Then`) now internally require an optional parent discoverability path.
  This is the basis required, so that constraints can be viewed via the web server.
  The end goal is to provide for each constraint a path,
  that starts with the problem's path and after that continues with the constraint node's path.
  For example: `/net/splitcells/run/conway-s-game-of-life/forAll/timeSteps` points to the constraint node
  of an instance of conway's game of life, that groups all values according to their time value.
  In other words, the aim is to provide a unique id for all constraint nodes, that can be easily discovered.
* **2022-09-06 \#8**: Disable history of solutions by default.
* **2022-08-24 \#8**: Unify Table methods naming:
  * Rename `Table#getLines` to `Table#lines`.
  * Rename `Table#getDistinctLines` to `Table#distinctLines`.
  * Rename `Table#getDistinctLineValues` to `Table#distinctLineValues`.
  * Rename `Table#getLines(int)` to `Table#line(int)`.
  * Rename `Table#getLines(int)` to `Table#line(int)`.
* **2022-07-15 \#8**: Rename `RoutingRating#events` to `RoutingRating#ratingComponents`, in order to communicate meaning a bit better.
* **2022-07-01 \#8**: Create `Query#constraintPath` in order to select a constraint path of a solution via the query interface,
  which is a lot easier to understand,
  than selecting paths by recursive calls of `Constraint#childrenView`.
* **2022-06-30** **\#8**:
  1. Remove `Constraint#parentOf`, because its definition does not seem to be useful and this method is not used.
  2. Rename `Query#constraint` to `Query#currentConstraint` in order to clarify its meaning.
* **2022-05-23** **\#170**: `SolutionBuilder` now requires to always define the demand and solution set explicitly.
  The helper methods `withNoDemands` and `withNoSupplies` are created for that, in order to state intent.
  Also, setting the supply or demand set multiple times during solution building is not allowed anymore, in order to simplify the building workflow.
* **2022-04-19** **\#170**: Create method `complexAdditions` for `RatingEvent` in order to prepare assigning multiple groups to one line.
* **2022-04-04** Remove no args method `Query.then`,
  as it does not seem to make sense.
* **2022-03-31** **\#8**:
  Rename optimizer components,
  so it will be easy to distinguish the offline repair from the planned online repair:
  1. Rename `ConstraintGroupBasedRepair` to `ConstraintGroupBasedOfflineRepair`.
  2. Rename `SupplySelector` to `SupplyOfflineSelector`.
  3. Rename `SupplySelectors` to `SupplyOfflineSelectors`.
### Minor Changes
* **2026-02-12 \#37** Tables can now be checked, if these are Solution instances instead via `Table#lookupAsSolution`.
* **2025-11-20 \#37**
  1. Create HasMaximalSize rater.
  2. Create `Rater#requireVerySimilar` helper function, in order to better compare Costs' double values.
  3. Create UniqueValueCountIsAtMost rater.
* **2025-11-08 \#37** Create new Not Rater, that inverses a given Rater.
* **2024-11-15 \#51**
    1. Define rating method after line removal instead of before for raters.
      This makes it possible to create simplified rater implementation.
    2. Deprecate RaterBasedOnLineGroup, as it is too complicated.
      Create LineGroupRater as an alternative. 
* **2024-11-13 \#51** Define description string API of ratings for users. 
* **2024-07-24 \#24** Constraint queries can now be used to check for the existence of constraint nodes without creating new constraints.
* **2023-12-22 ##252**: `DefaultOptimization` now provides the recommended optimizer for problems in general.
* **2023-06-14 ##170**: Add `DatabaseI#addWithSameHeaderPrefix` as a faster alternative to `DatabaseI#add`,
  if the line's header to be added to the database is a prefix to the database's header.
* **2023-06-11 ##170**: Introduce IndexedAttribute in order to access values of Lines faster.
* **2023-04-13 ##170**: Extend Query with optional value containing the Allocations the Query is being applied to.
  This allows one to create constraints,
  that depend on the supply and demand Tables of assignments of the given problem.
* **2023-03-17 ##170**: Introduce `HistoryForDatabase` for debugging purposes.
  An instance can be connected to a given database via the `DatabaseFactory`,
  which can be viewed via the webserver in order to understand how the content of the given database was created.
```
GelDev.process(() -> {
   [Actual Code]
}, env -> env.config().configValue(Databases.class).withConnector(d -> {
   if (d.path().equals(list("conway-s-game-of-life", "Propagation", "ForAll", "6", "Propagation", "ForAll", "0", "Propagation", "ForAll", "isDead", "ForAll", ".lines", "assignments/linesProcessing", "linesProcessing", "demands-free"))) {
      historyForDatabase(d);
   }
}));
```
* **2023-02-02** **\#170**: Add meta data container to GroupId.
* **2022-07-24** **\#8**:
  1. Deprecate `LinearInitialization` and prefer using `OnlineLinearInitialization` for better performance.
  2. Solution optimization now supports an `OptimizationConfig`,
     which simplifies future backward-compatible changes to the Solution interface.
* **2022-06-03** **\#8**: The history interface for assignments defines now a `toAnalysisFods` method,
  that creates an alternative more searchable version of `Table#toFods`.
  The new version has dedicated columns for fields of complex attributes.
  This makes it easier to search for events in the optimization history by i.e. demand or supply attributes.
