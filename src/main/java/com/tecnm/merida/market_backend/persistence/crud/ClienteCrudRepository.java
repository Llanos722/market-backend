package com.tecnm.merida.market_backend.persistence.crud;

import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import org.springframework.data.repository.ListCrudRepository;

public interface ClienteCrudRepository extends ListCrudRepository<Cliente, String> {
}
