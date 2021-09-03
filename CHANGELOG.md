# Changelog
The changelog format can be found [here](./src/main/md/net/splitcells/network/guidelines/changelog.md).
Tickets that create lasting requirements can be found [here](./src/main/md/net/splitcells/network/tickets/).

Tickets are referenced with a hashtag and prefix in order to indicate the
server, that hosts the ticket:
for example `#g1` stands for the first Github issue and `#s1` for the first
sourcehut issue.
Ticket references without a prefix are probably hosted on
[Github](https://github.com/www-splitcells-net/net.splitcells.network/issues).

Prefix to Server:
* g = Github: https://github.com/www-splitcells-net/net.splitcells.network/issues
* s = sourcehut: https://todo.sr.ht/~splitcells-net/net.splitcells.network

Ticket information located on platforms and not in the repository are
considered throw away information.
Links to such platforms may or may not be valid.

Releases are done everytime an important ticket is completed.
## [Unreleased]
### Major Changes
* **2021-08-29**:
   1. **\#79**: `net.splitcells.website.server` now renders text files with the file
      suffix `.html` instead of `.txt`.
      For the end user it means, that links to these file needs to be adjusted.
      This needs to be done, because static file webservers need to know
      the file type. Otherwise, text files transformed to HTML are not rendered
      correctly on web browsers.
      In practice this is used in order to serve the license
      [files](src/main/txt/net/splitcells/network/legal) to `splitcells.net`.
      See this [example](http://splitcells.net/net/splitcells/network/legal/licenses/EPL-2.0.html).
* **2021-08-14**:
   1. `user.bin.configure` now does not require additional user specific
      implementation.
      The command now works out of the box without an error message.
* **2021-08-10**:
   1. `command.repositories.install` now only installs sh, Bash and
      Python commands.
* **2021-08-05**:
   1. Rename `net.splitcells.dem.utils.CommonFunctions#hash_code` to `hashCode`.
### Minor Changes
* **2021-09-04**:
   1. Create command `user.ssh.key.login.allow` in order to allow login via
      public key. This command mainly grants read access
      to `~/.ssh/authorized_keys` for the ssh server.
   2. Create command `user.ssh.key.authorize` in order to import public keys
      into `~/.ssh/authorized_keys` via command.
      Already present entries are not inserted.
* **2021-08-29**:
   1. Create method `Paths.removeFileSuffix` in order to unify file suffix
      manipulation implementations.
   2. **\#79**: [Render new license info on website](https://github.com/www-splitcells-net/net.splitcells.network/issues/79).
   3. **\#s2**: [Create Host independent ticket numbering.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/2)
      Numbering is done via a prefix, that indicates the server hosting the
      ticket.
      This is documented in the changelog description located at the start.
* **2021-08-28**:
   1. Create helper command `wait.interactively.until` in order to wait until
      condition is fulfilled.
* **2021-08-24**:
   1. Add secondary arguments to `maven.execute` command.
   2. Add `ENFORCING_UNIT_CONSISTENCY` environment property to Dem,
      so it can be set during program startup without programming.
   3. Create project command `execute.example` for `gel.sheath` in order to make
      easy to create custom execution command.
* **2021-08-11**:
   1. Server SVG images from source folder by webserver and add them to the layout.
   2. Deprecated untyped repositories in webserver.
   3. Render LaTex math formulas via MathJax in website.
   4. Translate local image links in CommonMark to the link of the website.
* **2021-08-10**:
   1. Create command `project.repository.register` in order to omit editing
      config files manually.
* **2021-08-09**: Create `net.splitcells.network.deploy.build` for deploying
   the build process on multiple computers.
* **2021-08-08**: `command.repositories.install` now strips known file
   suffixes during installation.
   This way `os.state.interface` repos can now state their file endings and
   thereby have better IDE support in such repos.
### Patches
* **2021-08-28**:
   1. Fix licensing issues found via [FOSSA](https://app.fossa.com).
* **2021-08-05**:
   1. Fix Dependabot alert.
   1. Make `hashCode` and `equals` of `net.splitcells.gel.rating.framework.MetaRatingI`
      consistent.
   1. Make `hashCode` of `HasMinimalSize`, `HasSize`, `MinimalDistance`,
      `MinimalDistanceBasedOnDiffs`, `Compliance`, `Cost`, `BoolI`,
      `Comparators` more consistent to `equals`, in order to improve Code
      quality score in [lgtm.com](https://lgtm.com).
## [3.0.0] - 2021-08-05
Starting from this point a deprecated changelog format is used.
### Major Changes
1. **2021-07-08**:
   1. Default configuration of Dem's process does not ensure determinism
      or randomness.
2. **2021-07-04**:
   1. \#61 Remove XSL parameter from rendering API in webserver.
3. **2021-07-03**:
   1. Port 'repo.synchronize.with' to Python, add standardized argument handling
      and use better sub commands for each synchronization step.
      Also adds a logging message in case of error.
4. **2021-07-01**:
   1. Remove experimental web layouts.
5. **2021-06-25**:
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
6. **2021-06-24**:
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
7. **2021-06-23**: #8 Remove deprecated usage of `net.splitcells.gel.data.database.DatabaseI` and mark these as protected.
   These constructors will be made private in the future.
8. **2021-06-21**: Remove `net.splitcells.gel.problem.Problem#toSolution()`,
   because it is not used and was not implemented yet.
9. **2021-04-13**: #7: Model and solve oral exam problem.
10. **2021-04-07**: #42: Make Gel workspace visible in website.
11. **2021-04-12**: #46: Use deterministic environment and execution in CI.
12. Some undocumented work.
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
2. **2021-06-26** Add optimization to `den.xsd`.
3. **2021-06-25** Determine project's source folder correctly in ProjectRenderer.
4. Some undocumented work.
## [2.0.0]
Second version created during master thesis.
## [1.0.0]
First version created during practical course in university study. 
