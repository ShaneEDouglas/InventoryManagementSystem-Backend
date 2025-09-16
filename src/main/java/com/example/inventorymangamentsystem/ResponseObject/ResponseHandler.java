package com.example.inventorymangamentsystem.ResponseObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseHandler<T> {

    private String message;
    private T data;

    public ResponseHandler(String message, T data) {
        this.message = message;
        this.data = data;

    }

    public static <T> ResponseEntity<ResponseHandler<T>> responseBuilder(String message, HttpStatus httpStatus, T responseObject) {
        ResponseHandler<T> body = new ResponseHandler<T>(message, responseObject);
        return new ResponseEntity<>(body, httpStatus);


    }


    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
