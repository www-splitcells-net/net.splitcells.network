package net.splitcells.website.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.website.server.renderer.RenderingResult;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;

/**
 * TODO Create and use server interface, instead of implementation.
 */
public class Server {
    /**
     * TODO This is code duplication.
     *
     * @param port     port
     * @param renderer renderer
     */
    public void serveToHttpAt(int port, Function<String, Optional<RenderingResult>> renderer) {
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
                            .setSsl(true)//
                            .setKeyCertOptions(new PfxOptions()
                                    .setPath(configValue(SslKeystore.class).toString())
                                    .setPassword(configValue(SslKeystorePassword.class)))
                            .setTrustOptions(new PfxOptions()
                                    .setPath(configValue(SslKeystore.class).toString())
                                    .setPassword(configValue(SslKeystorePassword.class)))
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
                            final var result = renderer.apply(requestPath);
                            if (result.isPresent()) {
                                response.putHeader("content-type", result.get().getFormat());
                                promise.complete(result.get().getContent());
                            } else {
                                promise.fail("Could not render path:" + requestPath);
                            }
                        }, (result) -> {
                            if (result.failed()) {
                                domsole().append(textNode(result.cause().toString()), LogLevel.ERROR);
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

    public void serveAsAuthenticatedHttpsAt(int port, Function<String, Optional<RenderingResult>> renderer) {
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
                                .setPath(configValue(SslKeystore.class).toString())
                                .setPassword(configValue(SslKeystorePassword.class)))
                        .setTrustOptions(new PfxOptions()
                                .setPath(configValue(SslKeystore.class).toString())
                                .setPassword(configValue(SslKeystorePassword.class)))
                        .setClientAuth(ClientAuth.REQUIRED)
                        .setLogActivity(true)
                        .setPort(port);
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
