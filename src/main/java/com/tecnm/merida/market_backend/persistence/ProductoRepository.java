package com.tecnm.merida.market_backend.persistence;

import com.tecnm.merida.market_backend.persistence.crud.ProductoCrudRepository;
import com.tecnm.merida.market_backend.persistence.entity.Producto;

import java.util.List;

public class ProductoRepository {

    private ProductoCrudRepository productoCrudRepository;
    //select * from productos
    public List<Producto> getAll( ) {return (List<Producto>) ProductoCrudRepository.findAll();
    }
    //vamos a caastear

}
