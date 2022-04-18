# Changelog
## [Unreleased]
### Major Changes
* **2022-04-19** **\#170**: Create method `complexAdditions` for `RatingEvent` in order to prepare assigning multiple groups to one line.
* **2022-04-04** Remove no args method `Query.then`,
  as it does not seem to make sense.
* **2022-03-31** **\#8**:
  Rename optimizer components,
  so it will be easy to distinguish the offline repair from the planned online repair:
  1. Rename `ConstraintGroupBasedRepair` to `ConstraintGroupBasedOfflineRepair`.
  2. Rename `SupplySelector` to `SupplyOfflineSelector`.
  3. Rename `SupplySelectors` to `SupplyOfflineSelectors`.
  
