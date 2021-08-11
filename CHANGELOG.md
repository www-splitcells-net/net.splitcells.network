# Changelog
The changelog format can be found [here](./src/main/md/net/splitcells/network/guidelines/changelog.md).
The tickets can be found [here](./src/main/md/net/splitcells/network/tickets/).
Releases are done everytime a relevant ticket is completed.
## [Unreleased]
### Major Changes
1. **2021-08-10**:
   1. `command.repositories.install` now only install sh, Bash and
      Python commands.
   2. Create command `project.repository.register` in order to omit editing
      config files manually.
### Minor Changes
1. **2021-08-11**:
   1. Server SVG images from source folder by webserver and add them to the layout.
   2. Deprecated untyped repositories in webserver.
   3. Render LaTex math formulas via MathJax in website.
   4. Translate local image links in CommonMark to the link of the website.
2. **2021-08-09**: Create `net.splitcells.network.deploy.build` for deploying
   the build process on multiple computers.
3. **2021-08-08**: `command.repositories.install` now strips known file
   suffixes during installation.
   This way `os.state.interface` repos can now state their file endings and
   thereby have better IDE support in such repos.
#### Patches
1. **2021-08-05**:
   1. Fix Dependabot alert.
   1. Rename `net.splitcells.dem.utils.CommonFunctions#hash_code` to `hashCode`.
   1. Make `hashCode` and `equals` of `net.splitcells.gel.rating.framework.MetaRatingI`
      consistent.
   1. Make `hashCode` of `HasMinimalSize`, `HasSize`, `MinimalDistance`,
      `MinimalDistanceBasedOnDiffs`, `Compliance`, `Cost`, `BoolI`,
      `Comparators` more consistent to `equals`, in order to improve Code
      quality score in [lgtm.com](https://lgtm.com).
## [3.0.0] - 2021-08-05
### Major Changes
1. **2021-07-08**:
   1. Default configuration of Dem's process does not ensure determinism
      or randomness.
1. **2021-07-04**:
   1. \#61 Remove XSL parameter from rendering API in webserver.
1. **2021-07-03**:
   1. Port 'repo.synchronize.with' to Python, add standardized argument handling
      and use better sub commands for each synchronization step.
      Also adds a logging message in case of error.
1. **2021-07-01**:
   1. Remove experimental web layouts.
1. **2021-06-25**:
   1. \#8 Remove deprecated constructor ProjectRenderer.
   1. \#8 Provide path `/net/splitcells/website/layout/build` in server,
      in order to refresh project layout.
   1. \#8 Clean up error logging in HttpServer.
   1. \#8 Add description to RaterBasedOnLineValue based on classifier.
   1. Remove verification step after repair inside `repo.repair` in order to
      prevent duplicate verification calls.
   1. Install project commands in Ossi from repos listed in `~/.config/net.splitcells.os.state.interface/project.repositories`.
      In other words: `command.repositories.install` and `command.managed.install.project.commands`
      now each have their own repo list.
      The reason for this is the fact, that repos for projects cannot be installed like repos for commands in OSSI.
      The reason for this, is the fact, that files located in the src folder of the project repo,
      may not work as an independent command.
   1. Use `~/.config/net.splitcells.os.state.interface/command.repositories` for `command.repositories.install`.
      This way it is easier to understand the Ossi config files.
1. **2021-06-24**:
   1. \#8 Rename ForallI to ForAll, in order to simplify name.
   1. \#8 Create valid XML for natural argumentation.
   1. Use established px default value for default font-size in default web layout.
   1. Rename `ConstraintAI#process_lines_before_removal` to `processLinesBeforeRemoval` in order to unify naming.
   1. Rename `ConstraintAI#register_before_removal` to `registerBeforeRemoval` in order to unify naming.
   1. Rename `ConstraintAI#register_additions` to `registerAdditions` in order to unify naming.
   1. Rename `ConstraintAI#process_line_addition` to `processLineAddition` in order to unify naming.
   1. Prevent random test error in `SimplifiedAnnealingProblemTest#testProbability`.
   1. Adjust default value of ProcessPath, so it does not clutter project repo,
      if the program is executed with default settings inside IDE.
1. **2021-06-23**: #8 Remove deprecated usage of `net.splitcells.gel.data.database.DatabaseI` and mark these as protected.
   These constructors will be made private in the future.
1. **2021-06-21**: Remove `net.splitcells.gel.problem.Problem#toSolution()`,
   because it is not used and was not implemented yet.
1. **2021-04-13**: #7: Model and solve oral exam problem.
1. **2021-04-07**: #42: Make Gel workspace visible in website.
1. **2021-04-12**: #46: Use deterministic environment and execution in CI.
### Minor Changes
1. **2021-06-27**:
   1. Publish Ses project.
   1. Limit depth of `Local Path Context` in default web layout.
   1. Render `Local Path Context` for CommonMark documents.
1. **2021-06-25**:
   1. \#8 Create index file, for standard analysis.
1. \#8 Create RegulatedLength AllSame.
1. \#8 Create RegulatedLength Rater.
1. **2021-06-19**: Create command convention `repo.is.clean`.
   Exits with 0, if this repo can be synchronized and else exits 1.
   This can be used as a safeguard for automated synchronization command
   by exiting a command, when uncommitted changes are present.
1. **2021-06-06**: [#54: Integrate CommonMark documentation into project rendering.](https://github.com/www-splitcells-net/net.splitcells.network/issues/54)
1. Some undocumented work.
1. **2020-12-31**: Create public git repository.
1. Some undocumented work.
1. **2019-05-05**: Create private git repositories.
1. Some undocumented work.
#### Patches
1. **2021-06-27** Log error stack traces in web server.
1. **2021-06-26** Add optimization to `den.xsd`.
1. **2021-06-25** Determine project's source folder correctly in ProjectRenderer.
## [2.0.0]
Second version created during master thesis.
## [1.0.0]
First version created during practical course in university study. 
