/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.website.server.config.HttpPort;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Redirects HTTP calls to HTTPS.
 */
@JavaLegacy
public class RedirectServer implements Option<Service> {

    @Override public Service defaultValue() {
        return new Service() {
            Vertx vertx;

            @Override public void start() {
                vertx = Vertx.vertx();
                var server = vertx.createHttpServer(new HttpServerOptions()
                        .setPort(configValue(HttpPort.class))
                        .setPort(8081)
                        .setSsl(false));
                server.requestHandler(r -> r.response()
                        .setStatusCode(301)
                        .putHeader("Location", "https" + r.absoluteURI().substring(4))
                        .end());
                server.listen(configValue(HttpPort.class));
            }

            @Override public void close() {
                vertx.close();
            }

            @Override public void flush() {
                // Nothing needs to be done.
            }
        };
    }

    @Override public Optional<Tree> serialize(Service currentValue) {
        return Optional.of(tree(getClass() + " is enabled."));
    }
}
