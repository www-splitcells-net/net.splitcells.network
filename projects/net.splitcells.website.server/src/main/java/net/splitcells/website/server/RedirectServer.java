/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.website.server.config.HttpPort;

import static net.splitcells.dem.Dem.configValue;

/**
 * Redirects HTTP calls to HTTPS.
 */
public class RedirectServer implements Option<Service> {

    @Override
    public Service defaultValue() {
        return new Service() {
            Vertx vertx;

            @Override
            public void start() {
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

            @Override
            public void close() {
                vertx.close();
            }

            @Override
            public void flush() {
                // Nothing needs to be done.
            }
        };
    }
}
