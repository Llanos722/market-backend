package com.tecnm.merida.market_backend.domain.service;
import com.tecnm.merida.market_backend.domain.Category;
import com.tecnm.merida.market_backend.persistence.crud.CategoriaCrudRepository;
import com.tecnm.merida.market_backend.persistence.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
@Transactional(readOnly=true)
public class CategoryService {
 private final CategoriaCrudRepository crud;
 private final CategoryMapper mapper;
 public CategoryService(CategoriaCrudRepository crud, CategoryMapper mapper) { this.crud=crud; this.mapper=mapper; }
 public List<Category> getAll() { return crud.findAll().stream().map(mapper::toCategory).toList(); }
 public Category get(Integer id) { return mapper.toCategory(crud.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category no encontrado"))); }
 @Transactional
 public Category create(Category value) {
  value.setCategoryId(null); if(value.getCategory()==null || value.getCategory().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La categoria es obligatoria");
  return mapper.toCategory(crud.save(mapper.toCategory(value)));
 }
 @Transactional
 public Category update(Integer id, Category value) {
  get(id);
  value.setCategoryId(id); if(value.getCategory()==null || value.getCategory().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La categoria es obligatoria");
  return mapper.toCategory(crud.save(mapper.toCategory(value)));
 }
 @Transactional
 public void delete(Integer id) { get(id); crud.deleteById(id); }
}