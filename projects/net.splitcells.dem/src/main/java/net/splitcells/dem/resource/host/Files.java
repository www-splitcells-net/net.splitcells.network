package net.splitcells.dem.resource.host;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.dem.lang.Xml.toPrettyString;

/**
 * Some additional methods for the java.nio.file.Files class.
 */
public interface Files {

    static void createDirectory(Path directory) {
        try {
            createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static void ensureAbsence(Path path) {
        if (isDirectory(path)) {
            deleteDirectory(path);
        }
    }

    static boolean is_file(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }

    static Stream<Path> walk_recursively(Path path) {
        try {
            return java.nio.file.Files.walk(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }

    /**
     * IDEA Create method to move path to trash instead of deleting it.
     */
    static void deleteDirectory(Path dirToDelete) {
        try {
            FileUtils.deleteDirectory(dirToDelete.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void copyDirectory(Path sourceDirectory, Path targetDirectory) {
        try {
            FileUtils.copyDirectory(sourceDirectory.toFile(), targetDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(Path path, Node node) {
        writeToFile(path, toPrettyString(node));
    }

    static void writeToFile(Path path, byte[] content) {
        try (final var writer = new FileOutputStream(path.toFile())) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    static void writeToFile(Path path, String string) {
        writeToFile(path, string.getBytes());
    }
}
