----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Shell and Bash Guidelines

* Consider [these guidelines](https://github.com/icy/bash-coding-style).

# Secrets

The primary goal, when processing secrets like SSH keys,
is to prevent these to being printed out or to be written to the wrong location,
in order to prevent a leak of the secrets.

When secrets from environment variables or arguments are handled in scripts or workflow files,
these should be written as quickly as possible to files,
that are than used by other tools like SSH and its `~/.ssh/*` files.
These files should never be printed via other tools.
This minimizes the number of written commands, that access the secrets, and
therefore minimizes the risk of accessing the secrets incorrectly.

In order to write the secrets to files use the following template,
that ensures, that under no conditions the private key is printed to the output.
````sh
set +x && echo "${{ secrets.SSH_WORKFLOW_PRIVATE_KEY }}" > ~/.ssh/id_rsa
````