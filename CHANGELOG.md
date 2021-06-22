# Changelog
## Format
This Changelog is inspired by [keepachangelog.com](https://keepachangelog.com/en/1.0.0/).
Version numbers are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
This file adheres to the [CommonMark](https://spec.commonmark.org/0.29) specification.

Only completed ticket are listed here.
Partially completed tickets are not listed here.

Change items, that represent a ticket,
states the ticket's title after the ticket number with a colon:
```
1. **<Date of Completion If Known>**#<Ticket Number>: <Ticket Title>
```
For items ony related to a ticket, the following format is used:
```
1. **<Date of Completion If Known>**#<Ticket Number> <Description>
```
For change items without a ticket, the following format is used:
```
1. **<Date of Completion If Known>** <Description>
```
## [Unreleased]
### Major Changes
1. **2021-06-21**: Remove `net.splitcells.gel.problem.Problem#toSolution()`,
   because it is not used and was not implemented yet.
1. **2021-04-13**: #7: Model and solve oral exam problem.
1. **2021-04-07**: #42: Make Gel workspace visible in website.
1. **2021-04-12**: #46: Use deterministic environment and execution in CI.
### Minor Changes
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
## [2.0.0]
Second version created during master thesis.
## [1.0.0]
First version created during practical course in university study. 
