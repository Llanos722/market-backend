package com.tecnm.merida.market_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
 "spring.datasource.url=jdbc:h2:mem:ada11;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.username=sa", "spring.datasource.password=",
 "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MarketBackendApplicationTests {
 @LocalServerPort int port;
 @Autowired JdbcTemplate jdbc;
 private final HttpClient http=HttpClient.newHttpClient();
 private final ObjectMapper json=new ObjectMapper();
 private HttpResponse<String> request(String method,String path,String body) throws Exception {
  var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type","application/json");
  return http.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 private int create(String path,String body,String id) throws Exception {
  var result=request("POST",path,body); assertEquals(201,result.statusCode(),result.body());
  return json.readTree(result.body()).get(id).asInt();
 }
 @Test void crudAndPurchaseRollback() throws Exception {
  assertEquals(200,request("GET","/products",null).statusCode());
  int category=create("/categories","{\"category\":\"Pruebas\",\"active\":true}","categoryId");
  String categoryPath="/categories/"+category;
  assertEquals(200,request("GET",categoryPath,null).statusCode());
  assertEquals(200,request("PUT",categoryPath,"{\"category\":\"Actualizada\",\"active\":true}").statusCode());
  String client="{\"clientId\":\"ada11\",\"name\":\"Ana\",\"lastName\":\"Perez\",\"phone\":9991234567}";
  assertEquals(201,request("POST","/clients",client).statusCode());
  assertEquals(409,request("POST","/clients",client).statusCode());
  assertEquals(200,request("PUT","/clients/ada11",client).statusCode());
  assertEquals(200,request("GET","/clients/ada11",null).statusCode());
  String product="{\"name\":\"Arroz\",\"categoryId\":"+category+",\"barcode\":\"ABC-123\",\"price\":25.5,\"stock\":10,\"active\":true}";
  int first=create("/products",product,"productId");
  int second=create("/products",product.replace("\"stock\":10","\"stock\":0"),"productId");
  assertEquals(200,request("PUT","/products/"+first,product).statusCode());
  String header="{\"clientId\":\"ada11\",\"paymentMethod\":\"E\",\"items\":[";
  var success=request("POST","/purchases","{\"idCliente\":\"ada11\",\"medioPago\":\"TARJETA\",\"comentario\":\"ADA11\",\"productosAdquiridos\":[{\"idProducto\":"+first+",\"cantidad\":2,\"total\":50.0}]}");
  assertEquals(201,success.statusCode(),success.body());
  var saved=json.readTree(success.body());
  int purchaseId=saved.get("purchaseId").asInt();
  assertEquals(first,saved.get("items").get(0).get("productId").asInt());
  assertEquals(50.0,saved.get("items").get(0).get("total").asDouble());
  assertEquals("T",saved.get("paymentMethod").asText());
  assertEquals(1,jdbc.queryForObject("select count(*) from compras_productos where id_compra=? and id_producto=?",Integer.class,purchaseId,first));
  var cascadeFailure = request("POST", "/purchases", header + """
          {"productId":%d,"quantity":1,"total":25.5},
          {"productId":2147483647,"quantity":1,"total":25.5}
          ]}
          """.formatted(first));
  assertEquals(409,cascadeFailure.statusCode(),cascadeFailure.body());
  assertEquals(1,jdbc.queryForObject("select count(*) from compras",Integer.class));
  assertEquals(1,jdbc.queryForObject("select count(*) from compras_productos",Integer.class));
  assertEquals(400,request("POST","/purchases",header+"]}").statusCode());
  assertEquals(200,request("GET","/purchases",null).statusCode());
  assertEquals(409,request("DELETE","/products/"+first,null).statusCode());
  assertEquals(200,request("DELETE","/products/"+second,null).statusCode());
  int unused=create("/categories","{\"category\":\"Temporal\",\"active\":true}","categoryId");
  assertEquals(204,request("DELETE","/categories/"+unused,null).statusCode());
  assertEquals(201,request("POST","/clients",client.replace("ada11","temporal")).statusCode());
  assertEquals(204,request("DELETE","/clients/temporal",null).statusCode());
  assertEquals(404,request("GET","/clients/temporal",null).statusCode());
  assertEquals(200,request("GET","/v3/api-docs",null).statusCode());
 }
}
