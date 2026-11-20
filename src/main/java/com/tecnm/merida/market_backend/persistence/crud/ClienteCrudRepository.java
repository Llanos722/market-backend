package com.tecnm.merida.market_backend.persistence.crud;
import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteCrudRepository extends JpaRepository<Cliente, String> {}