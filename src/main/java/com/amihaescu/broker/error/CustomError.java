package com.amihaescu.broker.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomError {

    private int status;
    private String message;
    private String error;
    private String path;
}
