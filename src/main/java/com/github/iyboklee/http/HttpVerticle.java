/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee.http;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.github.iyboklee.http.model.ApiResult;
import com.github.iyboklee.postcode.PostcodeProxy;
import com.github.iyboklee.utils.ConfigUtils;
import com.github.iyboklee.utils.JaskonUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class HttpVerticle extends AbstractVerticle {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String Path = "/api/search";
    private static final String ContentType = "application/json";

    private HttpServer server;
    private PostcodeProxy postcodeProxy;

    @Override
    public void start(Future<Void> startedResult) {
        JsonObject config = config();
        String url = ConfigUtils.getMandatoryValueConfig(config, "url");
        String origin = ConfigUtils.getMandatoryValueConfig(config, "origin");
        String userAgent = ConfigUtils.getMandatoryValueConfig(config, "userAgent");

        postcodeProxy = new PostcodeProxy(url, origin, userAgent);
        server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        Route route = router.get(Path);

        route.blockingHandler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", ContentType);

            String query = routingContext.request().getParam("query");
            String page = routingContext.request().getParam("page");

            if (StringUtils.isEmpty(query)) {
                response.setStatusCode(400).end(JaskonUtils.writeAsString(new ApiResult("Empty query", 400)));
                return;
            }

            try {
                response.end(JaskonUtils.writeAsString(postcodeProxy.search(query, NumberUtils.toInt(page, 1))));
            } catch (IOException e) {
                routingContext.fail(e);
            }
        });

        route.failureHandler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", ContentType);
            Throwable throwable = routingContext.failure();
            response.setStatusCode(500).end(JaskonUtils.writeAsString(new ApiResult(throwable, 500)));
        });

        server.requestHandler(router::accept).listen(8080, asyncResult -> {
            log.info("HttpServer bind [port={}] result: {}", 8080, asyncResult.succeeded());
            if (asyncResult.failed())
                startedResult.fail(asyncResult.cause());
            else
                startedResult.complete();
        });
    }

    @Override
    public void stop() {
        if (server != null)
            server.close();
    }

}