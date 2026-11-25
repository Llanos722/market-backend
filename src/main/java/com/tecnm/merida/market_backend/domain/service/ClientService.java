package com.tecnm.merida.market_backend.domain.service;
import com.tecnm.merida.market_backend.domain.Client;
import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import com.tecnm.merida.market_backend.persistence.mapper.ClientMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Service
@Transactional(readOnly=true)
public class ClientService {
 private final ClienteCrudRepository crud;
 private final ClientMapper mapper;
 private final PasswordEncoder passwordEncoder;
 public ClientService(ClienteCrudRepository crud, ClientMapper mapper, PasswordEncoder passwordEncoder) { this.crud=crud; this.mapper=mapper; this.passwordEncoder=passwordEncoder; }
 public List<Client> getAll() { return crud.findAll().stream().map(mapper::toClient).toList(); }
 public Client get(String id) { return mapper.toClient(crud.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Client no encontrado"))); }
 @Transactional
 public Client create(Client value) {
  if(value.clientId()==null || value.clientId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"clientId es obligatorio"); if(crud.existsById(value.clientId())) throw new ResponseStatusException(HttpStatus.CONFLICT,"El cliente ya existe");
  var entity = mapper.toCliente(value);
  changePassword(entity, value.password());
  return mapper.toClient(crud.save(entity));
 }
 @Transactional
 public Client update(String id, Client value) {
  var entity = crud.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Client no encontrado"));
  entity.setNombre(value.name());
  entity.setApellido(value.lastName());
  entity.setCelular(value.phone());
  entity.setDireccion(value.address());
  entity.setCorreoElectronico(value.email());
  changePassword(entity, value.password());
  return mapper.toClient(crud.save(entity));
 }
 private void changePassword(Cliente entity, String password) {
  if(password == null) return;
  if(password.isBlank() || password.getBytes(StandardCharsets.UTF_8).length > 72) {
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"password debe contener entre 1 y 72 bytes UTF-8 y no estar en blanco");
  }
  entity.setPassword(passwordEncoder.encode(password));
 }
 @Transactional
 public void delete(String id) { get(id); crud.deleteById(id); }
}
