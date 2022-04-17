# Changelog
## [Unreleased]
### Major Changes
* **2022-04-03** **\#10**:
  1. Rename `Resource` interface to `ResourceOption` and it's `ResourceI`
     implementation to `ResourceOptionI` in order to clarify its meaning.
     This also makes it possible to use the name `Resource` for an interface
     of resource like things.
### Minor Changes
* **2022-04-18** **\#162**: There is now a default Comparator named `naturalComparator` provided for comparable objects in Dem.