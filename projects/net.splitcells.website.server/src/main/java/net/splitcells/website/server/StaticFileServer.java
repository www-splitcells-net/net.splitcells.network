package net.splitcells.website.server;

import net.splitcells.dem.resource.host.Files;

import java.nio.file.Paths;

import static net.splitcells.website.server.renderer.ProjectsRenderer.publicProjectsRenderer;

public class StaticFileServer {
    public static void main(String... args) {
        final var target = Paths.get("target/public").toAbsolutePath();
        Files.createDirectory(target);
        publicProjectsRenderer().publicProjectsRenderer().serveTo(target);
    }
}
