# Changelog
This Changelog is inspired by [keepachangelog.com](https://keepachangelog.com/en/1.0.0/).
Version numbers are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
This file adheres to the [CommonMark](https://spec.commonmark.org/0.29) specification.
## [Unreleased]
### Minor Changes
1. 2021-06-19: Create command convention `repo.is.clean`.
   Exits with 0, if this repo can be synchronized and else exits 1.
   This can be used as a safeguard for automated synchronization commands.
