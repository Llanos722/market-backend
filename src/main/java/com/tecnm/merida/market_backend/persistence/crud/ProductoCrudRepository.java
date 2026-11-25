package com.tecnm.merida.market_backend.persistence.crud;

import com.tecnm.merida.market_backend.persistence.entity.Producto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ProductoCrudRepository extends org.springframework.data.jpa.repository.JpaRepository<Producto, Integer> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Producto p where p.idProducto = :id")
    Optional<Producto> findForPurchase(@org.springframework.data.repository.query.Param("id") int id);

    List<Producto> findByIdCategoriaOrderByNombreAsc (int idCategoria);

   Optional<List<Producto>> findByCantidadStockLessThanAndEstado (int cantidadStock, boolean estado);
}
