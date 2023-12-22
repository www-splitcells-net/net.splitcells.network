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
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.Formats;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;
import net.splitcells.website.server.processor.BinaryMessage;

import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.log.LogLevel.WARNING;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.BinaryUtils.binaryOutputStream;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.Request.request;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * TODO Create and use server interface, instead of implementation.
 */
@JavaLegacyArtifact
public class Server {

    private Server() {
        throw constructorIllegal();
    }

    /**
     * TODO This is code duplication.
     *
     * @param renderer renderer
     */
    public static Service serveToHttpAt(Function<String, Optional<BinaryMessage>> renderer, Config config) {
        return new Service() {
            Vertx vertx;

            @Override
            public void flush() {

            }

            @Override
            public void close() {
                vertx.close();
            }

            @Override
            public void start() {
                System.setProperty("vertx.disableFileCaching", "true");
                System.setProperty("vertx.maxEventLoopExecuteTime", "1000000");
                System.setProperty("log4j.rootLogger", "DEBUG, stdout");
                vertx = Vertx.vertx(new VertxOptions()
                        .setMaxEventLoopExecuteTimeUnit(SECONDS)
                        .setMaxEventLoopExecuteTime(60L)
                        .setBlockedThreadCheckInterval(60_000L))
                ;
                final var deploymentOptions = new DeploymentOptions()
                        .setMaxWorkerExecuteTimeUnit(SECONDS)
                        .setMaxWorkerExecuteTime(60L);
                final var binaryProcessor = new Processor<Perspective, Perspective>() {
                    @Override
                    public synchronized Response<Perspective> process(Request<Perspective> request) {
                        return config.processor().process(request);
                    }
                };
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
                            logs().append(perspective("Webserver is not secured!"), WARNING);
                        }
                        webServerOptions.setMaxFormAttributeSize(100_000_000);
                        webServerOptions.setPort(config.openPort());
                        final var router = Router.router(vertx);
                        router.route("/favicon.ico").handler(a -> {
                        });
                        router.route("/*").handler(routingContext -> {
                            HttpServerResponse response = routingContext.response();
                            if (routingContext.request().path().endsWith(".form")) {
                                routingContext.response().setChunked(true);
                                routingContext.request().setExpectMultipart(true);
                            }
                            if (routingContext.request().isExpectMultipart()) {
                                routingContext.request().endHandler(voidz -> {
                                    vertx.<byte[]>executeBlocking((promise) -> {
                                        final var binaryRequest = parseBinaryRequest(routingContext.request().path()
                                                , routingContext.request().formAttributes());
                                        logs().append(perspective("Processing web server binary request.")
                                                        .withProperty("Binary request", binaryRequest.data())
                                                , LogLevel.DEBUG);
                                        final var binaryResponse = binaryProcessor
                                                .process(binaryRequest);
                                        response.putHeader("content-type", Formats.JSON.mimeTypes());
                                        promise.complete(toBytes(binaryResponse.data().createToJsonPrintable()
                                                .toJsonString()));
                                    }, (result) -> handleResult(routingContext, result));
                                });
                            } else {
                                vertx.<byte[]>executeBlocking((promise) -> {
                                    try {
                                        final String requestPath;
                                        if ("".equals(routingContext.request().path()) || "/".equals(routingContext.request().path())) {
                                            requestPath = "index.html";
                                        } else {
                                            requestPath = routingContext.request().path();
                                        }
                                        logs().append(perspective("Processing web server rendering request.")
                                                        .withProperty("Raw request path", routingContext.request().path())
                                                        .withProperty("Interpreted request path", requestPath)
                                                , LogLevel.DEBUG);
                                        final var result = renderer.apply(requestPath);
                                        if (result.isPresent()) {
                                            response.putHeader("content-type", result.get().getFormat());
                                            promise.complete(result.get().getContent());
                                        } else {
                                            promise.fail("Could not render path:" + requestPath);
                                        }
                                    } catch (Exception e) {
                                        logs().appendError(e);
                                        throw new RuntimeException(e);
                                    }
                                }, (result) -> handleResult(routingContext, result));
                            }
                        });
                        router.errorHandler(500, e -> {
                            logs().appendError(e.failure());
                        });
                        final var server = vertx.createHttpServer(webServerOptions);//
                        server.requestHandler(router);//
                        server.listen();
                    }
                }, deploymentOptions);
            }
        };
    }

    private static void handleResult(RoutingContext routingContext, AsyncResult<byte[]> result) {
        final var response = routingContext.response();
        if (result.failed()) {
            logs().appendError(executionException(perspective("Could not process form:")
                            .withProperty("path", routingContext.request().path())
                    , result.cause()));
            response.setStatusCode(500);
            response.end();
        } else {
            response.end(Buffer.buffer().appendBytes(result.result()));
        }
    }

    private static Request<Perspective> parseBinaryRequest(String path, MultiMap multiMap) {
        final var pathSplit = Lists.listWithValuesOf(path.split("/"));
        if (!pathSplit.isEmpty() && "".equals(pathSplit.get(0))) {
            pathSplit.removeAt(0);
        }
        final var requestData = perspective("");
        multiMap.entries().forEach(entry -> {
            requestData.withProperty(entry.getKey(), entry.getValue());
        });
        final var binaryRequest = request(trail(pathSplit), requestData);
        return binaryRequest;
    }

    /**
     * TODO The handlers are out of date. Use the same handlers as {@link #serveToHttpAt(Function, Config)}.
     *
     * @param renderer
     * @param config
     * @return
     */
    @Deprecated
    public static Service serveAsAuthenticatedHttpsAt(Function<String, Optional<BinaryMessage>> renderer, Config config) {
        return new Service() {
            Vertx vertx;

            @Override
            public void start() {
                System.setProperty("vertx.disableFileCaching", "true");
                System.setProperty("log4j.rootLogger", "DEBUG, stdout");
                vertx = Vertx.vertx();
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
                                .setPort(config.openPort())
                                .setMaxFormAttributeSize(100_000_000);
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
                                    logs().appendError(result.cause());
                                    response.setStatusCode(500);
                                    response.end();
                                } else {
                                    response.end(Buffer.buffer().appendBytes(result.result()));
                                }
                            });
                        });
                        router.errorHandler(500, e -> {
                            logs().appendError(e.failure());
                        });
                        final var server = vertx.createHttpServer(webServerOptions);//
                        server.requestHandler(router);//
                        server.listen();
                    }
                });
            }

            @Override
            public void close() {
                vertx.close();
            }

            @Override
            public void flush() {

            }
        };
    }
}
