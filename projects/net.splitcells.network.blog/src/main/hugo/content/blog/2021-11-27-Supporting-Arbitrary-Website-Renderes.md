---
title: Supporting Arbitrary Website Renderers
date: 2021-11-27
---
# Previous Situation
The best software is the one,
that does not need to be written.
The best way to accommodate this,
is to make written software replaceable and abandonable.

I created my own website server/static generator,
because I wanted to create the counterpart to dependency injection,
that is used in order to isolate projects from each other.
The idea was to create a dependency merging process,
that is used in order to create a unified documentation for a set of projects,
which may be isolated from each other.

I'm horrible at finding fitting public projects,
and so I naturally did not find one.
Therefore, I started creating my own after using Jekyll.
Until now, the web server was Java code only.
# Supporting Arbitrary Website Renderers
I created the command `project.render.py`,
that takes one input project,
transforms its files and writes the results to the output project.
The command itself does currently not do anything.

The actual implementations of the transformation process of this command is
injected via `command.managed.install.py`:
* Create a `project.render.*` script like `project.render.py` and
  `project.render.sh`, which is an implementation of `project.render`.
  Such an implementation, has to process the arguments `--from-project`
  and `--to-project`.
* Move it to `src/main/bash` or `src/main/sh` of a managed command repository,
  which is a folder registered via the command call
  `command.repositories.install <path of repository>`.
* Execute `user.bin.configure` in order to add the implementation to
  the `project.render` command.
* So when `project.render` is executed after `user.bin.configure`,
  the newly added implementation is executed as well.
  Arguments passed to `project.render` will be passed to all implementations.
  You can add as many implementations as one wants.
  All added implementations will be executed,
  when `project.render` is called.
# Project Rendering As A Build System
Note, that there is currently no default implementation for `project.render` and
every implementation has to be added by the user.
`project.render` defines a way to convert a given project
(that often is the source code of a program)
to a file system
(that most of the time is an artifact for the user).
By framing the functionality in this way,
it is clear, that `project.render` is basically a build system.
It is a very basic one,
but in the end of the day, it is one.

`project.render` could be used in order to compile any kind of source code.
In order to support multiple programming languages,
one could provide one implementation for each language, for example:
* Create a version, that takes a Java Maven Project and creates a runnable jar.
* Create a version, that takes a C project and builds an executable binary.

In order to support different types of input
(Java Maven projects and C projects)
each `project.render` implementation needs to detect the type of the input
project.
For the Java instance, one could check for the existence of a `pom.xml` file
and some additional more unique things.

In order to support different types of output
(building an executable jar versus building the project's documentation),
one needs some marker at the target project.
Each `project.render` implementation would have to check these markers,
in order to confirm,
if the implementation in question can construct the requested artifact,
that fits to the output project.

For instance, one could have a `.type` file in the target folder,
that consists of the string `executable` or `documentation`.
This file could than determine,
if the resulting artifact is an executable jar or a website containing
the project's documentation.

Alternatively, some input projects may inherently produce only one type
of output project.
# Split Cells Network Usage
I'm using `project.render` in order to document an abstract concept,
that I probably will use in the future.

This concept was used in order to create
`project.render.as.net.splitcells.website`,
which adds support for arbitrary website renderers to my website server
(`net.splitcells.website.server`).
This command is very similar to `project.render`,
with one exception:
the input project is always the current folder and the implementation
is delegated to a project command.

When one calls `project.render.as.net.splitcells.website.server <output folder>`,
`./bin/render.as.net.splitcells.website.to <output folder>` is executed.
As a demonstration, let's have a look at the current implementation for my
private website:
```
#!/usr/bin/env sh
# Render website via Java web server.
    mvn compile
    mvn exec:java -Dexec.mainClass='net.splitcells.website.martins.avots.StaticFileServer'
# Render Hugo blog.
    currentFolder=$(pwd)
    cd ~/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects/net.splitcells.network.blog
	    project.render.as.net.splitcells.website --to-project=$(realpath $currentFolder/target/public)
```

The result is a website, that is a combination of 2 other websites.
The second part of the showed example can be viewed [here](http://splitcells.net/net/splitcells/network/blog/site/index.html).
Basically, `project.render` and derived commands are used in order to
standardize the usage of different build commands and build system via a unified
API,
that is very primitive in order to be able to ignore implementation specifics.
# Conclusion
The main point of focus for the API is the filesystem.
Every build is viewed an integration of an input filesystem into an output
filesystem, which adhere to a common layout.

This API should make it possible to use arbitrary software for website
generation and provides the tools in order to abandon my custom Java web server,
in case I realize,
that there is a better option.
The API also shows the way,
how to migrate to a different software,
without breaking everything during the migration.
