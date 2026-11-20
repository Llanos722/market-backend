package com.tecnm.merida.market_backend.domain;
import java.math.BigDecimal;
public record Client(String clientId, String name, String lastName, BigDecimal phone, String address, String email) {}