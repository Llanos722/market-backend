package com.tecnm.merida.market_backend.web.controller;
import com.tecnm.merida.market_backend.domain.Category;
import com.tecnm.merida.market_backend.domain.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController
@RequestMapping("/categories")
public class CategoryController {
 private final CategoryService service;
 public CategoryController(CategoryService service) { this.service=service; }
 @GetMapping public List<Category> getAll() { return service.getAll(); }
 @GetMapping("/{id}") public Category get(@PathVariable("id") Integer id) { return service.get(id); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Category create(@RequestBody Category value) { return service.create(value); }
 @PutMapping("/{id}") public Category update(@PathVariable("id") Integer id,@RequestBody Category value) { return service.update(id,value); }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable("id") Integer id) { service.delete(id); }
}