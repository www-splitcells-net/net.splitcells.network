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
## API Compatibility
There is currently no guarantee of backwards compatibility.
Absolute backward compatibility creates a maintenance burden,
that currently has no benefits.

We do not break backward compatibility just for fun.
Many API changes are located and categorized in the changelogs of the corresponding project.
Breaking changes are tried to be omitted, but there is no guarantee for that.

You can try to decrease the likelihood of breaking a certain feature,
by contributing an appropriate test case/suite for this feature.
Regardless of that, keep in mind, that there is no guarantee of backwards
compatibility.

**TODO**: Consider declaring backward compatibility a nice to have feature, instead of an absolute goal.
* Provide migrations guidelines or even automation via things like OpenRewrite for breaking changes.
* No Forward compatibility
* Versioning system and releases are needed in this case.

## Inspirational External Guidelines
* [Submitting patches: the essential guide to getting your code into the kernel](https://www.kernel.org/doc/html/latest/process/submitting-patches.html)
* [Creating Pull Requests](https://www.kernel.org/doc/html/latest/maintainer/pull-requests.html)

## Notes

Signing by PGP or SSH key is not required.
Also, most open source projects do not do this and this does not seem to cause legal problem.
The [Linux Kernel](https://docs.kernel.org/process/maintainer-pgp-guide.html) does not sign commits and
instead focuses on verifying the developer communication instead.
Furthermore, all signature will be temporarily valid as well,
which reduces it usefulness,
also it does not mean, that check a repo regarding an expired key has not its use cases.
In the hand, commit signing creates a huge burden and has no benefits if it is not used properly.
Therefore, it is disregarded as a requirement.