# Changelog
This Changelog is inspired by [keepachangelog.com](https://keepachangelog.com/en/1.0.0/).
Version numbers are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
This file adheres to the [CommonMark](https://spec.commonmark.org/0.29) specification.
## [Unreleased]
### Major Changes
1. 2021-06-21: Remove `net.splitcells.gel.problem.Problem#toSolution()`,
   because it is not used and was not implemented yet.
### Minor Changes
1. 2021-06-19: Create command convention `repo.is.clean`.
   Exits with 0, if this repo can be synchronized and else exits 1.
   This can be used as a safeguard for automated synchronization command
   by exiting a command, when uncommitted changes are present.
1. 2021-06-06: [#54 Integrate CommonMark documentation into project rendering.](https://github.com/www-splitcells-net/net.splitcells.network/issues/54)
