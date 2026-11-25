package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Product;
import com.tecnm.merida.market_backend.domain.repository.CategoryRepository;
import com.tecnm.merida.market_backend.domain.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAll() {
        return productRepository.getAll();
    }

    public Optional<Product> getProduct(int productId) {
        return productRepository.getProduct(productId);
    }

    public Optional<List<Product>> getByCategory(int categoryId) {
        return productRepository.getByCategory(categoryId);
    }

    @Transactional
    public Product save(Product product) {
        product.setProductId(null);
        validate(product);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(int productId, Product product) {
        getProduct(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        product.setProductId(productId);
        validate(product);
        return productRepository.save(product);
    }

    @Transactional
    public boolean delete(int productId) {
        if (getProduct(productId).isEmpty()) {
            return false;
        }
        productRepository.delete(productId);
        return true;
    }

    private void validate(Product product) {
        if (product.getName() == null || product.getName().isBlank() || product.getCategoryId() == null
                || !Double.isFinite(product.getPrice()) || product.getPrice() < 0 || product.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, categoria, precio y stock validos son obligatorios");
        }
        if (categoryRepository.getCategory(product.getCategoryId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada");
        }
    }
}
