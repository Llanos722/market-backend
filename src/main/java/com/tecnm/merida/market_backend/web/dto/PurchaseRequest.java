package com.tecnm.merida.market_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.PurchaseItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public record PurchaseRequest(
        @JsonAlias("clientId") String idCliente,
        @JsonAlias("paymentMethod") String medioPago,
        @JsonAlias("comment") String comentario,
        @JsonAlias("items") List<Item> productosAdquiridos) {

    public Purchase toPurchase() {
        Purchase purchase = new Purchase();
        purchase.setClientId(idCliente);
        purchase.setPaymentMethod(medioPago);
        purchase.setComment(comentario);
        if (productosAdquiridos != null) {
            purchase.setItems(productosAdquiridos.stream().map(PurchaseRequest::toPurchaseItem).toList());
        }
        return purchase;
    }

    private static PurchaseItem toPurchaseItem(Item item) {
        if (item == null || item.idProducto() == null || item.idProducto() <= 0
                || item.cantidad() == null || item.cantidad() <= 0
                || item.total() == null || !Double.isFinite(item.total()) || item.total() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cada detalle requiere idProducto, cantidad y total validos");
        }
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setProductId(item.idProducto());
        purchaseItem.setQuantity(item.cantidad());
        purchaseItem.setTotal(item.total());
        purchaseItem.setActive(true);
        return purchaseItem;
    }

    public record Item(
            @JsonAlias("productId") Integer idProducto,
            @JsonAlias("quantity") Integer cantidad,
            Double total) {}
}
