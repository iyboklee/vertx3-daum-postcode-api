/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee.http.model;

import lombok.Getter;

@Getter
public class ApiResult<T> {

    private boolean success;
    private ApiError error;
    private T response;

    public ApiResult(T response) {
        this.response = response;
        this.success = true;
    }

    public ApiResult(String message, int status) {
        this.success = false;
        this.response = null;
        this.error = new ApiError(message, status);
    }

    public ApiResult(Throwable throwable, int status) {
        this.success = false;
        this.response = null;
        this.error = new ApiError(throwable, status);
    }

}