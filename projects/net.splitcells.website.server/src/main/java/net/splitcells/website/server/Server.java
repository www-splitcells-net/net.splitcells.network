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
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.execution.ExplicitEffect;
import net.splitcells.dem.execution.Processing;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.website.Format;
import net.splitcells.website.server.config.PasswordAuthenticationEnabled;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest;
import net.splitcells.website.server.security.access.AccessControl;
import net.splitcells.website.server.security.authentication.UserSession;
import net.splitcells.website.server.security.encryption.PrivateIdentityPemStore;
import net.splitcells.website.server.security.encryption.PublicIdentityPemStore;
import net.splitcells.website.server.security.encryption.SslEnabled;
import net.splitcells.website.server.vertx.DocumentNotFound;

import javax.net.ssl.SSLHandshakeException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.execution.EffectWorkerPool.effectWorkerPool;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.log.LogLevel.ERROR;
import static net.splitcells.dem.resource.communication.log.LogLevel.WARNING;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.Request.request;
import static net.splitcells.website.server.projects.RenderRequest.renderRequest;
import static net.splitcells.website.server.security.access.AccessControlImpl.accessControl;
import static net.splitcells.website.server.security.access.AccessSession.unsecuredStaticAccessSession;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;
import static net.splitcells.website.server.vertx.FileBasedAuthenticationProvider.LOGIN_KEY;
import static net.splitcells.website.server.vertx.FileBasedAuthenticationProvider.fileBasedAuthenticationProvider;

@JavaLegacy
public class Server {

    private Server() {
        throw constructorIllegal();
    }

