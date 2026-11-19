package com.tecnm.merida.market_backend.domain.service;
import com.tecnm.merida.market_backend.domain.Product;
import com.tecnm.merida.market_backend.domain.repository.ProductRepository;
import com.tecnm.merida.market_backend.persistence.crud.CategoriaCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service
@Transactional(readOnly=true)
public class ProductService {
 private final ProductRepository repository;
 private final CategoriaCrudRepository categories;
 public ProductService(ProductRepository repository, CategoriaCrudRepository categories) { this.repository=repository; this.categories=categories; }
 public List<Product> getAll() { return repository.getAll(); }
 public Optional<Product> getProduct(int id) { return repository.getProduct(id); }
 public Optional<List<Product>> getByCategory(int id) { return repository.getByCategory(id); }
 @Transactional
 public Product save(Product product) { product.setProductId(null); validate(product); return repository.save(product); }
 @Transactional
 public Product update(int id,Product product) {
  getProduct(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
  product.setProductId(id); validate(product); return repository.save(product);
 }
 @Transactional
 public boolean delete(int id) { if(getProduct(id).isEmpty()) return false; repository.delete(id); return true; }
 private void validate(Product product) {
  if(product.getName()==null || product.getName().isBlank() || product.getCategoryId()==null || !Double.isFinite(product.getPrice()) || product.getPrice()<0 || product.getStock()<0)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nombre, categoria, precio y stock validos son obligatorios");
  if(!categories.existsById(product.getCategoryId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Categoria no encontrada");
 }
}
