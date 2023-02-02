# Changelog
## [Unreleased]
### Major Changes
* **2023-01-22 \#s109** `Perspective#toXmlStringWithPrefixes` now renders leaf nodes as normal strings,
  if these contain characters that are not allowed in XML element names.
  For example, the perspective `perspective("test", DEN).withChild(perspective("values.", DEN))` will now be rendered as `<d:test>values.</d:test>`,
  instead of `<d:test><d:val name="values."></d:val></d:test>`.
* **2022-12-02 \#27** When `IsEchoToFile` is set to true,
  `Console` does no longer cancel its output to the standard output.
  If `IsEchoToFile` is set to true, console output is written to a file and to the standard output.
* **2022-11-07**
   1. Remove unused methods `CommonFunctions#findSystemOutput`.
   2. Remove unused methods `CommonFunctions#currentTime`.
   3. Remove unused methods `CommonFunctions#disableSystemOutput`.
* **2022-04-03** **\#10**: Rename `Resource` interface to `ResourceOption` and it's `ResourceI`
     implementation to `ResourceOptionI` in order to clarify its meaning.
     This also makes it possible to use the name `Resource` for an interface
     of resource like things.
### Minor Changes
* **2023-02-02** **\#170**: Create container for arbitrary typed values with a type safe interface.
* **2022-10-10** **\#8**: Create interface Identifiable, in order to be able to correctly implement `Object#equals` in wrappers and aspects.
* **2022-10-10** **\#196**: Files created during execution are now stored at `~/.local/state/<ProgramName>` by default.
  The format there should abide by the [Software Project File System Standards](https://splitcells.net/net/splitcells/network/guidelines/filesystem.html)
* **2022-06-03** **\#8**: `MathUtils#sumsForTarget` now supports generating sums with a given exact number of components.
  If this number is 2 for instance, it only returns sums, that contain 2 numbers.
* **2022-04-18** **\#162**: There is now a default Comparator named `naturalComparator` provided for comparable objects in Dem.