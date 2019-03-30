/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee.utils;

import io.vertx.core.json.JsonObject;

public class ConfigUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getMandatoryValueConfig(JsonObject config, String fieldName) {
        T value = (T) config.getValue(fieldName);
        if (value == null)
            throw new IllegalArgumentException("\"{filed=" + fieldName + "}\" must be specified in config");
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOptionalValueConfig(JsonObject config, String fieldName, T defaultValue) {
        T value = (T) config.getValue(fieldName);
        return value != null ? value : defaultValue;
    }

}