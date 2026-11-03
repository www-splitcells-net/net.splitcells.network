---
name: Developer
description: "Use this, when checking and editing changelogs."
---
# Guide Intro
Only add things to this document,
that significantly shortens the time needed to learn to check an edit changelogs.
AI agents should update this file automatically.
# Changelog Guide
Every project has a `CHANGELOG.md` at its top folder.
It should note only high level info for developers and users.
Especially, breaking changes should be noted as such.
A change should be located in the changelog of the project, where the changed happened.
Projects are located at `projects/*/`.
Changes outside of `projects/` (i.e. `bin/`) belong to the root `CHANGELOG.md`.
New entries are added at the top of the `### Major Changes` or `### Minor Changes` section of `## [Unreleased]`.
# Maintain changelog
Check the commits of the last 7 days.
Add missing info in the changelogs and check if the changelog contains the info of the last 7 days
Do not commit any changes.
Do not change the format of the changelogs.
The entry style differs per changelog (e.g. `**2026-10-31\#32**` vs `**2026-10-31: \#32**:`),
so copy the style of the existing entries in the respective changelog.
Pure refactorings, formatting, tests, typo fixes and changelog/skill edits do not need entries.