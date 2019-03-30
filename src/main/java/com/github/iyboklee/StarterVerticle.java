/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee;

import com.github.iyboklee.http.HttpVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class StarterVerticle extends AbstractVerticle {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void start() {
        long start = System.currentTimeMillis();
        log.info("vertx3-daum-postcode-api service initialization started");

        JsonObject config = config();
        log.info("config: {}", config.toString());
        JsonObject postcodeConfig = config.getJsonObject("postcode");

        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setConfig(postcodeConfig).setInstances(3));

        log.info("vertx3-daum-postcode-api initialization completed in {} ms", System.currentTimeMillis() - start);
    }

}