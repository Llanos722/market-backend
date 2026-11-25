package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.repository.PurchaseRepository;
import com.tecnm.merida.market_backend.persistence.crud.*;
import com.tecnm.merida.market_backend.persistence.mapper.PurchaseMapper;
import com.tecnm.merida.market_backend.web.dto.PurchaseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PurchaseService {
    private final PurchaseRepository purchases;
    private final ProductoCrudRepository products;
    private final ClienteCrudRepository clients;
    private final PurchaseMapper mapper;

    public PurchaseService(PurchaseRepository purchases, ProductoCrudRepository products,
                           ClienteCrudRepository clients, PurchaseMapper mapper) {
        this.purchases = purchases;
        this.products = products;
        this.clients = clients;
        this.mapper = mapper;
    }

    public List<Purchase> getAll() { return purchases.getAll(); }
    public Optional<List<Purchase>> getByClientId(String id) { return purchases.getByIdClient(id); }

    @Transactional
    public Purchase save(PurchaseRequest request) {
        if (request.idCliente() == null || request.idCliente().isBlank()
                || request.productosAdquiridos() == null || request.productosAdquiridos().isEmpty()) {
            throw bad("idCliente y productosAdquiridos son obligatorios");
        }
        String payment = paymentCode(request.medioPago());
        if (!clients.existsById(request.idCliente())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        Set<Integer> ids = new HashSet<>();
        for (var item : request.productosAdquiridos()) {
            if (item == null || item.idProducto() == null || item.idProducto() <= 0
                    || item.cantidad() == null || item.cantidad() <= 0) {
                throw bad("Cada detalle requiere idProducto y cantidad positivos");
            }
            if (item.total() != null && (!Double.isFinite(item.total()) || item.total() < 0)) {
                throw bad("El total debe ser un numero no negativo");
            }
            if (!ids.add(item.idProducto())) throw bad("No repitas un producto en la compra");
        }

        var compra = mapper.toCompra(request);
        compra.setMedioPago(payment);

        // Orden estable para evitar bloqueos cruzados entre compras concurrentes.
        for (var detail : compra.getProductos().stream()
                .sorted(Comparator.comparing(item -> item.getId().getIdProducto())).toList()) {
            int id = detail.getId().getIdProducto();
            var product = products.findForPurchase(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
            if (!Boolean.TRUE.equals(product.getEstado()) || product.getCantidadStock() < detail.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto inactivo o stock insuficiente: " + id);
            }
            if (detail.getTotal() == null) {
                if (product.getPrecioVenta() == null || !Double.isFinite(product.getPrecioVenta()) || product.getPrecioVenta() < 0) {
                    throw bad("Producto sin precio valido");
                }
                detail.setTotal(java.math.BigDecimal.valueOf(product.getPrecioVenta())
                        .multiply(java.math.BigDecimal.valueOf(detail.getCantidad())).doubleValue());
            }
            product.setCantidadStock(product.getCantidadStock() - detail.getCantidad());
            products.saveAndFlush(product);
        }

        return purchases.save(mapper.toPurchase(compra));
    }

    private String paymentCode(String value) {
        if (value == null || value.isBlank()) throw bad("medioPago es obligatorio");
        // PostgreSQL local conserva medio_pago CHAR(1).
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "TARJETA", "T" -> "T";
            case "EFECTIVO", "E" -> "E";
            case "TRANSFERENCIA", "TRANSFER", "TR", "B" -> "B";
            default -> throw bad("medioPago debe ser TARJETA, EFECTIVO o TRANSFERENCIA");
        };
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
