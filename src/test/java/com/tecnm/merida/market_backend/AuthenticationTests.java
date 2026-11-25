package com.tecnm.merida.market_backend;

import com.tecnm.merida.market_backend.domain.Client;
import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:jwt;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthenticationTests {
    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper json;
    private final HttpClient http = HttpClient.newHttpClient();

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret", () -> Base64.getEncoder().encodeToString(KEY.getEncoded()));
    }

    @BeforeEach
    void prepareClient() {
        jdbc.update("delete from clientes");
        jdbc.update("insert into clientes(id, password) values (?, ?)", "auth-test", passwordEncoder.encode("1234"));
    }

    @Test
    void loginAndProtectedProductsAreStateless() throws Exception {
        assertEquals(401, request("GET", "/products", null, null).statusCode());
        var incorrect = request("POST", "/auth/login", "{\"idCliente\":\"auth-test\",\"password\":\"wrong\"}", null);
        var missing = request("POST", "/auth/login", "{\"idCliente\":\"missing\",\"password\":\"wrong\"}", null);
        assertEquals(401, incorrect.statusCode());
        assertEquals(401, missing.statusCode());
        assertEquals(incorrect.body(), missing.body());
        assertEquals(401, request("POST", "/auth/login", "{}", null).statusCode());
        assertEquals(401, request("GET", "/auth/login", null, null).statusCode());

        jdbc.update("insert into clientes(id) values ('legacy')");
        assertEquals(401, request("POST", "/auth/login", "{\"idCliente\":\"legacy\",\"password\":\"1234\"}", null).statusCode());
        String token = login("auth-test", "1234");
        var claims = Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
        assertEquals("HS256", claims.getHeader().getAlgorithm());
        assertEquals("auth-test", claims.getPayload().getSubject());
        assertEquals(3600000, claims.getPayload().getExpiration().getTime() - claims.getPayload().getIssuedAt().getTime());
        assertFalse(claims.getPayload().containsKey("password"));
        var products = request("GET", "/products", null, token);
        assertEquals(200, products.statusCode(), products.body());
        assertTrue(products.headers().allValues("set-cookie").isEmpty());
        assertEquals(401, request("GET", "/products", null, null).statusCode());
    }

    @Test
    void rejectsInvalidExpiredAndIncompleteTokens() throws Exception {
        String expired = Jwts.builder().subject("auth-test").expiration(Date.from(Instant.now().minusSeconds(10)))
                .signWith(KEY, Jwts.SIG.HS256).compact();
        String wrongKey = Jwts.builder().subject("auth-test").expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(Jwts.SIG.HS256.key().build()).compact();
        String noExpiry = Jwts.builder().subject("auth-test").signWith(KEY, Jwts.SIG.HS256).compact();
        String noSubject = Jwts.builder().expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(KEY, Jwts.SIG.HS256).compact();
        for (String token : new String[]{"invalid", "", expired, wrongKey, noExpiry, noSubject}) {
            assertEquals(401, request("GET", "/products", null, token).statusCode());
        }
    }

    @Test
    void clientPasswordsAreHashedPreservedAndNeverReturned() throws Exception {
        String token = login("auth-test", "1234");
        var created = request("POST", "/clients", "{\"clientId\":\"new-client\",\"name\":\"Ana\",\"password\":\"1234\"}", token);
        assertEquals(201, created.statusCode(), created.body());
        assertNoPassword(created.body());
        String firstHash = hash("new-client");
        assertTrue(passwordEncoder.matches("1234", firstHash));
        assertNotEquals("1234", firstHash);
        assertNotEquals(hash("auth-test"), firstHash);

        var updated = request("PUT", "/clients/new-client", "{\"name\":\"Actualizada\"}", token);
        assertEquals(200, updated.statusCode(), updated.body());
        assertNoPassword(updated.body());
        assertEquals(firstHash, hash("new-client"));
        assertEquals("Actualizada", json.readTree(updated.body()).get("name").asText());
        assertEquals(200, request("PUT", "/clients/new-client", "{\"password\":null}", token).statusCode());
        assertEquals(firstHash, hash("new-client"));
        assertEquals(400, request("PUT", "/clients/new-client", "{\"password\":\" \"}", token).statusCode());
        assertEquals(400, request("PUT", "/clients/new-client", json.writeValueAsString(java.util.Map.of("password", "a".repeat(73))), token).statusCode());
        assertEquals(firstHash, hash("new-client"));

        var changed = request("PUT", "/clients/new-client", "{\"password\":\"5678\"}", token);
        assertEquals(200, changed.statusCode(), changed.body());
        assertNoPassword(changed.body());
        assertTrue(passwordEncoder.matches("5678", hash("new-client")));
        assertFalse(passwordEncoder.matches("1234", hash("new-client")));
        assertEquals(401, request("POST", "/auth/login", "{\"idCliente\":\"new-client\",\"password\":\"1234\"}", null).statusCode());
        login("new-client", "5678");
        assertNoPassword(request("GET", "/clients/new-client", null, token).body());
        assertNoPassword(request("GET", "/clients", null, token).body());

        var entity = new Cliente();
        entity.setPassword(firstHash);
        assertNoPassword(json.writeValueAsString(entity));
        assertNoPassword(json.writeValueAsString(new Client("test", null, null, null, null, null, "1234")));
    }

    @Test
    void swaggerIsPublicAndDeclaresBearerAuthentication() throws Exception {
        assertEquals(302, request("GET", "/swagger-ui.html", null, null).statusCode());
        assertEquals(200, request("GET", "/swagger-ui/index.html", null, null).statusCode());
        assertEquals(200, request("GET", "/v3/api-docs/swagger-config", null, null).statusCode());
        var response = request("GET", "/v3/api-docs", null, null);
        assertEquals(200, response.statusCode());
        var document = json.readTree(response.body());
        assertEquals("http", document.at("/components/securitySchemes/bearerAuth/type").asText());
        assertEquals("bearer", document.at("/components/securitySchemes/bearerAuth/scheme").asText());
        assertTrue(document.at("/security/0").has("bearerAuth"));
        var loginSecurity = document.at("/paths/~1auth~1login/post/security");
        assertTrue(loginSecurity.isArray() && loginSecurity.isEmpty());
        assertTrue(document.at("/components/schemas/Client/properties/password/writeOnly").asBoolean());
    }

    private String login(String clientId, String password) throws Exception {
        var response = request("POST", "/auth/login", json.writeValueAsString(java.util.Map.of("idCliente", clientId, "password", password)), null);
        assertEquals(200, response.statusCode(), response.body());
        assertTrue(response.headers().allValues("set-cookie").isEmpty());
        var body = json.readTree(response.body());
        assertEquals(1, body.size());
        return body.get("token").asText();
    }

    private String hash(String clientId) {
        return jdbc.queryForObject("select password from clientes where id=?", String.class, clientId);
    }

    private void assertNoPassword(String body) {
        assertFalse(body.contains("password"), body);
        assertFalse(body.contains("$2"), body);
        assertFalse(body.contains("1234"), body);
    }

    private HttpResponse<String> request(String method, String path, String body, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return http.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}
