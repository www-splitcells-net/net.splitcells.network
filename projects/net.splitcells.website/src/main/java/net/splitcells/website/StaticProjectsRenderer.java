package net.splitcells.website;

import net.splitcells.dem.resource.host.Files;

import java.nio.file.Paths;

//import static net.splitcells.website.ProjectsRenderer.publicProjectsRenderer;

public class StaticProjectsRenderer {
    public static void main(String... args) {
        final var target = Paths.get("target/public").toAbsolutePath();
        Files.createDirectory(target);
        //publicProjectsRenderer().serveTo(target);
    }
}
