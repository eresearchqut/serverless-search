package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
@TestProfile(SearchHandlerTestProfile.class)
public class SearchHandlerTest {

    @Inject
    IndexService indexService;

    @Test
    public void searchByParam()  {


        List<IndexRequest> indexRequests = List.of(
                new IndexRequest().setIndex("searchable")
                        .setDocument(Map.of("firstName", "Don", "lastName", "Johnson"))
                        .setId("djohnson"),
                new IndexRequest().setIndex("searchable")
                        .setDocument(Map.of("firstName", "Don", "lastName", "Draper"))
                        .setId("ddraper")
        );

        indexService.index(indexRequests);


        given()
                .auth().oauth2(Jwt
                        .claim("scope", "search/all")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:draper")
                .when()
                .get("/searchable/_search")
                .then()
                .log().body()
                .statusCode(200)
                .body("hits.hits[0]._source.lastName", equalTo("Draper"));
    }

    @Test
    public void searchByQueryDsl()  {


        List<IndexRequest> indexRequests = List.of(
                new IndexRequest().setIndex("michaels")
                        .setDocument(Map.of("firstName", "Michael", "lastName", "Keaton"))
                        .setId("mkeaton"),
                new IndexRequest().setIndex("michaels")
                        .setDocument(Map.of("firstName", "Michael", "lastName", "Jordan"))
                        .setId("mjordon")
        );

        indexService.index(indexRequests);


        given()
                .auth().oauth2(Jwt
                        .claim("scope", "search/all")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .body(Map.of("query", Map.of("match", Map.of("lastName", "Jordan"))))
                .when()
                .get("/michaels/_search")
                .then()
                .log().body()
                .statusCode(200)
                .body("hits.hits[0]._source.lastName", equalTo("Jordan"));
    }

    @Test
    public void searchIndexNotFound()  {
        given()
                .auth().oauth2(Jwt
                        .claim("scope", "search/get")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:should-not-be-found")
                .when()
                .get("/no-index/_search")
                .then()
                .log().body()
                .statusCode(404)
                .body( equalTo("no such index [no-index]"));
    }

    @Test
    public void searchInvalidIndexName() {
        given()
                .auth().oauth2(Jwt
                        .claim("scope", "search/get")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "Invalid index name")
                .when()
                .get("/_Invalid*/_search")
                .then()
                .statusCode(400)
                .body( equalTo("Invalid index name. Index names should be less than 128 characters. All letters must be lowercase. Index names can???t begin with underscores (_) or hyphens (-).Index names can???t contain spaces, commas, or the following characters: :, \", *, +, /, \\, |, ?, #, >, or <"));
    }

    @Test
    public void searchInvalidRole()  {
        given()
                .auth().oauth2(Jwt
                        .claim("scope", "index/all")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:should-not-be-permitted")
                .when()
                .get("/not-authed/_search")
                .then()
                .log().body()
                .statusCode(403);
    }


}