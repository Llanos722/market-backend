package com.tecnm.merida.market_backend.persistence.crud;

import com.tecnm.merida.market_backend.persistence.entity.Categoria;
import org.springframework.data.repository.ListCrudRepository;

public interface CategoriaCrudRepository extends ListCrudRepository<Categoria, Integer> {
}
