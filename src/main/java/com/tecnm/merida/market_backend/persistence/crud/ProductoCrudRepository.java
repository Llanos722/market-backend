package com.tecnm.merida.market_backend.persistence.crud;

import com.tecnm.merida.market_backend.persistence.entity.Producto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

// metodos abstractos que despues se implementaras

public interface ProductoCrudRepository extends CrudRepository<Producto, Integer> {

    /* SQL Query
    * SELECT *
    * FROM productos
    * WHERE id_categoria = 10
    * ORDER BY NAME ASC
     */

    List<Producto> findByIdCategoriaOrderByNombreAsc (int idCategoria);

    //cantidad Stock
   Optional<List<Producto>> findByCantidadStockLessThanAndEstado (int cantidadStock, boolean estado);
}
