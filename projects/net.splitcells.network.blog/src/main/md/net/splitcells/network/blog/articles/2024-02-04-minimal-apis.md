----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Preferring minimal APIs.
Keeping an API minimal is not considered the best solution in this project.
While it is preferred,
that the core functionality of a module should have a minimal number of methods,
many use cases of an API are a red flag and should be discouraged.
Unfortunately, methods supporting red flag use cases can not always avoided by an API,
because often only the context determines, if the use case is bad or not.
## Java's Path API
For instance, `someFolder.resolve('/anotherFolder/.python_history-28715.tmp')` is incorrect,
with an increased likely hood,
because resolving a path with a absolute path will return the absolute path unchanged.
It is very likely, that in this example the first slash of the path is incorrect,
or that the method call `resolve` itself is not suitable for the task.
In the latter case, `Path.of('/anotherFolder/.python_history-28715.tmp')` should be used instead.

If resolving an absolute path is a required feature, 2 methods would be recommended:
* A strict method supporting only the resolution of a given relative path,
  which is correct most of the time in this project.
  It should have a concise name like `resolve`.
* A loose method, accepting absolute and relative paths, with a longer descriptive name,
  like `resolveGlobally`.
  Such a method can be useful for resolving a path given in a command line tool (CLI).

This way, developers are more likely to use the stricter version by default,
thereby avoiding bugs.
If a given usage of `resolve` is false,
it is very easy to change the call to `resolveGlobally`.

If `resolveGlobally` is used,
junior and experience developers alike will better see,
that a special case is present as the method name is more chatty.
It hints to the fact, that something non obvious might happen.

Also note, that moving from `resolve` to `resolveGlobally` is far easier,
than the other way around.
Making a method more strict is far more likely to trigger new bugs in the short term,
than the other way around.
## Java's Optional
[Some](https://youtu.be/oRzt0rBsyUs?t=1704) consider the methods `of` and `ofNullable` in Java's Optional redundant.

In this project's context such a method splitting is considered correct.
Java's Optional is often used in order to execute some feature optionally,
while the data triggered by such sometimes come from sources using nulls.

Consider `getClass().getResource('.bashrc')` for example.
It returns null if the resource does not exist.
The following usage `process([...], Optional.of(getClass().getResource('.bashrc')))`
tells the reader, that `process` optionally can process a resource,
but the caller requires the resource to be processed.

If `of` would not throw an exception,
`process` would be executed without an error message and the execution would be erroneous.
In other words, a silent error would be present.

Using `ofNullable` would signal the reader,
that the resource's processing is truly optional.

Alternatively, one might use explicit null checks,
but this is considered a bad practice in this project,
as it can be hard to understand,
if a given variable can be null or not.
Instead, it is easier to check all variables for null,
but that leads to a lot of code noise.

Prohibiting nulls instead is easier,
although it can require to guard external APIs via wrapper methods.