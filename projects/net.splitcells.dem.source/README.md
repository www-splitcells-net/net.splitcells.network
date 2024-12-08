# Dependency Manager Source Code Lib

* **TODO**: Further remove usage of allowed external dependencies like `java.nio.file.Path`.
* **TODO**: Check via Java-Parser the usage of standard library things, that do not require an explicit import.
  Some of these need to be disallowed as well,
  as the existing grammar check does not support looking up things in Java's classpath.