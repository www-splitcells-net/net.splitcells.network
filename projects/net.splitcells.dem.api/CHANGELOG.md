----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
## [Unreleased]
### Major Changes
* **2024-12-24\#51** Rename `Assertions#assertThrows` to `Assertions#requireThrow`,
  in order to unify naming.
* **2024-10-01\#37**
    1. `Dem#executeThread(Runnable)` now also requires a name for the thread.
    2. The main Thread of `Dem#process(Runnable, Consumer)` now has a name.
      It consists of `Class#getPackageName() + "." + Class#getSimpleName()`.
* **2024-09-27** Rename `net.splitcells.dem.lang.perspective.Perspective*`
  to `net.splitcells.dem.lang.tree.Tree*` in order to make code compact and more readable.
  Also, rename `Domable#toPerspective()` to `Domable#toTree()`.
* **2024-09-23\#26** Remove deprecated `Domable#toDom`.
* **2024-09-01\#32** Rename `net.splitcells.dem.data.order.Comparator` to `Comparison`,
  in order to avoid confusion with `java.util.Comparator`.
* **2024-08-24\#37** Rename `ListView#assertEquals` to `ListView#requireEquals` in order
  to have a standard naming scheme for all assertion methods.
* **2024-05-09\#c11**
  1. Define `ConfigFileSystem`, that is used in order to store the program's file based configuration.
  2. Define `UserFileSystem`, that is used in order to store the user's data.
* **2024-04-28\#c11** Define `BootstrapFileSystem`, that is used in order to store the program's state.
* **2024-03-05 \#170** Deprecate `OptionI` in order to remove all abstraction classes in the future.
* **2023-10-25 \#242** `net.splitcells.dem.data.set.map.Map#put` now throws an exception,
    if the key is already present.
    This is done, because in this case, there is an increased chance, of a programming bug being present.
    If the value of the map should be updated regardless of the presence of the given key,
    the method `ensurePresence` should be used instead.
* **2023-07-13 \#249** Migrate Java's Path API usage to new FileSystem API,
  in order to support operation system unspecific deployment.
* **2023-06-06 \#248**
  1. Delete obsolete class `net.splitcells.dem.lang.perspective.PerspectiveXmlRenderer`,
     because it was not used.
  2. Delete obsolete classes `net.splitcells.dem.resource.communication.interaction.Dsui & DsuiTest`,
     because it was not used.
  3. Delete obsolete interface `net.splitcells.dem.resource.communication.interaction.Sui`,
     because it has no function anymore as the previous classes were removed.
  4. Move content of `net.splitcells.dem.resource.communication.interaction` to
     `net.splitcells.dem.resource.communication.log`,
     in order to simplify package structure.
* **2023-03-25** `Set#remove(Object)` will throw an exception,
  if the given Object is not present inside the set.
  In this case there is a higher probability, that the developer made a programming mistake,
  compared to the case, where the to be removed element is really present.
  If the developer really intended optionally remove an element of a Set,
  the method `Set#ensureRemoved(T)` is provided instead.
* **2023-03-17** Revers priorities of `LogLevel` so that `LogLevel.DEBUG.smallerThan(LogLevel.ERROR)` returns `true`. 
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
* **2024-11-19 \#51** Create wrapper for Java's `Collection#stream()`,
  in order to provide own additional methods fluently.
* **2024-07-28 \#26** `NameSpace#isXmlAttribute` now encodes, if values of that namespace should be rendered as XML attributes.
* **2024-04-26 \#c11**: Create `ServerLog`, that outputs log messages as JSON documents in a single line.
  This is suitable for log files on servers.
* **2023-1-13 \#252**: `CommonMarkDui` now provides a logger, that output CommonMark document.
* **2023-07-12 \#249** Implement FileSystemViaClassResources in order to provide FileSystem API for class resources.
  This can be used in order to provide file systems via jar files.
* **2023-07-12 \#249** Provide file system API in order to not to rely on Paths specific to the operation system.
* **2023-02-19** **\#170**: Log if most runtime performant settings are not active.
* **2023-02-19** **\#10**: Create DescribedBool for assertions with descriptions.
* **2023-02-02** **\#170**: Create container for arbitrary typed values with a type safe interface.
* **2022-10-10** **\#8**: Create interface Identifiable, in order to be able to correctly implement `Object#equals` in wrappers and aspects.
* **2022-10-10** **\#196**: Files created during execution are now stored at `~/.local/state/<ProgramName>` by default.
  The format there should abide by the [Software Project File System Standards](https://splitcells.net/net/splitcells/network/guidelines/filesystem.html)
* **2022-06-03** **\#8**: `MathUtils#sumsForTarget` now supports generating sums with a given exact number of components.
  If this number is 2 for instance, it only returns sums, that contain 2 numbers.
* **2022-04-18** **\#162**: There is now a default Comparator named `naturalComparator` provided for comparable objects in Dem.