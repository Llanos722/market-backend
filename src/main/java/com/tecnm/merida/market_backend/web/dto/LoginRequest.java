package com.tecnm.merida.market_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(String idCliente,
                           @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                           @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, format = "password") String password) {}
