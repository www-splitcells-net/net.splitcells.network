----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
* The changelog format can be found [here](./src/main/md/net/splitcells/network/guidelines/changelog.md).
* Tickets that create lasting requirements can be found [here](./src/main/md/net/splitcells/network/tickets/).
* The ticket reference scheme can be looked up [here](./src/main/md/net/splitcells/network/tickets/index.md).
* Releases are done every time an important ticket is completed.
## [Unreleased]
### Major Changes
* **2025-06-05 \#49** Reimplement `bin/worker.execute` as `bin/worker.execute.py`, in order to:
    1. Add automatic tests.
    2. Deploy remotely without extra scripts via just `bin/worker.execute` (for command dispatch) and the `bin/bootstrap.*` (initial build commands).
    3. Deploy persistent user systemd service remotely without extra scripts.
    4. Simplify code by generating bash scripts.
    5. Find out, that a Java implementation of `bin/worker.execute`,
      currently would not be a net benefit by remove Python 3 from the software stack.
* **2024-03-26 \#252** [Provide scheduling tool for schools in general.](https://splitcells.net/net/splitcells/network/community/features/done/2023-10-02-provide-scheduling-tool-for-schools-in-general.html)
* **2023-09-15** Migrate articles of `net.splitcells.network.blog` to `net.splitcells.network.community` and
  delete the project `net.splitcells.network.blog`,
  because the blog provides insight into
* **2023-09-15 \#281** Rename `net.splitcells.network.worker` to `net.splitcells.network.worker.via.java`.
  It is required for a new `net.splitcells.network.worker` project.
  This new project will integrate different implementations of the network worker and provide a common interface for that.
  Different implementations are required in order to support worker tasks for different environments.
  For instance cloning and updating all repos in different contexts.
  A build server needs to be able to update its repos via native programs for bootstrapping the build,
  but requires integration code for the used operation system.
  A Java based server instance running with a copy of the repos can updates its repos via Java code,
  without requiring any additional native programs.
  The Java based server is more portable as the build server with the bootstrapping build.
* **2023-09-15 \#281** [Create dedicated project for a project files standard](https://github.com/www-splitcells-net/net.splitcells.network/issues/281):
  1. This project moves all such files to one dedicated place and
      thereby creates a standard, that is easier to understand, support and check.
  2. Some files were moved from the root project and `projects/net.splitcells.os.state.interface` to
     `projects/net.splitcells.project.standard`.
* **2023-09-15 \#256** [Stop active support for offline builds.](https://github.com/www-splitcells-net/net.splitcells.network/issues/256)
* **2023-08-17 \#249** Provide default value for `net.splitcells.website.server.Config#detailedXslMenu`.
* **2023-04-14 \#241** [Adjust projects to the project partitioning guidelines](https://github.com/www-splitcells-net/net.splitcells.network/issues/241):
  1. Rename `net.splitcells.dem.core` to `net.splitcells.dem.ext`.
  2. Rename `net.splitcells.dem.merger` to `net.splitcells.dem.api`.
  3. Rename `net.splitcells.gel.sheath` to `net.splitcells.gel.ext`.
