# Changelog
The changelog format can be found [here](./src/main/md/net/splitcells/network/guidelines/changelog.md).
Tickets that create lasting requirements can be found [here](./src/main/md/net/splitcells/network/tickets/).

Tickets are referenced with a hashtag and prefix in order to indicate the
server, that hosts the ticket:
for example `#g1` stands for the first Github issue and `#s1` for the first
sourcehut issue.
Ticket references without a prefix are probably hosted on
[Github](https://github.com/www-splitcells-net/net.splitcells.network/issues).

Prefix To Server Dictionary:
* g = Github: [https://github.com/www-splitcells-net/net.splitcells.network/issues](https://github.com/www-splitcells-net/net.splitcells.network/issues)
* s = sourcehut: [https://todo.sr.ht/~splitcells-net/net.splitcells.network](https://todo.sr.ht/~splitcells-net/net.splitcells.network)
 
Ticket information located on platforms and not in the repository are
considered throw away information.
Links to such platforms may or may not be valid.

Releases are done every time an important ticket is completed.
## [Unreleased]
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
  1. **\#10** Rename `net.splitcells.dem.data.order.Comparator.comparator_`.
     to `comparatorLegacy` in order to have a clear name and remove its usage,
     where it is not needed.
  2. **\#10** Rename `net.splitcells.dem.data.order.Comparators.comparators`
     to `comparator` in order to unify naming.
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
  2.[There is the bad, there is the horrible and than there is \<sight\> error handling.](https://splitcells-net.srht.site/blog/2021-10-04-there-is-bad-there-is-horrible-and-than-there-is-error-handling/)
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
* **2021-12-10**: **\#138** Remove temporary duplicate allocations during
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
### Patches
1. **2021-06-27** Log error stack traces in web server.
2. **2021-06-26** Add optimization to `den.xsd`.
3. **2021-06-25** Determine project's source folder correctly in ProjectRenderer.
4. Some undocumented work.
## [2.0.0]
Second version created during master thesis.
## [1.0.0]
First version created during practical course in university study. 
