package com.example.fbk_balkan.payload;
import lombok.AllArgsConstructor;
import lombok.Data;


// Represents a success message with response data
@Data
@AllArgsConstructor
public class ApiSuccess<T> {
    private String message;
    private T data;
}
