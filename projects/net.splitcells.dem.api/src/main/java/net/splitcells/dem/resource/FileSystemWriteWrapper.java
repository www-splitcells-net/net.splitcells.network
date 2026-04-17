/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class FileSystemWriteWrapper implements FileSystem {
    public static FileSystem fileSystemWriteWrapper(FileSystemView view) {
        return new FileSystemWriteWrapper(view);
    }

    private final FileSystemView view;
    private boolean isUsageWarned = false;

    private FileSystemWriteWrapper(FileSystemView argView) {
        view = argView;
    }

    private void warnUsage() {
        if (!isUsageWarned) {
            logs().warnUnimplementedPart(FileSystemWriteWrapper.class);
            isUsageWarned = true;
        }
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem appendToFile(Path path, byte[] content) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        return fileSystemWriteWrapper(view.subFileSystemView(path.toString()));
    }

    @Override
    public FileSystem delete(String path) {
        warnUsage();
        return this;
    }

    @Override
    public InputStream inputStream(Path path) {
        return view.inputStream(path);
    }

    @Override
    public String readString(Path path) {
        return view.readString(path);
    }

    @Override
    public boolean exists() {
        return view.exists();
    }

    @Override
    public boolean isFile(Path path) {
        return view.isFile(path);
    }

    @Override
    public boolean isDirectory(Path path) {
        return view.isDirectory(path);
    }

    @Override
    public Stream<Path> walkRecursively() {
        return view.walkRecursively();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        return view.walkRecursively(path);
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        return view.readFileAsBytes(path);
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        return view.subFileSystemView(path);
    }
}
