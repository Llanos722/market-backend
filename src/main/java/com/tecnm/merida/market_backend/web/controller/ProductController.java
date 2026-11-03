package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.Product;
import com.tecnm.merida.market_backend.domain.repository.ProductRepository;
import com.tecnm.merida.market_backend.domain.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/products")
public class ProductController {

    private ProductService productService;
    private ProductRepository productRepository;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAll() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable int productId) {
        return productService.getProduct((productId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable int categoryId) {
        return productService.getByCategory((categoryId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping("/")
    public ResponseEntity<Product> save(@RequestBody Product productId) {
        return new ResponseEntity<>(productService.save(productId), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Product> delete(@RequestBody int productId) {
        if (productService.delete(productId)) {

            return ResponseEntity.ok().build();
        }
        {
        }
        return ResponseEntity.notFound().build();
    }
}
