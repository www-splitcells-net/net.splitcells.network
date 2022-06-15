# Contributing to Splitcells Network
* [Licensing](./LICENSE.md) and [License Notices](./NOTICE.md) of This Project
* [Source Code Guidelines](https://splitcells.net/net/splitcells/network/guidelines/index.html)
## Building the Project
Java 11 (AdoptOpenJDK is preferred), Maven, Python 3 and Bash is required in
order to build this project.
Execute `./bin/build`, that's it. ✨
The Java projects can be build via `mvn clean install` at the root folder.
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
## API Compatibility
There is no guarantee of backwards compatibility.

All API changes are located and categorized in the [Changelog](https://splitcells.net/net/splitcells/CHANGELOG.global.html).
Breaking changes are tried to be omitted, but there is no guarantee for that.
The author of the software use this project as a dependency for their own
private code.
So there is at least an interest, to keep breaking changes to a minimum.
On the other hand, the API is not polished,
so there will be breaking changes to the API.

Absolute backward compatibility creates a maintenance burden and if any kind
of backward compatibility is required it may be best to just contact this
project.
We do not break backward compatibility just for fun and would like to support
efforts to minimize breaking changes.

You can try to decrease the likelihood of breaking a certain feature,
by contributing an appropriate test case/suite for this feature.
Regardless of that, keep in mind, that there is no guarantee of backwards
compatibility.
## Helpful Notes
### Browsing Repository in Browser
When browsing the repo in the web some browser addons/extension might help
to visualize its content:
* PlantUML Visualizer for [Firefox](https://addons.mozilla.org/en-US/firefox/addon/plantuml-visualizer/)
  or [Chrome](https://chrome.google.com/webstore/detail/plantuml-visualizer/ffaloebcmkogfdkemcekamlmfkkmgkcf).
  renders PlantUML images.
* [TeX All the Things](https://chrome.google.com/webstore/detail/tex-all-the-things/cbimabofgmfdkicghcadidpemeenbffn/)
  for Chrome can be used for rendering math formulas in a neat way.
### Inspirational External Guidelines
* [Submitting patches: the essential guide to getting your code into the kernel](https://www.kernel.org/doc/html/latest/process/submitting-patches.html)
* [Creating Pull Requests](https://www.kernel.org/doc/html/latest/maintainer/pull-requests.html)
### Helpful Development Tools
This [PlantUML Editor](https://plantuml-editor.kkeisuke.com/) can be used to
edit PlantUMLs without installing software locally.