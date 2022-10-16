package net.splitcells.cin;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Environment;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Cin {
    private Cin() {
        throw constructorIllegal();
    }

    public static void configure(Environment env) {
        env.config().configValue(Databases.class)
                .withConnector(database -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                    @Override
                    public String render() {
                        return database.toHtmlTable().toHtmlString();
                    }

                    @Override
                    public Optional<String> title() {
                        return Optional.of(database.path().toString());
                    }

                    @Override
                    public List<String> path() {
                        return database.path();
                    }
                }));
        env.config().configValue(Solutions.class)
                .withConnector(solution -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                    @Override
                    public String render() {
                        return solution.toHtmlTable().toHtmlString();
                    }

                    @Override
                    public Optional<String> title() {
                        return Optional.of(solution.path().toString());
                    }

                    @Override
                    public List<String> path() {
                        return solution.path();
                    }
                }));
    }
}
