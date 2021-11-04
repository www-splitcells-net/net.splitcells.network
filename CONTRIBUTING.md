# Contributing to Splitcells Network
## Building the Project
Java 11 (AdoptOpenJDK is preferred), Maven, Python 3 and Bash is required in
order to build this project.
Execute `./bin/build`, that's it. ✨
The Java projects can be build via `mvn clean install` at the root folder.
## Browsing Repository in Browser
When browsing the repo in the web some browser addons/extension might help
to visualize its content:
* PlantUML Visualizer for [Firefox](https://addons.mozilla.org/en-US/firefox/addon/plantuml-visualizer/)
  or [Chrome](https://chrome.google.com/webstore/detail/plantuml-visualizer/ffaloebcmkogfdkemcekamlmfkkmgkcf).
  renders PlantUML images.
* [TeX All the Things](https://chrome.google.com/webstore/detail/tex-all-the-things/cbimabofgmfdkicghcadidpemeenbffn/)
  for Chrome can be used for rendering math formulas in a neat way.
## Submitting via Pull Request
* The authors of the commits have to sign off all commits of the pull request
  according to the [Developer’s Certificate of Origin (DCO)](src/main/txt/net/splitcells/network/legal/Developer_Certificate_of_Origin.v1.1.txt).
  You thereby confirm compliance with the DCO.
  Help regarding the version control Git can be found
  [here](src/main/md/net/splitcells/network/guidelines/gist/git.md).
* At least the main author has to sign the last commit with its PGP key.
  PGP signing helps to ensure source code integrity.
  A quickstart and further guidelines for PGP can be found
  [here](src/main/md/net/splitcells/network/guidelines/gist/pgp.md).
## Inspirational External Guidelines
* [Submitting patches: the essential guide to getting your code into the kernel](https://www.kernel.org/doc/html/latest/process/submitting-patches.html)
* [Creating Pull Requests](https://www.kernel.org/doc/html/latest/maintainer/pull-requests.html)
## Helpful Development Tools
This [PlantUML Editor](https://plantuml-editor.kkeisuke.com/) can be used to
edit PlantUMLs without installing software locally.