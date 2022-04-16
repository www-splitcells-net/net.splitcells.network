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
* **2022-04-10**: Support rendering website to filesystem with a custom root path.
  This way multiple versions of a website can be deployed to a single filesystem.
  Compare `https://splitcells.net/net/splitcells/martins/avots/website/index.html`
  and `https://splitcells.net/net/splitcells/website/minimal/net/splitcells/martins/avots/website/index.html`.
  Previously, this was supported in theory,
  but there was a bug making it impossible.