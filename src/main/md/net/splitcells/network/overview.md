# Bird's-eye View of Splitcells Network

You can use browser extensions like [PlantUML Visualizer for Firefox](https://addons.mozilla.org/en-US/firefox/addon/plantuml-visualizer/)
to view the following diagram.

## Components

```puml
@startuml

[net.splitcells.network] .up.> related_projects
related_projects .up.> [os.state.interface.lib.gpl.2]
related_projects .up.> [os.state.interface.lib.gpl.3]
[net.splitcells.network] .up.> infrastructure
infrastructure <.up.> Github
infrastructure <.up.> Gitlab
infrastructure <.up.> sourcehut
[net.splitcells.network] -down-> [pom.java.defaults]

[pom.java.defaults] -down-> [Dem.process]
[Dem.process] -right-> [Configuration]
[Configuration] -right-> [Domsole]
[Dem.process] -down-> [Gel.defineProblem]
gel_user .down.> [Gel.defineProblem]
[Gel.defineProblem] -down-> [Problem]
[Problem] -down-> [Solution]
[Solution] -down-> [Optimization]
[Solution] .down.> solution_export
[Solution] -down-> [History]

import .down.> [Table]
[Table] -down-> [Supply]
[Table] -down-> [Demand]
[Supply] -down-> [Allocations]
[Demand] -down-> [Allocations]
[Allocations] -left-> [Problem]
[Problem] -down-> [Constraint]
[Problem] -down-> [DerivedProblem]
[Constraint] -down-> [Type]
[Constraint] -down-> [Query]
[Constraint] -down-> [Rating]
[Rating] -down-> [MetaRating]
[Rating] -down-> [Rater]

[net.splitcells.network] -right-> [os.state.interface]
osi_user .down.> [os.state.interface]
[os.state.interface] -down-> [os.state.interface.lib]
[os.state.interface] -right-> [command]
[command] -down-> [repo.process]
[command] -down-> [command.managed]
[os.state.interface.lib] .down.> users_system

[Dem.process] -left-> [ProjectsRenderer]
[ProjectsRenderer] .down.> website
[ProjectsRenderer] -down-> [Extension]
[Extension] -down-> [Validator]
[Network.Blog] .down.> website
website .down.> net.splitcells

@enduml
```

## Infrastructure

```puml
@startuml

[net.splitcells.network] <.up. maven_central
[net.splitcells.network] <.up.> repo
repo <.up.> Github
repo <.up.> Gitlab
repo <.up.> sourcehut
[net.splitcells.network] .down.> www.splitcells.net

@enduml
```