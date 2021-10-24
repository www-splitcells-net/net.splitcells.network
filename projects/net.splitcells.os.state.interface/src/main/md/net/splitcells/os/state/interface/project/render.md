# Project Rendering Command
The project rendering command mainly provides a simple API in order to integrate
build system.

The rendering command takes the from-project and creates a file structure at
the to-project:
* Check the from-project in order to determine its type.
* Check the to-project in order to determine its type.
* Determine and execute the appropriate build command via dependency injection
  provided by `command.managed.execute`.

Let's illustrate this with an example, the deployment of [splitcells.net](http://splitcells.net):
![Example Illustration](../../../../../../../../../src/main/svg/net/splitcells/os/state/interface/project/render/example.illustration.svg)
1. Copy files from `website.default.content/src/main/resources` to a temporary
   folder.
2. Build the `network.blog` with the static blog generator hugo and copy its
   files to the temporary folder.
3. Render XML and CommonMark files and place these at the temporal folder.
4. Upload content of temporal folder to the SFTP-Server, which host
   the website.