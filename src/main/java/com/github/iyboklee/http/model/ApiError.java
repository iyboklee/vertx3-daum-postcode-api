/**
 * @Author iyboklee (iyboklee@gmail.com)
 */
package com.github.iyboklee.http.model;

import lombok.Getter;

@Getter
public class ApiError {

    private String message;
    private int status;

    public ApiError(Throwable throwable, int status) {
        this(throwable.getMessage(), status);
    }

    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
    }

}