* **2023-02-12 \#218** [Relicense main repo to EPL-2.0 with GPL-2.0-or-later as a secondary license.](https://github.com/www-splitcells-net/net.splitcells.network/issues/218)
  1. This was done, because `EPL-2.0 OR MIT` was planned as an transitioning licensing,
     until `EPL 2.0` with `GPL-2.0-or-later` as the secondary license (`EPL-2.0 OR GPL-2.0-or-later`) could be adopted.
     Before this change it was assumed, that `EPL-2.0 OR GPL-2.0-or-later` requires dependencies to be
     compliant with `GPL-2.0-or-later` at all times.
     It was later found out, that `GPL-2.0-or-later` compliance seems to be only relevant,
     when work is actively made available under the secondary license `GPL-2.0-or-later` and
     this only has to be done by the distributor.
  2. Create [licensing guidelines](https://splitcells.net/net/splitcells/network/guidelines/licensing.html).
* **2022-11-27 \#183**: The property `test.groups` is now used in Maven to determine,
  which Tags are required for a test to be executed,
  during the build.
* **2022-11-12** Allow leading digits in file and folder names in filesystem guidelines.
* **2022-10-07** **\#162**: [There are now 3 recommended types of test](https://splitcells.net/net/splitcells/network/guidelines/test.html): minimal, extensive and side effect tests
* **2022-02-28** **\#125**:
  1. Rename `*Renderer*` of `net.splitcells.website.server.project.renderer.**`
     to `*ProjectRendererExtension*` in order to ensure unique class names in the
     future.
  2. Improve package structure by moving `net.splitcells.website.server.project.renderer.*`
     to `net.splitcells.website.server.project.renderer.extension.*`.
* **2022-02-23** **\#s69**: Rename Renderer class names in order to unify
  naming:
  1. `CommonMarkChangelogExtension` -> `CommonMarkChangelogRenderer`
  2. `CommonMarkReadmeExtension` -> `CommonMarkReadmeRenderer`
* **2022-02-22** **\#s72**:
  1. Remove unused class `net.splitcells.website.server.project.Location`.
  2. Define config class, in order to pass additional or
     configuration info to renderers and server.
  3. Add config parameter to `ProjectRenderer#renderHtmlBodyContent`,
     `Renderer#renderFile` and `Server`.
  4. Move validators of `net.splitcells.website.server.*`
     to `net.splitcells.website.server.project.validator`,
     in order to improve code structure overview.
  5. Use `/net/splitcells/website/server/config/layout.xml` inside XSL scripts
     for the file layout file.
     This way, multiple XSL scripts for different webserver configurations
     are not needed.
     The layout file is also not stored in the file system anymore
     and is queried from the new Config class.
     Thereby the number of different locations for the webserver is reduced.
* **2022-02-21** **\#142** Remove `net.splitcells.website.html.content`
  project.
  It was an alternative minimal style for the website.
  It is now integrated into the default style and can be enabled via the
  `generated.style` option and the value `minimal`.
  For this the `RenderingConfig` class was created and is used by `ProjectsRenderer`.
  The standard styling is too complex.
  Using one unified style with switches makes it easy to recognize the complex
  parts and to migrate these to simpler forms.
  Furthermore, maintaining 2 styles that are mostly identical is costly.
* **2022-02-20**: Move `package.install` implementation for rpm-ostree from OS
  state interface lib,
  to the OS state interface lib GPL 2.0,
  because rpm-ostree contains GPL 2.0 code.
* **2022-02-17**: **\#8**: Make argumentation rendering in Gel less verbose for
  optimization solutions.
  This makes it easier to understand errors in solutions by omitting the
  excessive usage of the word `argumentation`.
  1. Every `Constraint#naturalArgumentation` method now returns an optional
     perspective.
     Thereby, the number of nodes in the AST of the argumentation is reduced.
  2. The usage of the word `argumentation` itself was greatly reduced.
     Before the change an argumentation would look like the following:
     ```text
     Argumentation For all rail
     Argumentation Argumentation Then values of allocated hours should have the same value
     ```
     Now it looks like this `For all rail Then values of allocated hours should have the same value`.
* **2022-03-20**:
  * The webserver now maps some paths to the root index file.
    This allows the user to interact with the live server more easily,
    as it does not have to write the root index file into the URL in order to open it.
    Thereby, the user does not have to handle an error,
    because it tried to open a website with the most common URL.
    In other words `https://localhost:8443/index.html` becomes `https://localhost:8443`.
### Minor Changes
* **2023-08-14 \#249**: Split `net.splitcells.network` root project into a root project,
  that is used for building all network projects,
  and a network file project containing documentation.
  This way the root folder of the network project is just a multi module Maven project for building and
  the projects sub folder contains all projects of the network project.
* **2023-06-11 \#s140**: Publish repo of `net.splitcells.symbiosis`.
* **2022-12-05** [**\#s118 Speed up SourceHut build via m2 cache**](https://todo.sr.ht/~splitcells-net/net.splitcells.network/118):
  The new repo `net.splitcells.network.m2` is created, that contains a Maven m2 repo.
  This m2 repo is used in order to build Maven projects locally.
  This m2 repo also works as an inventory of external Maven dependencies.
* **2022-12-02** [**\#198** Manage dot files](https://github.com/www-splitcells-net/net.splitcells.network/issues/198):
  Recommend Chezmoi in order to manage dotfiles.
* **2022-10-21** [**\#162** Ensure that integration and capabilities tests are executed from time to time.](https://github.com/www-splitcells-net/net.splitcells.network/issues/162)
* **2022-10-17**: **\#s107** Document core, API and extension concepts in the project guidelines.
* **2022-10-16**:
  1. **\#210**: Create general source code guidelines with the current main on automatically formatting the code.
  2. **\#200**: [Join the OpenSSF Best Practices Badge Program.](https://bestpractices.coreinfrastructure.org/en/projects/6588)
* **2022-08-22**: Create new projects and link to their respective peer repos:
  * `net.splitcells.network.community.via.javadoc`
  * `net.splitcells.network.community.git-bug`
  * `net.splitcells.network.repos`
* **2022-07-29**: [**\#165**](https://github.com/www-splitcells-net/net.splitcells.network/issues/165) Create [deprecation protocol guidelines](https://splitcells.net/net/splitcells/network/guidelines/backwards-compatibility.html).
* **2022-04-04**: [**\#171**: Log number of warnings:](https://github.com/www-splitcells-net/net.splitcells.network/issues/171)
  This are logged via `./bin/test.via.network.worker` and logged at `net.splitcells.network.log/src/main/csv/net/splitcells/network/worker/builder/warnings/$(hostname).csv`.
* **2022-03-06**: **\#10**: Create project `net.splitcells.dem.core`,
    which contains interface implementations of the Dem project.
    The goal is to split the Dem project into an interface project (merger)
    and implementation project (core).
* **2022-02-28**: **\#125**: Create and add `CommonMarkChangelogEventRenderer`
  to the web server in order to extract events from one changelog file.
  For every supported path `[...}/CHANGELOG.html` there is now an appropriate
  supported path `[...}/CHANGELOG.events.html`.
* **2022-02-25**
  1. Create command `repo.copy`.
     It makes the `repo.clone*` commands more usable, by providing useful options and
     making it easy to combine said flags.
     For instance, in order to ensure that a folder contains a repo one can use the following call:
     `repo.copy --target-folder=. --omit-if-exists=true --remote-repo='<URL of remote repo>'`.
     I do not know, if this is better than
     `repo.exists || repo.clone.into.current '<URL of remote repo>'`.
     It may be the case, because additional checks can be added more easily this way,
     without changing existing code,
     but I am not quite sure.
     On the other hand, this is also a way to document the expected use cases of a tool
     and can be used as a guide, informing the user how to use the software.
  2. **\#142**:
     1. Define `rootPath` option to web server config.
     2. Translate links according to the `rootPath` config,
        which is `/` by default.
        If `/` is used as `rootPath`,
        then the path translation does not change the path.
        For instance, `/net/splitcells/gel/index.html` is translated to `/net/splitcells/gel/index.html` in this case.
        If `rootPath` is set to `/net/splitcells/martins/avots/website/`,
        then it is translated to `/net/splitcells/martins/avots/website/net/splitcells/gel/index.html`.
* **2022-02-23**
  1. [**\#s69** Create easier to navigate alternative to local path context in website](https://todo.sr.ht/~splitcells-net/net.splitcells.network/69):
     The default layout for the website contains now a `Relevant Local Path Context` section
     in the secondary content column.
     It is located right before the `Local Path Context` and only lists paths,
     that are relevant for ordinary users.
     The new section makes it easier to navigate between the different pages of
     the site.
  2. **\#s69**:
     The web server's renderers now have a concept of a relevant path layout.
     Other than the regular path layout, it contains not all supported path of the web server.
     The relevant layout contains a subset of the regular layout
     and focuses on the paths,
     that are relevant for the normal users.
### Patches
* **2022-02-20**: Fix syntax errors in `system.ssh.server.require` and
  `system.ssh.server.require`.
## [4.0.0] - 2022-02-14
### Summary
* **2022-02-14**: [**\#138** Create developer introduction](https://github.com/www-splitcells-net/net.splitcells.network/issues/138):
   1. An [introduction to the constraint system](https://splitcells.net/net/splitcells/gel/test/functionality/n-queen-problem.html)
      was created.
      It shows how to model and solve the N queen problem on a conceptional and source code
      level.
   2. The performance of Gel was improved as well by 5 times,
      because the runtime for solving the N queen problem was completely unacceptable.
      The original runtime for the Backtracking algorithm was 65 seconds and was
      improved to about 25 seconds.
      This is still a lot, but somewhat bearable as one (hopefully) would not make
      a new coffee in that time.
   3. There are also now 2 new repositories in order to avoid data bloat in the main repo:
      1. [net.splitcells.network.log](https://github.com/www-splitcells-net/net.splitcells.network.log):
         This is used as a public repo for logs like performance data or the
         execution time of project tasks.
      2. [net.splitcells.network.media](https://github.com/www-splitcells-net/net.splitcells.network.media):
         Contains media files like SVGs.
* **2022-02-15**: [Comment On Generic Allocators Fourth Version](https://splitcells-net.srht.site/blog/2022-02-15-generic-allocators-fourth-version/) [(Gemini Link)](gemini://splitcells-net.srht.site/2022-02-15-Generic-Allocators-Fourth-Version.gmi)
### Major Changes
* **2022-02-12**: [**\#s66** Move suitable Dem guidelines to the network project.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/66)
* **2022-01-30**: Remove all additional commands in `user.start` like
  `user.configure`,
  because this makes the command too complicated and it also makes it too hard
  to maintain backwards compatibility.
* **2022-01-09**: **\#138**: Make default values of StaticFlags more reasonable performant.
* **2022-01-09**: Remove `run.and.print.only.errors.sh` and `run.and.print.sh`,
  because there are too many `run.and.*` commands,
  that are not used and are too similar.
  This makes it unjustifiable hard to choice a fitting `run.and.*` command.
* **2021-12-28**: **\#s44** The Domsole now filters log messages with a lesser
  priority than INFO by default.
  This behaviour can be overwritten with the Option MessageFilter.
* **2021-12-26**: **\#s44** Rename `Validator` to `SourceValidator` in order to
  clarify purpose,
  because a validator for rendered files via the web server is needed.
* **2021-12-16**: The rating methods of the constraint interface now only return
  ratings instead of meta ratings,
  in order to increase runtime performance.
  Meta ratings have a higher overhead than primitive ratings,
  because these are wrappers around primitive ratings based on a lookup
  map.
  In most common optimization problems and also for simple problems in general,
  meta ratings are not required.
  Supporting this common case explicitly, yields performance gains.
  Meta ratings can be still obtained via the `asMetaRating` method of ratings.
* **2021-12-12**: The command `repo.repair` was rewritten in Python in order to
  support better argument parsing,
  which has a relatively complex syntax in Bash.
  The implicit argument (no flag required for setting) was converted to an
  explicit argument (`--remote-repo=[...]`.
  The command now also accepts one value for this argument,
  because multiple ones do not make sense.
* **2021-12-09**:
  1. **\#148** Unify method naming by renaming 2 methods:
     1. Rename `Contraint#register_addition` to `Contraint#registerAddition`.
     2. Rename `Contraint#register_before_removal` to `Contraint#registerBeforeRemoval`.
  2. **\#148** Fix typos:
     1. Rename `Database#subscriberToBeforeRemoval` to `Database#subscribeToBeforeRemoval`.
     2. Rename `Database#subscriberToAfterRemoval` to `Database#subscribeToAfterRemoval`.
* **2021-12-05**: **\#148** [Fix local path context for XML files](https://github.com/www-splitcells-net/net.splitcells.network/issues/148):
  Remove choice Documents from custom XML format.
  A choice elements can be wrapped with an article element in order to get
  the same result,
  but with metadata support.
* **2021-12-04**:
  1. **\#138**: Replace `net.splitcells.dem.environment.config.StaticFlags.PROFILING_RUNTIME`
     with `PROFILING_METHOD_STATISTICS`.
     The former one was deprecated,
     because it was not used and its intent was not clear.
     The latter one was introduced, because its usage is quite clear.
     Note that generic static flags are not that useful,
     because by enabling a generic one,
     many additional runtime tasks may be activated,
     which may destroy the software's performance.
  2. **\#138**: Rename package `net.splitcells.dem.resource.host.integration`
     to `net.splitcells.dem.resource.communication.log`.
     The reason for this, is the fact, that the shell may not be on the host
     computer.
* **2021-11-27**: **\#s22**: Rename Optimization interface to OfflineOptimization
  in order to clarify the difference to the new OnlineOptimization interface.
* **2021-11-21**: **\#s22**: Rename `net.splitcells.gel.solution.optimization.Optimization`
  to `OfflineOptimization` in order to clarify its meaning.
* **2021-11-06**:
  1. **\#10** Rename `net.splitcells.dem.data.order.Comparison.comparator_`.
     to `comparatorLegacy` in order to have a clear name and remove its usage,
     where it is not needed.
  2. **\#10** Rename `net.splitcells.dem.data.order.Comparators.comparators`
     to `comparison` in order to unify naming.
* **2021-11-05**: `system.update` for Flatpak now does not exit with an error,
  if Flatpak's version itself is deprecated.
  This is done because otherwise the whole update process would be aborted.
  An appropriate echo to stderr is created instead.
* **2021-10-31**: **\#110** [Register extension path via extension instead of duplicate code](https://github.com/www-splitcells-net/net.splitcells.network/issues/110):
  Remove deprecated `ProjectsRenderer#projectsLayout` method and use
  `ProjectsRenderer#projectsPath` instead.
  This new method is a lot easier to implement than the precursor,
  especially if future extension may be implemented via third party software.
* **2021-10-18**:
  1. **\#10** Privatize Constructor of NotImplementedYet exception.
     Thereby, the public API is unified.
  2. **\#10** Privatize Constructor of LinkTranslator for CommonMark.
     Thereby, the public API is unified.
* **2021-10-01**: Install man pages to `~/bin/man/man1` so that the common man
  command can find and display man pages installed by os state interface.
* **2021-09-18**: Install pip modules via `pip.module.install` for user
  and therefore without admin rights.
* **2021-09-18**: Remove command `system.configure.auto` of OS state interface
  project.
* **2021-09-17**: **\#10**
  1. Delete the interface
     `net.splitcells.dem.resource.communication.Subscribable`,
     because it was not used.
  2. Make package structure more consistent.
     1. Move Dem's Files interface from communication package to more general
        resource package.
     2. Move Dem's LogMessage, LogMessageI and LogLevel from the package
        `net.splitcells.dem.resource.communication.interaction` to
        `net.splitcells.dem.resource.host.interaction`,
        because sees interfaces and classes are not host specific.
  3. Rename `net.splitcells.dem.lang.annotations.Return_this` to `ReturnThis`
     in order to unify naming. 
* **2021-09-15**: **\#8** Migrate Domsole from XML node base to Perspective
  base.
  Thereby, a custom rendering format is created,
  which is more suitable for the console,
  as it is a lot shorter, simpler and nicer.
  The XML rendering is currently broken and will be fixed for the website,
  when it is used again.
  Logging based on XML nodes is removed,
  also some deprecated method still exists,
  but they have no functionality.
* **2021-09-11**: `run.and.show.if.failed` now echos content of stderr to
  the shell during the execution normally.
  This way one knows all the errors, that appeared after a successful
  execution of the given command.
  If for instance one has a data synchronization script for multiple backup
  servers, one can see which server could not be reached,
  without marking the whole execution as failure.
* **2021-09-05**:
   1. Disable caching for default weblayout in order to keep CSS styling in
      webbrowser up to date.
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
* **2022-02-14**: [**\#138** Create developer introduction.](https://github.com/www-splitcells-net/net.splitcells.network/issues/138)
* **2022-02-12**:
  1. **\#s68**: Recommend stating the license in CommonMark files.
  2. [**\#s68**: Create repo for media files:](https://todo.sr.ht/~splitcells-net/net.splitcells.network/68)
     This repo is named `net.splitcells.network.media` and is a peer repo to
     this repo cluster.
* **2022-02-07**: [**\#43**: Ensure at least 30% test coverage.](https://github.com/www-splitcells-net/net.splitcells.network/issues/43)
* **2022-02-05**:
   1. [**\#163**: Create `repo.push.at` as the new version of `repo.push.to`](https://github.com/www-splitcells-net/net.splitcells.network/issues/163):
      1. Deprecate `repo.push.to`,
         because it uses positional arguments and therefore understanding argument
         meaning gets hard.
         The command also only supports remote IDs and not URLs.
         The positional argument parsing makes it hard to add simple features.
         The deprecated commands signals this with an error message,
         but works otherwise.
      2. Create command `repo.push.at`, that uses only classical Unix style argument
         handling.
         Support for pushing to URLs was added as well.
   2. **\#10**: Create `generation.style` variable for default XSL style
      for multiple output style configuration.
   3. **\#10**: Integrate JaCoCo report into website via an additional renderer.
* **2022-02-04**: **\#s68**: Add peer repository `net.splitcells.network.media`
  to website.
* **2022-01-31**: Make test coverage via codecov.io public.
  It can be seen on the README.
* **2022-01-30**: Create command `world.manage`, that calls all
  configuration and update commands provided by OS state interface.
  Thereby, the user can check, if all possible configurations and updates are
  applied.
  This also acts as a central documentation for all configuration and update
  commands of the OS state interface project.
  This command adds the new concept of a world in the context of the user, system
  and network concepts.
  The world contains and represents all top level concepts
  and therefore is a representation of all accessible objects.
* **2022-01-23**: Create command `flatpak.gui`, in order to easily find
  Flatpak's default GUI based application manager.
* **2022-01-21**: Create command `sh.scroll.enable`.
  * Sometimes scrolling is not enabled in a shell.
    This can be the case, if one is opened over ssh.
    This command enables scrolling for the current session.
    This command is not used for scripting,
    but makes it easy to look this command up for manual usage.
* **2022-01-18**: Create command `ssh.execute` in order to execute commands
  on remote server in foreground.
* **2022-01-16**: Create and publish blog article [A Case For Repo Process](https://splitcells-net.srht.site/blog/2022-01-10-a-case-for-repo-process/).
* **2022-01-11**: **\#s58** Implement first draft for injecting blogs of
  different projects into the network blog.
* **2022-01-05**: **\#138** Create project `net.splitcells.gel.quickstart`.
  It is a minimal project, that demonstrates how use the Gel framework.
  It can also be used as a template for new projects.
* **2021-12-27**: **\#s44** Create first draft of HTML link Validator based on
  heuristics regarding HTML parsing,
  so that a framework does not need to be included. 
* **2021-12-23**: **\#138** CSV rendering via `chart.js` now renders the
  requested csv file.
  Previously, this renderer always rendered the same file,
  regardless of the request
* **2021-12-20**:
  1. Create command `ssh.execute.in.background` in order to start
     background tasks on remote servers with systemd and ssh.
  2. Create command `system.lock`,
     that locks all input and output devices of the system users'
     (i.e. lockscreen).
* **2021-12-19**: **\#138** Create rating cache for constraints via
  ConstraintAspect.
  In the future, it may be implemented in such a way,
  that it may be disabled.
  This is easy to implement, because this feature is implemented as an aspect.
* **2021-12-13**: **\#132** [Justify current bias toward online optimization.](https://github.com/www-splitcells-net/net.splitcells.network/issues/132)  
* **2021-12-11**: **\#138**
  1. Add `net.splitcells.network.log` to website's
     default projects.
     This is a new git repository containing log files.
     These files are primarily performance data and are used in order to show
     performance regressions.
  2. Create CSV serving and rendering of CSV files as charts to website server
     (CsvRenderer and CsvChartRenderer).
     This is used in order to create a simple visualization of the new
     repository.
* **2021-12-05**: **\#148** [Fix local path context for XML files](https://github.com/www-splitcells-net/net.splitcells.network/issues/148):
  Add path information to XML documents during website rendering.
* **2021-11-29**:
   1. **\#138** Create `test.capabilities` and `test.integration` in order to
      execute tests, that require relatively long time for execution.
* **2021-11-27**:
   1. **\#s22** [Implement backtracking optimization](https://todo.sr.ht/~splitcells-net/net.splitcells.network/22):
      1. Implements backtracking as an OnlineOptimization.
      2. In this context EnumerableOptimizationSpace was created in order to provide
         a search space for backtracking.
   2. **\#142** Create `net.splitcells.website.html.content` project in order to
      provide an alternative HTML styling.
   3. **\#s26** [Support arbitrary renderers for website server](https://github.com/www-splitcells-net/net.splitcells.network/issues/26):
      1. Details are described in the [network's blog](https://splitcells-net.srht.site/blog/2021-11-27-supporting-arbitrary-website-renderes/). 
      2. Create OS State Interface command `project.render`,
         in order to define a primitive unified building API for different build
         systems and projects.
      4. Create command `project.render.as.net.splitcells.website` in order
         to support arbitrary website renderers.
         This is currently used in order to deploy the network's blog,
         my private website and the network's website as one website.
         Linking between the network's blog and the rest of the site is currently
         lacking.
* **2021-11-20**: **\#37** [Simplify default web layout.](https://github.com/www-splitcells-net/net.splitcells.network/issues/37)
* **2021-11-08**:
  1. **\#26** Create command `project.render.as.net.splitcells.website` as an
     API for creating plugins for `splitcells.net`'s website server built out of
     software, which have no explicit integration for said website server.
     Integration is done by reading or writing to the same filesystem.
     In order to create a plugin following needs to be done:
     * Add and implement the following executable at a project: `./bin/render.as.net.splitcells.website.to`.
       It takes one argument containing a path.
       The command needs to write the plugin's part of the website to that path.
     * Add the following to the website's build script:
       * `cd <plugin's path>`
       * `project.render.as.net.splitcells.website`
  2. **\#s24** [Deprecate usage of CommonMark as the main documentation format](https://todo.sr.ht/~splitcells-net/net.splitcells.network/24):
     CommonMark is now only supported on second level.
     Sew XML is now the primarily supported format for articles.
     The [source type guidelines](./src/main/md/net/splitcells/network/guidelines/source-types.md)
     were updated accordingly.
* **2021-10-31**:
  1. **\#55** [Minimize number of used languages and protocols](https://github.com/www-splitcells-net/net.splitcells.network/issues/55):
     Create [guidelines](src/main/md/net/splitcells/network/guidelines/source-types.md)
     for the formats used in the source code.
  2. **\#117** [Document N-Queen-Problem for users](https://github.com/www-splitcells-net/net.splitcells.network/issues/117):
     The file is located [here](projects/net.splitcells.gel.sheath/src/main/md/net/splitcells/gel/test/functionality/n-queen-problem.md). 
* **2021-10-11**:
  1. **\#119** Document deployment:
     1. Create Maven profile `deployment` in order to create deployable jars.
     2. [Document](src/main/md/net/splitcells/network/deployment.md) deployment and link it from README.
  2. **\#128** [Create word dictionary with links to their definition.](https://github.com/www-splitcells-net/net.splitcells.network/issues/128).
* **2021-10-05** Create blog articles:
  1. [Development Timing And Discovery Based Networks Of Perspectives](https://splitcells-net.srht.site/blog/2021-10-04-development-timing-and-discovery-based-networks-of-perspectives/)
  2.[There is the bad, there is the horrible and than there is [sight] error handling.](https://splitcells-net.srht.site/blog/2021-10-04-there-is-bad-there-is-horrible-and-than-there-is-error-handling/)
* **2021-10-03** **\#101** [Support gemini protocol for network blog](https://github.com/www-splitcells-net/net.splitcells.network/issues/101):
  The site was deployed to `splitcells-net.srht.site`([Gemini link](gemini://splitcells-net.srht.site)).
* **2021-10-02** **\#99** [Add license headers to all webservers default content](https://github.com/www-splitcells-net/net.splitcells.network/issues/99):
  1. The network's [blog articles](https://splitcells-net.srht.site/)
  2. CSS files
  3. Javascript files
  4. HTML files
  5. Layout/XSL files
* **2021-09-30** **\#s6** [Jumpstart State Network Optimization (SEP)](https://todo.sr.ht/~splitcells-net/net.splitcells.network/6):
  Provide an API in order to manage and work on complex optimizations,
  that consist of multiple sub problems.
  Thereby the SchoolSchedulingTest was migrated to new the API in order to simplify
  future code for this test.
* **2021-09-19** Define chrome installation command via flatpak.
* **2021-09-17**: **\#10** Introduce the first pure project: [net.splitcells.dem.merger](projects/pure/net.splitcells.dem.merger)
* **2021-09-14**
   1. Create concept [presentation](http://splitcells.net/net/splitcells/gel/presentation/covid.html) for Gel based on thought experiment.
   2. Support serving raw HTML files via webserver.
   3. Render [changelog](http://splitcells.net/net/splitcells/network/CHANGELOG.html) on website.
* **2021-09-11**: Support Fedora Silverblue, by implementing `package.install` via `rpm-ostree`.
  This package install command has the highest priority, because package managers like dnf should be ignored
  in this case.
* **2021-09-04**:
   1. Create command `user.ssh.key.login.allow` in order to allow login via
      public key. This command mainly grants read access
      to `~/.ssh/authorized_keys` for the ssh server.
   2. Create command `user.ssh.key.authorize` in order to import public keys
      into `~/.ssh/authorized_keys` via command.
      Already present entries are not inserted.
   3. Remove Lombok plugin installation steps inside
      `package.install.java.ide.via.eclipse` because an update of Flatpak
      broke this.
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
* **2022-02-11**:
  1. Fix source code error in `user.ssh.key.authorize`,
     which prohibited its execution.
  2. Simplify and generalize detection for Fedora based systems with DNF in
     `system.ssh.server.start`.
     This makes this script also workable on Fedora Silverblue.
* **2022-02-10**: JPG images served by website server's resource extension now
  get the correct content type.
* **2022-02-07**: SVG images served by website server's resource extension now
  get the correct content type.
* **2022-01-23**: **\#43** The default XSL style sheet for the website does not
  convert the layout links for the local path context anymore.
  The conversion was introduced as a hack in order to compensate for other bad
  code.
  It converted paths for XML files by replacing the XML suffix via an HTML
  suffix,
  which made the links invalid.
* **2022-01-23**: **\#138**: LinearDeinitializer now correctly selects the
  allocation of a supply.
* **2022-01-23**: **\#s27**: [Fix local path content for rendering of text files.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/27)
* **2022-01-09**: **\#148**: [Fix local path context for rendered XML files in website server.](https://github.com/www-splitcells-net/net.splitcells.network/issues/148)
* **2022-01-02**: **\#138** Fix user command rendering via `UserCommandRenderer`
  by rendering command tree with `http://splitcells.net/den.xsd` namespace.
* **2021-12-31**: **\#138** The website server now supports via the LinkTranslator links
  from CommonMark files to other files located at other projects,
  instead to other files of the same projects.
* **2021-12-27**:
  1. **\#s44**:
     1. Restore rendering of stack traces for logging in
        Domsole (see `Ui#append`).
     2. Fix warning in webserver caused by `UserCommandRenderer#projectPaths`.
        This method now correctly provides its managed project paths.
* **2021-12-23**: The supply selector had a probability to throw an exception,
  if there are freed supplies given an optimization event list,
  but no free supplies are present before applying the optimization event list
  to a given solution.
  If there are no free supplies present,
  freed supplies are used by the supply selector instead.
* **2021-12-19**: `repo.synchronize.with` now uses the new interface of
  `repo.repair`,
  which got a breaking change previously in order to improve
  and standardize argument parsing.
* **2021-12-10**: **\#138** Remove temporary duplicate assignments during
  HistoryI reset, in order to improve performance.
* **2021-12-05**: Fix implementation and runtime check of `HistoryI#resetTo`.
* **2021-12-05**: `Ordered#compare_to` now does not always double-check
  equality.
* **2021-12-03**: `command.managed.execute` now uses `shutil.which` instead of
  `distutils.spawn.find_executable`,
  because the latter one is deprecated and will be
  [removed from Python with the version 3.12](https://www.python.org/dev/peps/pep-0632).
* **2021-11-27**:
    1. **\#s22** Fix the ordering of MetaRatingI.
       Previously, the order was inverse: a meta rating with cost of 0 was
       worse than a meta rating with cost of 1.
    2. `shell.width.sh` now works in Bourne shell as well.
* **2021-11-20**: Fix text file rendering in web server via `txt` file suffix.
* **2021-10-25**  **\#90** Fix deployment of CommonMark files:
  Before the patch CommonMark were not rendered and uploaded via the website
  server.
* **2021-10-22** `command.managed.install` now removes the file suffix
  of the source file, when installing the command as a dependency.
  Previously this was only done for the API defining commands.
  For example: the Ossi project has the command `repo.push.sh`,
  which uses dependency injection in order to do its job.
  This command is installed to `~/bin/net.splitcells.os.state.interface.commands.managed/repo.push`.
  Note the stripped file suffix in order to have a name,
  that is not specific to the implementation
  (this behaviour was already present before the implementation).
  Another file provides the implementation for Git repositories and is named
  like the original command + its implementation specific file suffix.
  Let's say `repo.push.sh`.
  Before the patch the file suffix would not be removed and the file would be
  installed to `~/bin/net.splitcells.os.state.interface.commands.managed/repo.push.sh.0`.
  This would lead to an error, during the execution of `repo.push`,
  because the dependency `repo.push.0` would not be present.
  After the patch the file is installed to `~/bin/net.splitcells.os.state.interface.commands.managed/repo.push.0`.
* **2021-10-19** **\#s7**: Fix CI timeout on sourcehut.
  `user.ssh.key.generate.sh` causes a timeout on the build job for FreeBSD.
  This was caused by the fact, that the file location and the password were set
  by sending newline symbols via the echo command and a shell pipe to the
  `ssy-keygen` command.
  The echo command was used in a non POSIX way,
  which fails on the new FreeBSD release.
  This patch sets the file location and password via flags provided by
 `ssh-keygen`.
* **2021-10-18**: **\#100** Fix links from CommonMark files transformed to
   CommonMark files:
   Before the patches the webserver could not create links between CommonMark
   files, because the file suffix `.md` was not replaced with `.html`.
  `.md` are the file suffix for the source CommonMark files.
  `.html` are the file suffix for the rendered CommonMark files.
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
13. Some undocumented work.
14. **2020-12-31**: Create public git repository.
15. Some undocumented work.
16. **2019-05-05**: Create private git repositories.
17. **2017-01-01**: Work Without Version Control (The date is an approximation.)
18. **2016-11-01**: Created the second version of assignment solver written from the ground up for master thesis and named it `Generic Allocator` for the first time.
    See [here](https://splitcells.net/net/splitcells/gel/history/origin-of-the-project-generic-allocator.html) for more info.
    (The date is an approximation.)
19. **2015-01-01**: Created precursor assignment solver of this project during an internship at university.
    See [here](https://splitcells.net/net/splitcells/gel/history/origin-of-the-project-generic-allocator.html) for more info.
    (The date is an approximation.)
### Minor Changes
1. **2022-10-31**: [**\#s97** Document migration based development.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/97)
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
### Patches
1. **2021-06-27** Log error stack traces in web server.
2. **2021-06-26** Add optimization to `den.xsd`.
3. **2021-06-25** Determine project's source folder correctly in ProjectRenderer.
4. Some undocumented work.
## [2.0.0]
Second version created during master thesis.
## [1.0.0]
First version created during practical course in university study. 
