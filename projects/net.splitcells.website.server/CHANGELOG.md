# Changelog
The changelog format can be found [here](../../src/main/md/net/splitcells/network/guidelines/changelog.md).

## [Unreleased]
### Major Changes
* **2022-03-07**:
  1. Move `net.splitcells.website.server.project.ProjectsRenderer`
     to `net.splitcells.website.server.projects.ProjectsRendererI` in order to
     have a dedicated package for combining the rendering of multiple projects.
  2. Create interface `ProjectsRenderer` used by `ProjectsRendererI`.
     This way alternative web server implementations are possible.
### Minor Changes
* **2022-05-28**: **\#168**: `repo.status` now executes `./bin/net.splitcells.osi.repos.peers` in order to find additional sub repos, that are located in the same repo as the current is.
  This is a way to support repo nesting with a flat folder structure.
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
* **2022-05-23**: **\#82**: XML-Rendering can now handle the header `<?xml version="1.0" encoding="UTF-8"?>` of the input document without triggering an error.
  This is patch is implemented hackily.
* **2022-04-29**: **\#91**: Links to sub projects READMEs in CommonMark files are now translated correctly.
* **2022-04-10**: Support rendering website to filesystem with a custom root path.
  This way multiple versions of a website can be deployed to a single filesystem.
  Compare `https://splitcells.net/net/splitcells/martins/avots/website/index.html`
  and `https://splitcells.net/net/splitcells/website/minimal/net/splitcells/martins/avots/website/index.html`.
  Previously, this was supported in theory,
  but there was a bug making it impossible.