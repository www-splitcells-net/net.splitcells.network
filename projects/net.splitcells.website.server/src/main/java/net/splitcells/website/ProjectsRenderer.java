package net.splitcells.website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsRenderer {

    public static ProjectsRenderer publicProjectsRenderer() {
        final var userHome = Paths.get(System.getProperty("user.home"));
        final var projectRepositories = userHome.resolve("Documents/projects/net.splitcells.martins.avots.support.system/private");
        final var profile = "public";
        return projectsRenderer(profile, fallbackProjectRenderer(profile, projectRepositories), publicProjectsRenderer(profile, projectRepositories));
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories) {
        return new ProjectRenderer(profile
                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/")
                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                , "/");
    }

    public static List<ProjectRenderer> publicProjectsRenderer(String profile, Path projectRepositories) {
        return list
                (new ProjectRenderer
                                (profile,
                                        projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/net/splitcells/martins/avots/website")
                        , new ProjectRenderer
                                (profile
                                        , projectRepositories.resolve("..").resolve("public/net.splitcells.network/projects/net.splitcells.dem/src/main/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/net/splitcells/dem")
                        , new ProjectRenderer
                                (profile
                                        , projectRepositories.resolve("..").resolve("public/net.splitcells.network/projects/net.splitcells.gel.doc/src/main/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/net/splitcells/gel")
                        , new ProjectRenderer
                                (profile
                                        , projectRepositories.resolve("..").resolve("public/net.splitcells.network/projects/net.splitcells.system/src/main/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/net/splitcells")
                        , new ProjectRenderer
                                (profile
                                        , projectRepositories.resolve("..").resolve("public/net.splitcells.network/src/main/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/net/splitcells/network")
                );
    }

    public static ProjectsRenderer projectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        return new ProjectsRenderer(name, fallbackRenderer, renderers);
    }

    public void build() {
        final var generatedFiles = Paths.get("target", "generated");
        Files.createDirectory(generatedFiles);
        writeToFile(generatedFiles.resolve("layout." + profile + ".xml"), projectsLayout().toDom());
        generateFolderPath(Paths.get("target", "generated"));
    }

    public void serveTo(Path target) {
        build();
        projectsPaths().stream()
                .map(path -> "/" + path.toString())
                .map(path -> {
                    if (path.endsWith(".xml")) {
                        return path.substring(0, path.length() - 4) + ".html";
                    }
                    return path;
                })
                .forEach(path -> {
                    final var targetPath = path(target, path.substring(1));
                    createDirectory(targetPath.getParent());
                    writeToFile(targetPath, render(path).getContent());
                });
    }

    public void serveToHttpAt(int port, boolean useHttps) {
        build();
        {
            System.setProperty("vertx.disableFileCaching", "true");
            System.setProperty("log4j.rootLogger", "DEBUG, stdout");
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new AbstractVerticle() {
                @Override
                public void start() {
                    // TODO Errors are not logged.
                    final var webServerOptions = new HttpServerOptions()//
                            .setLogActivity(true)//
                            .setSsl(useHttps)//
                            .setKeyCertOptions(new PfxOptions().setPath("src/main/resources.private/keystore.p12").setPassword("password"))//
                            .setTrustOptions(new PfxOptions().setPath("src/main/resources.private/keystore.p12").setPassword("password"))//
                            .setPort(port);//
                    final var router = Router.router(vertx);
                    router.route("/favicon.ico").handler(a -> {
                    });
                    router.route("/*").handler(routingContext -> {
                        HttpServerResponse response = routingContext.response();
                        vertx.<byte[]>executeBlocking((promise) -> {
                            final String requestPath;
                            if ("".equals(routingContext.request().path()) || "/".equals(routingContext.request().path())) {
                                requestPath = "index.html";
                            } else {
                                requestPath = routingContext.request().path();
                            }
                            final var result = render(requestPath);
                            response.putHeader("content-type", result.getFormat());
                            promise.complete(result.getContent());
                        }, (result) -> {
                            if (result.failed()) {
                                result.cause().printStackTrace();
                                response.setStatusCode(500);
                                response.end();
                            } else {
                                response.end(Buffer.buffer().appendBytes(result.result()));
                            }
                        });
                    });
                    router.errorHandler(500, e -> {
                        e.failure().printStackTrace();
                    });
                    final var server = vertx.createHttpServer(webServerOptions);//
                    server.requestHandler(router);//
                    server.listen();
                }
            });
        }
    }

    public void serveAsAuthenticatedHttpsAt(int port) {
        build();
        {
            System.setProperty("vertx.disableFileCaching", "true");
            System.setProperty("log4j.rootLogger", "DEBUG, stdout");
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new AbstractVerticle() {
                @Override
                public void start() {
                    // TODO Errors are not logged.
                    final var webServerOptions = new HttpServerOptions()//
                            .setSsl(true)//
                            .setKeyCertOptions(new PfxOptions().setPath("src/main/resources.private/keystore.p12").setPassword("password"))//
                            .setTrustOptions(new PfxOptions().setPath("src/main/resources.private/keystore.p12").setPassword("password"))//
                            .setClientAuth(ClientAuth.REQUIRED)//
                            .setLogActivity(true)//
                            .setPort(port);//
                    final var router = Router.router(vertx);
                    router.route("/favicon.ico").handler(a -> {
                    });
                    router.route("/*").handler(routingContext -> {
                        HttpServerResponse response = routingContext.response();
                        vertx.<byte[]>executeBlocking((promise) -> {
                            final String requestPath;
                            if ("".equals(routingContext.request().path()) || "/".equals(routingContext.request().path())) {
                                requestPath = "index.html";
                            } else {
                                requestPath = routingContext.request().path();
                            }
                            final var result = render(requestPath);
                            response.putHeader("content-type", result.getFormat());
                            promise.complete(result.getContent());
                        }, (result) -> {
                            if (result.failed()) {
                                result.cause().printStackTrace();
                                response.setStatusCode(500);
                                response.end();
                            } else {
                                response.end(Buffer.buffer().appendBytes(result.result()));
                            }
                        });
                    });
                    router.errorHandler(500, e -> {
                        e.failure().printStackTrace();
                    });
                    final var server = vertx.createHttpServer(webServerOptions);//
                    server.requestHandler(router);//
                    server.listen();
                }
            });
        }
    }

    public static void main(String... args) {
        // TODO privateProjectsRenderer().serveAsAuthenticatedHttpsAt(8444);
        publicProjectsRenderer().serveToHttpAt(8443, true);
        publicProjectsRenderer().serveToHttpAt(8080, false);
    }

    private final String profile;
    private final Map<String, ProjectRenderer> renderers = map();
    private final ProjectRenderer fallbackRenderer;

    private ProjectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        this.profile = name;
        this.fallbackRenderer = fallbackRenderer;
        for (var renderer : renderers) {
            this.renderers.put(renderer.resourceRootPath(), renderer);
        }
    }

    public RenderingResult render(String path) {
        final var matchingRoots = renderers.keySet().stream().filter(root -> path.startsWith(root)).collect(toList());
        if (matchingRoots.isEmpty()) {
            // System.out.println("No match for: " + path);
            //System.out.println("Patterns: " + renderers.keySet());
            return fallbackRenderer.render(path);
        }
        // System.out.println("Match for: " + path);
        // System.out.println("Match on: " + matchingRoots.get(0));
        matchingRoots.sort((a, b) -> Integer.valueOf(a.length()).compareTo(b.length()));
        matchingRoots.reverse();
        return renderers.get(matchingRoots.get(0)).render(path);
    }

    public Set<Path> projectsPaths() {
        return renderers.values().stream()
                .map(renderer -> renderer.projectPaths())
                .reduce((a, b) -> a.with(b)).get();
    }

    public Perspective projectsLayout() {
        final var layout = perspective(VAL, NATURAL);
        renderers.values().forEach(renderer -> {
            var current = layout;
            for (final var element : list(renderer.resourceRootPath().split("/")).stream().filter(e -> !"".contentEquals(e)).collect(toList())) {
                final var children = s(current, element);
                final Perspective child;
                if (children.isEmpty()) {
                    child = perspective(VAL, NATURAL)
                            .withProperty(NAME, NATURAL, element);
                    current.withChild(child);
                } else {
                    child = children.get(0);
                }
                current = child;
            }
            if (!renderer.projectLayout().children().isEmpty()) {
                layout.withPath(renderer.projectLayout().children().get(0), NAME, NATURAL);
            }
        });
        layout.withProperty(NAME, NATURAL, "Path Context");
        return layout;
    }

    private List<Perspective> s(Perspective current, String element) {
        final var children = current.children().stream()
                .filter(child -> child.nameIs(VAL, NATURAL))
                .filter(child -> child.propertyInstances(NAME, NATURAL).stream()
                        .anyMatch(property -> property.value().get().name().equals(element)))
                .collect(toList());
        assertThat(children).hasSizeLessThan(2);
        return children;
    }
}
