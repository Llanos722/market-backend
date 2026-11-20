# ADA 11: CRUD y compras maestro-detalle

Ejecuta `.\gradlew.bat bootRun` o reinicia desde IntelliJ. Abre http://localhost:8080/swagger-ui/index.html.
La configuracion local de PostgreSQL se conserva sin cambios.

## Cumplimiento

- ProductController, CategoryController y ClientController exponen GET (coleccion y por ID), POST, PUT y DELETE.
- PurchaseController expone GET /purchases y POST /purchases.
- PurchaseRequest recibe el maestro y toda su lista de detalles en una sola peticion.
- PurchaseService.save tiene @Transactional y usa PurchaseMapper, que delega los detalles a PurchaseItemMapper.
- El servicio establece la referencia a Compra de cada CompraProducto y ejecuta una sola llamada compraRepository.save(compra). CascadeType.ALL y @MapsId guardan la cabecera y propagan su ID a los detalles.
- Adicionalmente se descuenta stock bajo bloqueo. Un error revierte compra, detalles y stock.

## Preparacion en Swagger

POST /categories:
```json
{"category":"ADA 11","active":true}
```
POST /clients:
```json
{"clientId":"CLI-001","name":"Ana","lastName":"Perez","phone":9991234567,"address":"Merida","email":"ana@example.com"}
```
POST /products (usa el categoryId recibido):
```json
{"name":"Arroz ADA11","categoryId":1,"barcode":"ADA11-A","price":250,"stock":10,"active":true}
```
Crea un segundo producto activo con stock 0. A es el ID del primero y B el del segundo; B debe ser mayor que A.

## Compra exitosa

POST /purchases: sustituye idProducto por A y usa un cliente existente.
```json
{
  "idCliente":"CLI-001",
  "medioPago":"TARJETA",
  "comentario":"Entregar por la tarde",
  "productosAdquiridos":[{"idProducto":1,"cantidad":2,"total":500.0}]
}
```
Debe responder 201 con purchaseId y detalle. GET /products/{A} debe mostrar stock 8; GET /purchases debe mostrar la compra y sus detalles.

El DTO admite total y lo guarda como solicita la consigna. Si se omite, se calcula con el precio actual y la cantidad. Fecha y estado P los asigna el servidor. La respuesta conserva el modelo de dominio en ingles.

La columna local medio_pago es CHAR(1): TARJETA se guarda como T, EFECTIVO como E y TRANSFERENCIA como B. No se modifica la tabla. Los nombres anteriores en ingles siguen funcionando como alias de entrada.

## Rollback desde Swagger

Anota el stock de A y la lista GET /purchases. Envia A y B reales:
```json
{
  "idCliente":"CLI-001",
  "medioPago":"TARJETA",
  "comentario":"ADA11 rollback",
  "productosAdquiridos":[
    {"idProducto":1,"cantidad":1,"total":250.0},
    {"idProducto":2,"cantidad":1,"total":150.0}
  ]
}
```
Debe responder 409 por stock insuficiente en B. El descuento de A ya se envio a SQL pero se revierte: conserva stock 8 y no aparece una compra nueva ni detalles parciales.

## Verificacion

`.\gradlew.bat build` ejecuta pruebas HTTP integrales sobre H2 en modo PostgreSQL, sin modificar los datos locales. Comprueba los CRUD, el JSON en espanol, TARJETA, el total suministrado, cascada y dos escenarios de rollback:

1. Falla el segundo producto por falta de stock y se revierte el primer descuento.
2. Una restriccion de prueba falla al insertar el segundo detalle: se revierten la cabecera, el primer detalle y ambos descuentos. Esa restriccion existe solo en la base de pruebas.
