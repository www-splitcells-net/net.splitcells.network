# Changelog
## [Unreleased]
### Major Changes
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
* **2022-07-24** **\#8**:
  1. Deprecate `LinearInitialization` and prefer using `OnlineLinearInitialization` for better performance.
  2. Solution optimization now supports an `OptimizationConfig`,
     which simplifies future backward-compatible changes to the Solution interface.
* **2022-06-03** **\#8**: The history interface for allocations defines now a `toAnalysisFods` method,
  that creates an alternative more searchable version of `Table#toFods`.
  The new version has dedicated columns for fields of complex attributes.
  This makes it easier to search for events in the optimization history by i.e. demand or supply attributes.
