package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Category;
import com.tecnm.merida.market_backend.domain.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.getAll();
    }

    public Category get(Integer id) {
        return categoryRepository.getCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
    }

    @Transactional
    public Category create(Category category) {
        category.setCategoryId(null);
        validate(category);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Integer id, Category category) {
        get(id);
        category.setCategoryId(id);
        validate(category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Integer id) {
        get(id);
        categoryRepository.delete(id);
    }

    private void validate(Category category) {
        if (category.getCategory() == null || category.getCategory().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoria es obligatoria");
        }
    }
}
