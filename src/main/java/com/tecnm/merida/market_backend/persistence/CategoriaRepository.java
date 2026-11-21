package com.tecnm.merida.market_backend.persistence;

import com.tecnm.merida.market_backend.domain.Category;
import com.tecnm.merida.market_backend.domain.repository.CategoryRepository;
import com.tecnm.merida.market_backend.persistence.crud.CategoriaCrudRepository;
import com.tecnm.merida.market_backend.persistence.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoriaRepository implements CategoryRepository {
    private final CategoriaCrudRepository crud;
    private final CategoryMapper mapper;

    public CategoriaRepository(CategoriaCrudRepository crud, CategoryMapper mapper) {
        this.crud = crud;
        this.mapper = mapper;
    }

    @Override
    public List<Category> getAll() {
        return crud.findAll().stream().map(mapper::toCategory).toList();
    }

    @Override
    public Optional<Category> getCategory(int id) {
        return crud.findById(id).map(mapper::toCategory);
    }

    @Override
    public Category save(Category category) {
        return mapper.toCategory(crud.save(mapper.toCategory(category)));
    }

    @Override
    public void delete(int id) {
        crud.deleteById(id);
    }
}
