package com.tecnm.merida.market_backend.web.controller;
import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.service.PurchaseService;
import com.tecnm.merida.market_backend.web.dto.PurchaseRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController
@RequestMapping("/purchases")
public class PurchaseController {
 private final PurchaseService service;
 public PurchaseController(PurchaseService service) { this.service=service; }
 @GetMapping public List<Purchase> getAll() { return service.getAll(); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED)
 public Purchase create(@RequestBody PurchaseRequest request) { return service.save(request); }
}
