----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Contributing to Splitcells Network
* [Licensing](./LICENSE.md) and [License Notices](./NOTICE.md) of This Project
* [Source Code Guidelines](https://splitcells.net/net/splitcells/network/guidelines/index.html)
* [Build Instructions](https://splitcells.net/net/splitcells/network/BUILD.html)
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

Many API changes are located and categorized in the changelogs of the corresponding project.
Breaking changes are tried to be omitted, but there is no guarantee for that.

Absolute backward compatibility creates a maintenance burden,
but we do not break backward compatibility just for fun.

You can try to decrease the likelihood of breaking a certain feature,
by contributing an appropriate test case/suite for this feature.
Regardless of that, keep in mind, that there is no guarantee of backwards
compatibility.

TODO: Consider declaring backward compatibility a nice to have feature, instead of an absolute goal.
If backward compatibility is not guaranteed, an migration guideline should be provided.
Maybe via OpenRewrite for Java code?
The reasoning behind this is the maintenance burden without other relevant positive side effects.
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