----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
The changelog format can be found [here](../../src/main/md/net/splitcells/network/guidelines/changelog.md).

## [Unreleased]
### Major Changes
* **2024-11-05: \#37** Add basic security system:
    1. Define and implement authorization.
    2. Define and implement authentication.
    3. Define and implement access control, in order to force a certain style of authorization and authentication API usage.
    4. Require admin role for RAM and CPU utilization pages.
* **2024-09-27: \#45** Support relative links in CommonMark documents.
  This makes the community repo easier to handle.
* **2024-09-24: \#38** Make web server thread safe, by providing a dedicated renderer for each thread.
* **2024-04-01: \#c15** Discourage server configuration without dependency injection.
* **2024-04-01:** [\#c15 Speed up static website deployment](https://codeberg.org/splitcells-net/net.splitcells.network.community/issues/15)
  by providing Config for static website rendering.
  Currently, this is used to disable tests during the execution of the `TestExtension`.
* **2024-01-19: \#252** `ProjectRendererI` now required `ProjectsRenderer` in order to provide source code.
* **2024-01-18: \#252** Provide source code of rendered content inside XSL at `/net.splitcells.website.server/source-code/`.
  The previous configuration file system, which was unused, unusable and available at `/net.splitcells.website/current/xml`,
  is removed.
* **2023-10-23: \#252** Rename `net.splitcells.website.server.project.RenderingResult`
    to `net.splitcells.website.server.processor.BinaryMessage` in order to have a clean abstract API
    for the website server communication.
    This API will be used as the base API for the webserver in the future.
* **2023-04-24: \#199** Rename project `net.splitcells.website.default.content`
  to `net.splitcells.website.content.default` in order to get more consistent project naming,
  when the website project for minimal content is introduced.
* **2022-12-17: \#27**: Extend Config with detailedXslMenu.
  If this value is present use its content for the detailed menu in the page layout.
  This is the start to remove hard coded personal info from the default layout.
  Its content is a XSL template, that declares the XSL variable `net-splitcells-website-server-config-menu-detailed`.
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
* **2024-08-24 \#37:** Provide optional multi-threading of `net.splitcells.website.server.Server#serveToHttpAt`.
  The multi-threading is achieved by using a copy of the `ProjectsRenderer` for each thread.
  This minimizes the amount of code needed, in order to achieve thread safety.
* **2024-05-09: \#c11** Provide `FileSystemViaMemory`.
* **2024-05-08: \#c11** Provide optional basic authentication in web server
  via the `PasswordAuthenticationEnabled` option.
* **2024-05-02: \#c11** Create `RedirectServer` in order redirect HTTP calls to HTTPS calls. 
* **2024-02-16: \#252** Support serving zip files.
* **2024-01-03: \#252** Provide `isServerForGeneralPublic` config for server,
    in order to only enable notifications with `only-for-general-public=true`,
    if `isServerForGeneralPublic` is also true.
    `isServerForGeneralPublic` is set to true by default.
* **2023-11-05: \#267** Provide table showing Dem's configuration via `net/splitcells/dem/config.html`.
* **2023-10-19: \#252** Add `projectsRendererExtensions` to Config in order dynamically add extension to the server.
* **2023-09-06: \#249**
    1. The menu at the top bar can now be configured via `Config#xslWindowMenu`.
    2. Tests can now be executed by requesting the page `net/splitcells/website/test.html`. 
* **2023-05-17: \#s86**
  1. Deprecate Renderer interface.
  2. Make sub renderer interfaces ProjectRenderer, ProjectsRenderer and ProjectRendererExtension more uniform.
  3. Provide a way to query title meta data from ProjectsRenderer.
* **2023-01-22**:
  1. [**\\#196** Create sane default config and behaviour regarding temporary data for, i.e. logging:](https://github.com/www-splitcells-net/net.splitcells.network/issues/196)
     1. Store logs etc. at `~./.local/state/net.splitcells.dem/src/main/[file type]/[program name]/[date time of program start]/` by default.
     2. Introduce the option `RenderUserStateRepo`, in order to render `~./.local/state/net.splitcells.dem`, if desired.
        Thereby, the logs can be found at the website's file layout (`/net/splitcells/website/layout.html`).
     3. The server now has the internal file `/net/splitcells/website/server/config/menu/detailed.xsl`,
        that can be configured.
        This XSL script generates the website's menu.
        The result is used for the website's side menu, that is visible on most pages.
        A dedicated menu page, is also generated by the XSL script and available at `/net/splitcells/website/main-menu.html`.
        It has basically the same content as the side menu.
        While the side menu is not visible on mobile phones in the default layout,
        the dedicated menu page can be viewed on mobile phones as well.
        Mobile users have an easy access to this page, via a link at the very top of every page.
   2. **\\#213**: If a CommonMark file starts with the standard license header,
      the license header is placed at the end of the file during rendering,
      in order to improve read flow.
* **2023-01-14**: **\\#27** Introduce `net.splitcells.website.server.Config.cssFiles`,
  in order to be able to replace the used css files used by the layout.
  This in turn improves customizability of the layout, with 
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
