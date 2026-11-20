package com.tecnm.merida.market_backend.persistence.crud;
import com.tecnm.merida.market_backend.persistence.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaCrudRepository extends JpaRepository<Categoria, Integer> {}