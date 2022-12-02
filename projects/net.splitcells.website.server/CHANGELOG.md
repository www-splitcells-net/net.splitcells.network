----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
The changelog format can be found [here](../../src/main/md/net/splitcells/network/guidelines/changelog.md).

## [Unreleased]
### Major Changes
* **2022-07-06**: **\#114**: Links to XML documents in CommonMark documents are translated to links to corresponding HTML documents
  (i.e. `/net/splitcells/gel/index.xml` -> `/net/splitcells/gel/index.html`)
  in order to link between different document types.
* **2022-03-07**:
  1. Move `net.splitcells.website.server.project.ProjectsRenderer`
     to `net.splitcells.website.server.projects.ProjectsRendererI` in order to
     have a dedicated package for combining the rendering of multiple projects.
  2. Create interface `ProjectsRenderer` used by `ProjectsRendererI`.
     This way alternative web server implementations are possible.
### Minor Changes
* **2022-11-19**: [**\\#s115** Speed up website deployment.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/115):
  Introduce `net.splitcells.website.server.Config.mutableProjectsPath` in order to state,
  if `projectsPath` can be assumed to stay the same during the web servers execution.
  This speeds up the rendering of static websites enormously,
  because `ProjectsRendererI` caches `projectsPath`, if `mutableProjectsPath` is set to true.
  This in turn greatly improves the performance of `RenderingValidatorForHtmlLinks`,
  as it uses `projectsPath` in order to validate internal links.
* **2022-11-18**: **\\#8**: If `net.splitcells.website.RenderUserStateRepo` is set to true,
  add the project at `~/.local/state/net.splitcells.dem/` to the default set of projects for the project renderer.
  This project is used in order to store log files and other documents.
  `RenderUserStateRepo` is set to false by default,
  because this should only be rendered, if the user explicitly wants it,
  in order to prevent leakage of private information be accident.
  This could otherwise happen,
  if the user renders a website with defaults settings and
  uploads it without checking the results in detail.
* **2022-10-23**: [**\#s89** Fix cookie issue.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/89)
  1. There is now [HTML guidelines](https://splitcells.net/net/splitcells/network/guidelines/html.html) regarding cookies.
  2. Access to `document.cookie` is now blocked, by overwriting its getter and setter methods in the standard layout.
     This ensures, that cookies are not used by accident.
* **2022-09-04**:
  1. Render `DEVELOPMENT.md` of each project.
  2. Render `BUILD.md` of each project.
  3. Render `LICENSE.md` of each project.
  4. Render `NOTICE.md` of each project.
* **2022-08-27**: The global changelog now widens the definition of events searched in other changelogs.
  Previously every list starting with just a date, was considered an event description.
  Now random strings after the date are accepted, as long as these are seperated by a whitespace from the date.
* **2022-05-29**: [**\#s84** Centralise build instruction documentaton.](https://todo.sr.ht/~splitcells-net/net.splitcells.network/84):
  1. Render `CONTRIBUTING.md` for each project.
  2. Instead of duplicate build instructions links to the `CONTRIBUTING.md` document is provided.
* **2022-05-28** [**\#87** Fix links inside documents](https://github.com/www-splitcells-net/net.splitcells.network/issues/87):
  `/net/splitcells/network/status.html` now shows, if from a historic perspective the number of invalid links is zero or at least improves.
  It checks the repo `net.splitcells.network.log`,
  if according to the file `src/main/csv/net/splitcells/website/server/project/validator/RenderingValidatorForHtmlLinks/<build name>/<builder server>.csv`,
  the number of invalid links of the last build is as small as the lowest number of invalid links in the complete build history.
* **2022-05-02**: **\#142** Make it easy to support arbitrary input formats and output styling.
* **2022-04-25**: **\#162**: On the default layout,
  there is now status button on the website in the top left corner.
  It currently shows, if all available computers executed the build system in last 7 days.
  If this is the case it states `INFO`, which is based on the log levels.
  If this is not the case it states `WARNING`.
  The button leads to `https://splitcells.net/net/splitcells/network/status.html` which lists all successful
  and failed executions.
  Keep in mind, that on the version hosted at `https://splitcells.net/`,
  the status only refers to the status during the build time of the website, as it is static.
* **2022-04-17**:
  * \#142 Deploy first draft of minimal styled version to `splitcells.net`.
  * \#57 Render Javadoc of projects at `<projectFolder>/javadoc/*`,
    if a Javadoc build is located at `target/site/apidocs`.
* **2022-03-29**: Create renderer extension for JavaScript files.
  Thereby, `*.js` files can be requested and the `text/javascript` MIME type
  will be used.
  These JavaScript can be stored under `src/main/js/**` for each project.
* **2022-03-20**:
  [**\#125** Create global news feeds](https://github.com/www-splitcells-net/net.splitcells.network/issues/125):
  It is located at `/net/splitcells/CHANGELOG.global.html`.
### Patches
* **2022-05-29**: [**\#s78** Wrong Context Path](https://todo.sr.ht/~splitcells-net/net.splitcells.network/78):
  1. This is primarily caused by not passing the path of a rendered file to the rendering process.
  2. Prevent usage of FileStructureTransformer inside extensions, as this class does not manage the handling of path information of a rendered document directly/explicitly.
     Instead, provide rendering methods in ProjectRenderer and ProjectsRenderer, which require and handle path information for a given document and pass it to the FileStructureTransformer.
  3. Extensions should pass relative and not absolute paths to the Project(s)Renderers.
* This is caused mainly by the usage of the FileStructureTransformer outside the ProjectRenderer. Most such usage is locateded in ProjectRendererExtensions.
* **2022-05-23**: **\#82**: XML-Rendering can now handle the header `<?xml version="1.0" encoding="UTF-8"?>` of the input document without triggering an error.
  This is patch is implemented hackily.
* **2022-04-29**: **\#91**: Links to sub projects READMEs in CommonMark files are now translated correctly.
* **2022-04-10**: Support rendering website to filesystem with a custom root path.
  This way multiple versions of a website can be deployed to a single filesystem.
  Compare `https://splitcells.net/net/splitcells/martins/avots/website/index.html`
  and `https://splitcells.net/net/splitcells/website/minimal/net/splitcells/martins/avots/website/index.html`.
  Previously, this was supported in theory,
  but there was a bug making it impossible.
