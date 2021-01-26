package com.amihaescu.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Symbol", description = "Abbreviation to uniquely identify public trade shares of stock")
public class Symbol {

    @Schema(description = "symbol value", minLength = 1, maxLength = 5)
    private String value;
}
