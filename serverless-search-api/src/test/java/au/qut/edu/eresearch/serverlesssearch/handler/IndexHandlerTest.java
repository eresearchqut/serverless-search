package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
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
@TestProfile(IndexHandlerTestProfile.class)
public class IndexHandlerTest {

    @Inject
    IndexService indexService;

    @Test
    public void getDocument()  {

        // Given

        indexService.index(List.of(
                new IndexRequest().setIndex("gettable")
                        .setDocument(Map.of("firstName", "John", "lastName", "McClane"))
                        .setId("jmclane")

        ));


        given()
                .auth().oauth2(Jwt
                        .claim("scope", "index/get")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .when()
                .get("/gettable/_doc/jmclane")
                .then()
                .statusCode(200)
                .body("_index", equalTo("gettable"))
                .body("_id", equalTo("jmclane"))
                .body("_source.firstName", equalTo("John"))
                .body("_source.lastName", equalTo("McClane"));

    }


    @Test
    public void indexPut()  {

        // Given

        given()
                .auth().oauth2(Jwt
                        .claim("scope", "index/put")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .body(Map.of("firstName", "Clark", "lastName", "Kent"))
                .when()
                .put("/superheros-alias/_doc/superman")
                .then()
                .statusCode(200)
                .body("_index", equalTo("superheros-alias"))
                .body("_id", equalTo("superman"));


    }




}