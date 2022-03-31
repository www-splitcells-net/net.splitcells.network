# Software Deployment
There is currently no ready to go deployment pipeline based on public artifact
repositories or distributors.
## Java Project Deployment
Build instructions for Java projects are located [here](./../../../../../../projects/net.splitcells.pom.java.defaults/README.md).
## OS State Interface And Its Library
OS state interface can be installed via the projects
[setup instructions](../../../../../../projects/net.splitcells.os.state.interface/net/splitcells/os/state/interface/manual/setup.md).
You can register its library project via `command.repository.register <path to this repo>/projects/net.splitcells.os.state.interface.lib`
for the current user's OS State Interface instance.
Executing `user.bin.configure` in the shell installs the library
for the current user's OS State Interface instance.