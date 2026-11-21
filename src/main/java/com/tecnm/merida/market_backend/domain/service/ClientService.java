package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Client;
import com.tecnm.merida.market_backend.domain.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll() {
        return clientRepository.getAll();
    }

    public Client get(String id) {
        return clientRepository.getClient(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    @Transactional
    public Client create(Client client) {
        if (client.clientId() == null || client.clientId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId es obligatorio");
        }
        if (clientRepository.getClient(client.clientId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente ya existe");
        }
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(String id, Client client) {
        get(id);
        return clientRepository.save(new Client(id, client.name(), client.lastName(), client.phone(), client.address(), client.email()));
    }

    @Transactional
    public void delete(String id) {
        get(id);
        clientRepository.delete(id);
    }
}
