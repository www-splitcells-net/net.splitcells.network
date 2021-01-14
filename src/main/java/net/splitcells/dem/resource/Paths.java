package net.splitcells.dem.resource;

import com.google.common.io.Files;
import net.splitcells.dem.utils.ConstructorIllegal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.asList;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public final class Paths {
    private static final Pattern PATH_ELEMENT_SYNTAX = Pattern.compile("[a-zA-Z0-9\\-\\.-_]*");

    private Paths() {
        throw new ConstructorIllegal();
    }

    public static Path path(String root, String... elements) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(root).matches(PATH_ELEMENT_SYNTAX);
            asList(elements).forEach(element -> assertThat(element).matches(PATH_ELEMENT_SYNTAX));
        }
        return java.nio.file.Paths.get(root, elements);
    }
    
    public static Path userHome(String relativePath) {
        return path(System.getProperty("user.home"), relativePath);
    }

    public static Path path(Path root, String... elements) {
        return path(root, asList(elements));
    }

    public static Path path(Path root, Collection<String> elements) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            elements.forEach(element -> assertThat(element).matches(PATH_ELEMENT_SYNTAX));
        }
        var rVal = root;
        for (final String element : elements) {
            rVal = rVal.resolve(element);
        }
        return rVal;
    }

    public static void copyFileFrom(Path source, Path target) {
        try {
            Files.copy(source.toFile(), target.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateFolderPath(Path targetFolderDescription) {
        try {
            createDirectories(targetFolderDescription);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
