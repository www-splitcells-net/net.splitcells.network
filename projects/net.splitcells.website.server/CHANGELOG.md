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
* **2022-03-20**:
  [**\#125** Create global news feeds](https://github.com/www-splitcells-net/net.splitcells.network/issues/125):
  It is located at `/net/splitcells/CHANGELOG.global.html`.