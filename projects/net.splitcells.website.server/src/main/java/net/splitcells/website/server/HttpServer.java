package net.splitcells.website.server;

import net.splitcells.dem.resource.host.Files;

import java.nio.file.Paths;

import static net.splitcells.website.Projects.projectsRenderer;

public class HttpServer {
    public static void main(String... args) {
        final var target = Paths.get("target/public").toAbsolutePath();
        Files.createDirectory(target);
        projectsRenderer().serveToHttpAt(8443);
        projectsRenderer().serveAsAuthenticatedHttpsAt(8080);
    }
}
