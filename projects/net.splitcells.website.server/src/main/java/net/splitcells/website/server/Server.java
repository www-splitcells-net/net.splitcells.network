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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.WARNING;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;

/**
 * TODO Create and use server interface, instead of implementation.
 */
@JavaLegacyArtifact
public class Server {
    /**
     * TODO This is code duplication.
     *
     * @param renderer renderer
     */
    public void serveToHttpAt(Function<String, Optional<RenderingResult>> renderer, Config config) {
        {
            System.setProperty("vertx.disableFileCaching", "true");
            System.setProperty("vertx.maxEventLoopExecuteTime", "1000000");
            System.setProperty("log4j.rootLogger", "DEBUG, stdout");
            Vertx vertx = Vertx.vertx(new VertxOptions()
                    .setMaxEventLoopExecuteTimeUnit(SECONDS)
                    .setMaxEventLoopExecuteTime(60L)
                    .setBlockedThreadCheckInterval(60_000L));
            final var deploymentOptions = new DeploymentOptions()
                    .setMaxWorkerExecuteTimeUnit(SECONDS)
                    .setMaxWorkerExecuteTime(60L);
            vertx.deployVerticle(new AbstractVerticle() {
                @Override
                public void start() {
                    // TODO Errors are not logged.
                    final var webServerOptions = new HttpServerOptions();
                    if (config.isSecured()) {
                        webServerOptions.setLogActivity(true)//
                                .setSsl(true)//
                                .setKeyCertOptions(new PfxOptions()
                                        .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                        .setPassword(config.sslKeystorePassword().orElseThrow()))
                                .setTrustOptions(new PfxOptions()
                                        .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                        .setPassword(config.sslKeystorePassword().orElseThrow()));
                    } else {
                        domsole().append(perspective("Webserver is not secured!"), WARNING);
                    }
                    webServerOptions.setPort(config.openPort());
                    final var router = Router.router(vertx);
                    router.route("/favicon.ico").handler(a -> {
                    });
                    router.route("/*").handler(routingContext -> {
                        HttpServerResponse response = routingContext.response();
                        vertx.<byte[]>executeBlocking((promise) -> {
                            try {
                                final String requestPath;
                                if ("".equals(routingContext.request().path()) || "/".equals(routingContext.request().path())) {
                                    requestPath = "index.html";
                                } else {
                                    requestPath = routingContext.request().path();
                                }
                                final var result = renderer.apply(java.net.URLDecoder.decode(requestPath, StandardCharsets.UTF_8.name()));
                                if (result.isPresent()) {
                                    response.putHeader("content-type", result.get().getFormat());
                                    promise.complete(result.get().getContent());
                                } else {
                                    promise.fail("Could not render path:" + requestPath);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }, (result) -> {
                            if (result.failed()) {
                                domsole().append(perspective(result.cause().toString()), LogLevel.ERROR);
                                domsole().appendError(result.cause());
                                response.setStatusCode(500);
                                response.end();
                            } else {
                                response.end(Buffer.buffer().appendBytes(result.result()));
                            }
                        });
                    });
                    router.errorHandler(500, e -> {
                        domsole().appendError(e.failure());
                    });
                    final var server = vertx.createHttpServer(webServerOptions);//
                    server.requestHandler(router);//
                    server.listen();
                }
            }, deploymentOptions);
        }
    }

    public void serveAsAuthenticatedHttpsAt(Function<String, Optional<RenderingResult>> renderer, Config config) {
        System.setProperty("vertx.disableFileCaching", "true");
        System.setProperty("log4j.rootLogger", "DEBUG, stdout");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                // TODO Errors are not logged.
                final var webServerOptions = new HttpServerOptions()
                        .setSsl(true)
                        .setKeyCertOptions(new PfxOptions()
                                .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                .setPassword(config.sslKeystorePassword().orElseThrow()))
                        .setTrustOptions(new PfxOptions()
                                .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                .setPassword(config.sslKeystorePassword().orElseThrow()))
                        .setClientAuth(ClientAuth.REQUIRED)
                        .setLogActivity(true)
                        .setPort(config.openPort());
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
                        final var result = renderer.apply(requestPath);
                        if (result.isPresent()) {
                            response.putHeader("content-type", result.get().getFormat());
                            promise.complete(result.get().getContent());
                        } else {
                            promise.fail("Could not render path:" + requestPath);
                        }
                    }, (result) -> {
                        if (result.failed()) {
                            domsole().appendError(result.cause());
                            response.setStatusCode(500);
                            response.end();
                        } else {
                            response.end(Buffer.buffer().appendBytes(result.result()));
                        }
                    });
                });
                router.errorHandler(500, e -> {
                    domsole().appendError(e.failure());
                });
                final var server = vertx.createHttpServer(webServerOptions);//
                server.requestHandler(router);//
                server.listen();
            }
        });
    }
}
