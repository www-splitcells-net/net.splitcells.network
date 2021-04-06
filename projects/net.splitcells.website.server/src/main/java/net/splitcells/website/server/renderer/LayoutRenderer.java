package net.splitcells.website.server.renderer;

import com.google.common.collect.Streams;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.NATURAL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.website.Projects.projectsRenderer;

public class LayoutRenderer {
    public static void main(String... args) throws Exception {
        // TODO privateProjectsRenderer().build();
        projectsRenderer().build();
        final var layout = perspective("layout", NATURAL);
        Streams.concat(
                Files.walk(Paths.get("../net.splitcells.os.state.interface/src/main/bash"))
                        .filter(Files::isRegularFile)
                , Files.walk(Paths.get("../net.splitcells.os.state.interface/src/main/python"))
                        .filter(Files::isRegularFile))
                .sorted()
                .forEach(file -> extend(layout, list(file.toFile().getName().split("\\."))));
        System.out.println(Paths.get(System.getProperty("user.home"))
                .resolve("Documents/projects/net.splitcells.martins.avots.support.system/private")
                .resolve("net.splitcells.martins.avots.support.system/src/main/")
                .resolve("xml/net/splitcells/martins/avots/support/system/layout.xml"));
        net.splitcells.dem.resource.host.Files.writeToFile(
                Paths.get(System.getProperty("user.home"))
                        .resolve("Documents/projects/net.splitcells.martins.avots.support.system/private")
                        .resolve("net.splitcells.martins.avots.support.system/src/main/")
                        .resolve("xml/net/splitcells/martins/avots/support/system/layout.xml")
                , Xml.toPrettyString(perspective("", NameSpaces.NATURAL)
                        .withChild(layout).toDom()));
    }

    public static void extend(Perspective perspective, List<String> path) {
        if (path.isEmpty()) {
            return;
        }
        final Perspective nextChild = perspective
                .childNamed(path.get(0), NATURAL)
                .orElseGet(() -> {
                    final var nextChild2 = perspective(path.get(0), NATURAL);
                    perspective.withChild(nextChild2);
                    return nextChild2;
                });
        path.remove(0);
        extend(nextChild, path);
    }
}