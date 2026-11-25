package com.tecnm.merida.market_backend.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
public record Client(String clientId, String name, String lastName, BigDecimal phone, String address, String email,
                     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                     @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, format = "password") String password) {}
