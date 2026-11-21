/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.StreamUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class FileSystemUnionView implements FileSystemView {

    private static final String UNAMBIGUOUS_PATH = "Could find unambiguous file system match.";
    private static final String MATCHES = "matches";
    private static final String PATH = "matches";

    private static final Path ABSOLUTE_TEST = path("test");

    public static FileSystemUnionView fileSystemUnionView(boolean argUnique, List<FileSystemView> fileSystems) {
        return new FileSystemUnionView(argUnique, argUnique, fileSystems);
    }

    public static FileSystemUnionView fileSystemUnionView(boolean argUnique, FileSystemView... fileSystems) {
        return new FileSystemUnionView(argUnique, argUnique, list(fileSystems));
    }

    public static FileSystemUnionView fileSystemUnionView(boolean argUnique, boolean argEnforceUniqueFiles, FileSystemView... fileSystems) {
        return new FileSystemUnionView(argUnique, argEnforceUniqueFiles, list(fileSystems));
    }

    private final List<FileSystemView> fileSystems;
    private final boolean uniqueFiles;
    private final boolean enforceUniqueFiles;
    private final Path basePath;

    private FileSystemUnionView(boolean argUnique, boolean argEnforceUniqueFiles, List<FileSystemView> fileSystemsArg) {
        this(argUnique, argEnforceUniqueFiles, fileSystemsArg, java.nio.file.Paths.get("./"));
    }

    private FileSystemUnionView(boolean argUnique, boolean argEnforceUniqueFiles, List<FileSystemView> fileSystemsArg, Path argBasePath) {
        fileSystems = fileSystemsArg;
        uniqueFiles = argUnique;
        basePath = argBasePath;
        enforceUniqueFiles = argEnforceUniqueFiles;
    }

    /**
     * Resolves the given path relative to {@link #basePath} and
     * ensures, that the result is {@link #basePath} itself or one of its children.
     *
     * @param path This is a path relative to {@link #basePath}.
     * @return The normalized path relative to the root of {@link #fileSystems}, if it is located inside {@link #basePath}.
     */
    private Optional<Path> resolveInside(Path path) {
        if (path.isAbsolute()) {
            throw execException(tree("Path has to be a child of the base path.")
                    .withProperty("base path", basePath.toString())
                    .withProperty("path", path.toString()));
        }
        final var base = basePath.normalize();
        final var resolved = base.resolve(path).normalize();
        if (base.toString().isEmpty()) {
            if (!ABSOLUTE_TEST.resolve(path).normalize().startsWith(ABSOLUTE_TEST)) {
                throw execException(tree("Path has to be a child of the base path.")
                        .withProperty("base path", basePath.toString())
                        .withProperty("path", path.toString()));
            }
        } else if (!resolved.startsWith(base)) {
            throw execException(tree("Path has to be a child of the base path.")
                    .withProperty("base path", basePath.toString())
                    .withProperty("path", path.toString()));
        }
        return Optional.of(resolved);
    }

    private Path requireInside(Path path) {
        return resolveInside(path).orElseThrow(() -> execException(tree("The given path has to be relative and located inside the base path.")
                .withProperty("path", path.toString())
                .withProperty("base path", basePath.toString())));
    }

    private FileSystemView getFileSystemWithExistingFile(Path adjustedPath) {
        final var matches = fileSystems.stream()
                .filter(f -> f.isFile(adjustedPath))
                .collect(toList());
        if (uniqueFiles && matches.size() != 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, adjustedPath.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (enforceUniqueFiles) {
                throw exception;
            }
        }
        return matches.get(0);
    }

    @Override
    public InputStream inputStream(Path path) {
        val adjustedPath = requireInside(path);
        return getFileSystemWithExistingFile(adjustedPath).inputStream(adjustedPath);
    }

    @Override
    public String readString(Path path) {
        val adjustedPath = requireInside(path);
        return getFileSystemWithExistingFile(adjustedPath).readString(adjustedPath);
    }

    @Override
    public boolean exists() {
        final var matches = fileSystems.stream()
                .filter(FileSystemView::exists)
                .collect(toList());
        return matches.hasElements();
    }

    @Override
    public boolean isFile(Path path) {
        val resolvedPath = resolveInside(path);
        if (resolvedPath.isEmpty()) {
            return false;
        }
        val adjustedPath = resolvedPath.get();
        final var matches = fileSystems.stream()
                .map(f -> f.isFile(adjustedPath))
                .filter(f -> f)
                .collect(toList());
        if (uniqueFiles && matches.size() > 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (enforceUniqueFiles) {
                throw exception;
            }
        }
        return matches.hasElements();
    }

    @Override
    public boolean isDirectory(Path path) {
        val resolvedPath = resolveInside(path);
        if (resolvedPath.isEmpty()) {
            return false;
        }
        val adjustedPath = resolvedPath.get();
        final var matches = fileSystems.stream()
                .map(f -> f.isDirectory(adjustedPath))
                .filter(f -> f)
                .collect(toList());
        if (uniqueFiles && matches.size() > 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (enforceUniqueFiles) {
                throw exception;
            }
        }
        return matches.hasElements();
    }

    @Override
    public Stream<Path> walkRecursively() {
        final var walks = fileSystems.stream()
                .map(f -> f.walkRecursively(basePath))
                .collect(toList());
        if (walks.isEmpty()) {
            throw execException(tree("Given directory does not exist and therefore cannot be walked.")
                    .withProperty("basePath", basePath.toString())
                    .withProperty("file system", this.toString()));
        }
        return StreamUtils.concat(walks);
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        val adjustedPath = requireInside(path);
        final var walks = fileSystems.stream()
                .filter(f -> f.isDirectory(adjustedPath))
                .map(f -> f.walkRecursively(adjustedPath))
                .collect(toList());
        if (walks.isEmpty()) {
            throw execException(tree("Given directory does not exist and therefore cannot be walked.")
                    .withProperty("path", path.toString())
                    .withProperty("basePath", basePath.toString())
                    .withProperty("file system", this.toString()));
        }
        return StreamUtils.concat(walks);
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        val adjustedPath = requireInside(path);
        return getFileSystemWithExistingFile(adjustedPath).readFileAsBytes(adjustedPath);
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        return new FileSystemUnionView(uniqueFiles, enforceUniqueFiles, fileSystems, requireInside(Path.of(path)));
    }

    @Override public License license(String path) {
        return fileSystems.stream()
                .map(fs -> fs.license(requireInside(Path.of(path))))
                .filter(License::hasInfo)
                .findFirst()
                .orElse(License.license());
    }
}
