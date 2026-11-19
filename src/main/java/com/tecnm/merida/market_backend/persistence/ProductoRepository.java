package com.tecnm.merida.market_backend.persistence;
import com.tecnm.merida.market_backend.domain.Product;
import com.tecnm.merida.market_backend.domain.repository.ProductRepository;
import com.tecnm.merida.market_backend.persistence.crud.ProductoCrudRepository;
import com.tecnm.merida.market_backend.persistence.entity.Producto;
import com.tecnm.merida.market_backend.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class ProductoRepository implements ProductRepository {
    private final ProductoCrudRepository crud;
    private final ProductMapper mapper;
    public ProductoRepository(ProductoCrudRepository crud, ProductMapper mapper) { this.crud=crud; this.mapper=mapper; }
    public List<Product> getAll() { return mapper.toProducts((List<Producto>) crud.findAll()); }
    public Optional<List<Product>> getByCategory(int id) { return Optional.of(mapper.toProducts(crud.findByIdCategoriaOrderByNombreAsc(id))); }
    public Optional<List<Product>> getScarceProducts(int quantity) { return crud.findByCantidadStockLessThanAndEstado(quantity,true).map(mapper::toProducts); }
    public Optional<Product> getProduct(int id) { return crud.findById(id).map(mapper::toProduct); }
    public Product save(Product product) { return mapper.toProduct(crud.save(mapper.toProducto(product))); }
    public void delete(int id) { crud.deleteById(id); }
}
