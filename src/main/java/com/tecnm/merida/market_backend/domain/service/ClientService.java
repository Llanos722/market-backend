package com.tecnm.merida.market_backend.domain.service;
import com.tecnm.merida.market_backend.domain.Client;
import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
@Transactional(readOnly=true)
public class ClientService {
 private final ClienteCrudRepository crud;
 private final ClientMapper mapper;
 public ClientService(ClienteCrudRepository crud, ClientMapper mapper) { this.crud=crud; this.mapper=mapper; }
 public List<Client> getAll() { return crud.findAll().stream().map(mapper::toClient).toList(); }
 public Client get(String id) { return mapper.toClient(crud.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Client no encontrado"))); }
 @Transactional
 public Client create(Client value) {
  if(value.clientId()==null || value.clientId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"clientId es obligatorio"); if(crud.existsById(value.clientId())) throw new ResponseStatusException(HttpStatus.CONFLICT,"El cliente ya existe");
  return mapper.toClient(crud.save(mapper.toCliente(value)));
 }
 @Transactional
 public Client update(String id, Client value) {
  get(id);
  value = new Client(id,value.name(),value.lastName(),value.phone(),value.address(),value.email());
  return mapper.toClient(crud.save(mapper.toCliente(value)));
 }
 @Transactional
 public void delete(String id) { get(id); crud.deleteById(id); }
}