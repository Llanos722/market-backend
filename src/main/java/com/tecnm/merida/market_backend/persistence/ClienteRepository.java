package com.tecnm.merida.market_backend.persistence;

import com.tecnm.merida.market_backend.domain.Client;
import com.tecnm.merida.market_backend.domain.repository.ClientRepository;
import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository implements ClientRepository {
    private final ClienteCrudRepository crud;
    private final ClientMapper mapper;

    public ClienteRepository(ClienteCrudRepository crud, ClientMapper mapper) {
        this.crud = crud;
        this.mapper = mapper;
    }

    @Override
    public List<Client> getAll() {
        return crud.findAll().stream().map(mapper::toClient).toList();
    }

    @Override
    public Optional<Client> getClient(String id) {
        return crud.findById(id).map(mapper::toClient);
    }

    @Override
    public Client save(Client client) {
        return mapper.toClient(crud.save(mapper.toCliente(client)));
    }

    @Override
    public void delete(String id) {
        crud.deleteById(id);
    }
}
