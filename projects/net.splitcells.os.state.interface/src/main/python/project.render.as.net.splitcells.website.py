#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.

See `project.render.py` for origin of this command.
This command is used in order to render a website based on the format of
`net.splitcells.website.server`.

One could just './bin/render.as.net.splitcells.website.to' itself,
but this command explicitly formalizes this standard.

This as an API for creating plugins for `splitcells.net`'s website server built
out of software, which have no explicit integration for said website server.
Integration is done by reading or writing to the same filesystem.

In order to create a plugin following needs to be done:
* Add and implement the following executable at a project: `./bin/render.as.net.splitcells.website.to`.
  It takes one argument containing a path.
  The command needs to write the plugin's part of the website to that path.
* Add the following to the website's build script:
  * `cd <plugin's path>`
  * `project.render.as.net.splitcells.website`

TODO Make this an implementation of `project.render.py`,
that basically cd's into <--from-project> and then executes
`./bin/render.as.net.splitcells.website.to <--to-project>`.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import subprocess

if __name__ == '__main__':
	argsParser = argparse.ArgumentParser(description='Renders a project to a different project.')
	argsParser.add_argument('--to-project'
		, nargs='?'
		, type=str
		, help='This is the path of project, where the results are stored.'
		, required=True)
	parsedArgs = argsParser.parse_args()
	argsDic = vars(parsedArgs)
	subprocess.call('./bin/render.as.net.splitcells.website.to ' + argsDic.get('to_project')
		, shell='True')