    /**
     * TODO Create stress test.
     *
     * @param renderer
     * @param config
     * @return
     */
    public static Service serveToHttpAt(Supplier<ProjectsRenderer> renderer, Config config) {
        config.withIsMultiThreaded(true);
        final ProjectsRenderer projectsRenderer = new ProjectsRenderer() {
            final ExplicitEffect<ProjectsRenderer> effect = effectWorkerPool(renderer, 10);

            @Override
            public void build() {
                throw notImplementedYet();
            }

            @Override
            public void serveTo(Path target) {
                throw notImplementedYet();
            }

            @Override
            public Service httpServer() {
                throw notImplementedYet();
            }

            @Override
            public Service authenticatedHttpsServer() {
                throw notImplementedYet();
            }

            @Override
            public Optional<BinaryMessage> render(String path) {
                throw notImplementedYet();
            }

            @Override
            public RenderResponse render(RenderRequest request) {
                final var processing = Processing.<ProjectsRenderer, RenderResponse>processing();
                processing.withArgument(null);
                effect.affect(i -> processing.withResult(i.render(request)));
                return processing.result();
            }

            @Override
            public Optional<BinaryMessage> sourceCode(String path) {
                throw notImplementedYet();
            }

            @Override
            public Set<Path> projectsPaths() {
                throw notImplementedYet();
            }

            @Override
            public Set<Path> relevantProjectsPaths() {
                throw notImplementedYet();
            }

            @Override
            public Config config() {
                throw notImplementedYet();
            }

            @Override
            public net.splitcells.dem.data.set.list.List<ProjectRenderer> projectRenderers() {
                throw notImplementedYet();
            }

            @Override
            public Optional<PageMetaData> metaData(String path) {
                throw notImplementedYet();
            }

            @Override
            public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path, Config config) {
                throw notImplementedYet();
            }

            @Override
            public Optional<BinaryMessage> renderContent(String content, LayoutConfig metaContent) {
                throw notImplementedYet();
            }

            @Override
            public boolean requiresAuthentication(RenderRequest request) {
                final var processing = Processing.<ProjectsRenderer, Boolean>processing();
                processing.withArgument(null);
                effect.affect(i -> processing.withResult(i.requiresAuthentication(request)));
                return processing.result();
            }

            @Override
            public Set<Path> projectPaths(ProjectPathsRequest request) {
                throw notImplementedYet();
            }
        };
        return serveToHttpAt(accessControl(unsecuredStaticAccessSession(projectsRenderer)), config);
    }

    /**
     * <p>TODO Move this code into vertx package, in order to contain {@link io.vertx} dependencies.</p>
     * <p>TODO Always open 2 ports.</p>
     * <p>TODO Why is Vert.x is used at all? Is Netty (the base of Vert.x) or Jetty (more focused on HTTP) for instance not enough?
     * Netty is probably a tad bit more complex to use than Jetty, as Netty focuses more on the low level communication protocols instead of HTTP.
     * Low level communication protocols should be handled by dedicated libraries instead.
     * Especially, because the HTTP protocol is a special case compared to TCP, UDP, FTP and co.
     * At the time of writing 25-04-2025 Jetty already supports HTTP/3 in the base project,
     * whereas in Netty HTTP/3 support seems to be an experimental incubator project.
     * Therefore, Jetty should be the preferred option.
     * Consider removing the need for a logging framework implementation except a interface based logging framework,
     * in order to minimize the config.</p>
     * <p>TODO IDEA Every HTTP login should be indicated in HTTP protocol as valid,
     * in order to make attacks harder.
     * It is unclear, if something like this is useful for normal deployments.</p>
     *
     * @param renderer renderer
     */
    private static Service serveToHttpAt(AccessControl<ProjectsRenderer> renderer, Config config) {
        return new Service() {
            Vertx vertx;

            @Override
            public void flush() {
                // Nothing needs to be done.
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
                                /* TODO The event loop should not be blocked by that much,
                                 * as other users cannot request a page during the blockage.
                                 */
                                .setMaxEventLoopExecuteTimeUnit(SECONDS)
                                .setMaxEventLoopExecuteTime(60L)
                                .setBlockedThreadCheckInterval(60_000L))
                        .exceptionHandler(t -> logs().fail(t));
                // TODO Use own worker pool/executor provided by Dem.
                final var deploymentOptions = new DeploymentOptions()
                        .setWorkerPoolName(Server.class.getName())
                        .setWorkerPoolSize(20)
                        .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)// TODO Limit blocking.
                        .setMaxWorkerExecuteTime(1L);
                // TODO Make binaryProcessor thread safe.
                final var binaryProcessor = new Processor<Tree, Tree>() {
                    @Override
                    public Response<Tree> process(Request<Tree> request) {
                        return config.processor().process(request);
                    }
                };
                final var deployResult = vertx.deployVerticle(new AbstractVerticle() {
                    @Override
                    public void start(Promise<Void> startPromise) {
                        // TODO Errors are not logged.
                        final var webServerOptions = new HttpServerOptions();
                        if (config.isSecured()) {
                            webServerOptions.setSsl(true)//
                                    .setKeyCertOptions(new PfxOptions()
                                            .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                            .setPassword(config.sslKeystorePassword().orElseThrow()))
                                    .setTrustOptions(new PfxOptions()
                                            .setPath(config.sslKeystoreFile().orElseThrow().toString())
                                            .setPassword(config.sslKeystorePassword().orElseThrow()));
                        } else if (configValue(SslEnabled.class)) {
                            webServerOptions.setSsl(true)//
                                    .setPemKeyCertOptions(new PemKeyCertOptions()
                                            .setKeyValue(buffer(configValue(PrivateIdentityPemStore.class)
                                                    .orElseThrow()))
                                            .setCertValue(buffer(configValue(PublicIdentityPemStore.class)
                                                    .orElseThrow())));
                        } else {
                            logs().append(tree("Webserver is not secured!"), WARNING);
                        }
                        webServerOptions.setMaxFormAttributeSize(100_000_000);
                        webServerOptions.setPort(config.openPort());
                        final var router = Router.router(vertx);
                        router.route("/favicon.ico").handler(a -> {
                            /* TODO Nothing needs to be done for now, as this is not supported yet,
                             * but a response is required.
                             */

                        });
                        final var authenticator = BasicAuthHandler.create(fileBasedAuthenticationProvider());
                        final var authenticationEnabled = configValue(PasswordAuthenticationEnabled.class);
                        /* The BodyHandler ensures, that all parts of a multipart request are available
                         * at the next handler in a multi threaded context,
                         * by downloading/receiving all data from the request.
                         */
                        router.route().useNormalizedPath(true)
                                .handler(BodyHandler.create())
                                .handler(new BasicAuthHandler() {
                                    @Override
                                    public void handle(RoutingContext routingContext) {
                                        if (authenticationEnabled) {
                                            renderer.access((u, r) -> {
                                                if (r.requiresAuthentication(renderRequest(trail(requestPath(routingContext))
                                                        , Optional.empty(), u))) {
                                                    authenticator.handle(routingContext);
                                                } else {
                                                    routingContext.next();
                                                }
                                            }, ANONYMOUS_USER_SESSION);
                                        } else {
                                            routingContext.next();
                                        }
                                    }
                                })
                                .handler(routingContext -> {
                                    HttpServerResponse response = routingContext.response();
                                    if (routingContext.request().isExpectMultipart()) {
                                        vertx.<byte[]>executeBlocking(promise -> {
                                                    val requestPath = routingContext.request().path();
                                                    final var binaryRequest = parseBinaryRequest(requestPath
                                                            , routingContext.request().formAttributes());
                                                    logs().append(tree("Processing web server binary request.")
                                                                    .withProperty("Binary request", binaryRequest.data())
                                                            , LogLevel.DEBUG);
                                                    final var binaryResponse = binaryProcessor
                                                            .process(binaryRequest);
                                                    response.putHeader("content-type", Format.JSON.mimeTypes());
                                                    if (binaryResponse.hasData()) {
                                                        promise.complete(toBytes(binaryResponse.data().createToJsonPrintable()
                                                                .toJsonString()));
                                                    } else {
                                                        promise.fail(new DocumentNotFound(requestPath));
                                                    }
                                                }, config.isSingleThreaded()
                                                , result -> handleResult(routingContext, result));
                                    } else {
                                        vertx.<byte[]>executeBlocking(promise -> {
                                                    try {
                                                        final String requestPath = requestPath(routingContext);
                                                        logs().append(tree("Processing web server rendering request.")
                                                                        .withProperty("Raw request path", routingContext.request().path())
                                                                        .withProperty("Interpreted request path", requestPath)
                                                                , LogLevel.DEBUG);
                                                        /* TODO This style creates duplicate threads. Use a callback for the response instead.
                                                         * Callbacks would also make the renderer queue requests,
                                                         * which avoids holding one thread for each parallel request.
                                                         */
                                                        final UserSession user;
                                                        if (routingContext.user() == null) {
                                                            user = ANONYMOUS_USER_SESSION;
                                                        } else {
                                                            user = (UserSession) routingContext.user().attributes().getValue(LOGIN_KEY);
                                                        }
                                                        renderer.access((u, r) -> {
                                                            final var result = r.render(renderRequest(trail(requestPath), Optional.empty(), user));
                                                            if (result.data().isPresent()) {
                                                                response.putHeader("content-type", result.data().get().getFormat());
                                                                promise.complete(result.data().get().getContent());
                                                            } else {
                                                                promise.fail(new DocumentNotFound(requestPath));
                                                            }
                                                        }, user);
                                                    } catch (Exception e) {
                                                        logs().fail(e);
                                                        throw new RuntimeException(e);
                                                    }
                                                }, config.isSingleThreaded()
                                                , result -> handleResult(routingContext, result));
                                    }
                                });
                        router.errorHandler(500, e -> {
                            if (e.failure() instanceof SSLHandshakeException sslHandshakeException) {
                                // Avoid stack trace for error, that is present on the client and not this program.
                                logs().append(tree("Could not establish SSL connection.").withProperty("reason", sslHandshakeException.getMessage()), ERROR);
                            } else {
                                logs().fail(e.failure());
                            }
                        });
                        vertx.createHttpServer(webServerOptions)
                                .requestHandler(router)
                                .exceptionHandler(th ->
                                        // TODO Avoid logging stack traces for connection issues. Filter appropriate stack traces. When filtering is added, at least log the type of filtered exceptions and not just the message.
                                        logs().fail(tree("An error occurred at the HTTP server.").with(th)))
                                .listen(result -> {
                                    if (result.failed()) {
                                        startPromise.fail(result.cause());
                                    } else {
                                        startPromise.complete();
                                    }
                                });
                    }
                }, deploymentOptions);
                final var deployWaiter = new Semaphore(1);
                final List<Throwable> errors = list();
                try {
                    deployWaiter.acquire();
                } catch (Throwable t) {
                    Thread.currentThread().interrupt();
                    throw ExecutionException.execException("Could not start HTTP server.");
                }
                deployResult.onComplete(result -> {
                    if (result.failed()) {
                        errors.add(result.cause());
                    }
                    deployWaiter.release();
                });
                try {
                    deployWaiter.acquire();
                } catch (Throwable t) {
                    Thread.currentThread().interrupt();
                    throw ExecutionException.execException("An error occurred during start of the HTTP server.");
                }
                if (!errors.isEmpty()) {
                    throw ExecutionException.execException("Could not start HTTP server.", errors.getFirst());
                }
            }
        };
    }

    private static String requestPath(RoutingContext routingContext) {
        final String requestPath;
        if ("".equals(routingContext.request().path()) || "/".equals(routingContext.request().path())) {
            requestPath = "index.html";
        } else {
            requestPath = routingContext.request().path();
        }
        return requestPath;
    }

    private static void handleResult(RoutingContext routingContext, AsyncResult<byte[]> result) {
        final var response = routingContext.response();
        if (result.failed() && result.cause() instanceof DocumentNotFound) {
            logs().append(tree("Could not find render for path")
                            .withProperty("path", result.cause().getMessage())
                    , ERROR);
            response.setStatusCode(404);
            response.end();
            return;
        }
        if (result.failed()) {
            logs().fail(ExecutionException.execException(tree("Could not process form:")
                            .withProperty("path", routingContext.request().path())
                    , result.cause()));
            response.setStatusCode(500);
            response.end();
        } else {
            response.end(buffer().appendBytes(result.result()));
        }
    }

    private static Request<Tree> parseBinaryRequest(String path, MultiMap multiMap) {
        final var pathSplit = Lists.listWithValuesOf(path.split("/"));
        if (!pathSplit.isEmpty() && "".equals(pathSplit.get(0))) {
            pathSplit.removeAt(0);
        }
        final var requestData = tree("");
        multiMap.entries().forEach(entry -> {
            requestData.withProperty(entry.getKey(), entry.getValue());
        });
        return request(trail(pathSplit), requestData);
    }

    /**
     * <p>TODO This is code duplication.
     * Move this functionality into {@link #serveToHttpAt(AccessControl, Config)} via {@link Config}.</p>
     *
     * @param renderer
     * @param config
     * @return
     */
    @Deprecated
    public static Service serveAsAuthenticatedHttpsAt
    (Function<String, Optional<BinaryMessage>> renderer, Config config) {
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
                            // TODO
                        });
                        router.route("/*").handler(routingContext -> {
                            HttpServerResponse response = routingContext.response();
                            vertx.<byte[]>executeBlocking(promise -> {
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
                                    promise.fail(new DocumentNotFound(requestPath));
                                }
                            }, result -> {
                                if (result.failed()) {
                                    logs().fail(result.cause());
                                    response.setStatusCode(500);
                                    response.end();
                                } else {
                                    response.end(buffer().appendBytes(result.result()));
                                }
                            });
                        });
                        router.errorHandler(500, e -> {
                            logs().fail(e.failure());
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
                // Nothing needs to be done.
            }
        };
    }
}
