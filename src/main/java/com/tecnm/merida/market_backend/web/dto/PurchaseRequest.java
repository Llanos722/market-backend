package com.tecnm.merida.market_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

/** DTO maestro-detalle de la ADA 11. */
public record PurchaseRequest(
        @JsonAlias("clientId") String idCliente,
        @JsonAlias("paymentMethod") String medioPago,
        @JsonAlias("comment") String comentario,
        @JsonAlias("items") List<Item> productosAdquiridos) {
    public record Item(
            @JsonAlias("productId") Integer idProducto,
            @JsonAlias("quantity") Integer cantidad,
            Double total) {}
}
