package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.repository.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<Purchase> getAll() {
        return purchaseRepository.getAll();
    }

    public Optional<List<Purchase>> getByClientId(String clientId) {
        return purchaseRepository.getByIdClient(clientId);
    }

    @Transactional
    public Purchase save(Purchase purchase) {
        if (purchase.getClientId() == null || purchase.getClientId().isBlank()
                || purchase.getItems() == null || purchase.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idCliente y productosAdquiridos son obligatorios");
        }
        purchase.setPaymentMethod(paymentCode(purchase.getPaymentMethod()));
        purchase.setDate(LocalDateTime.now());
        purchase.setState("P");
        return purchaseRepository.save(purchase);
    }

    private String paymentCode(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "medioPago es obligatorio");
        }
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "TARJETA", "T" -> "T";
            case "EFECTIVO", "E" -> "E";
            case "TRANSFERENCIA", "TRANSFER", "TR", "B" -> "B";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "medioPago debe ser TARJETA, EFECTIVO o TRANSFERENCIA");
        };
    }
}
