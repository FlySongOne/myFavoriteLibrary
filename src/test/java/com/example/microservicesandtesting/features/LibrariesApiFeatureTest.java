package com.example.microservicesandtesting.features;

import com.example.microservicesandtesting.models.Library;
import com.example.microservicesandtesting.repostitories.LibraryRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;


import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LibrariesApiFeatureTest {

    @Autowired
    private LibraryRepository libraryRepository;

    @Before
    public void setUp() {
        libraryRepository.deleteAll();
    }

    @After
    public void tearDown() {
        libraryRepository.deleteAll();
    }

    @Test
    public void shouldAllowFullCrudForALibrary() throws Exception {
        Library firstLibrary = new Library(
                "firstLib",
             "123-32 23st",
                "New York",
                "12345"
        );

        Library secondLibrary = new Library(
                "secondLib",
                "334-11 43st",
                "New York",
                "12346"
        );

        Stream.of(firstLibrary, secondLibrary)
                .forEach(library -> {
                    libraryRepository.save(library);
                });

        when()
                .get("http://localhost:8080/libraries/")
                .then()
                .statusCode(is(200))
                .and().body(containsString("firstLib"))
                .and().body(containsString("secondLib"));

        // Test creating a Library
        Library libraryNotYetInDb = new Library(
                "new_library",
                "our street",
                "NY",
                "00000"
        );

        given()
                .contentType(JSON)
                .and().body(libraryNotYetInDb)
                .when()
                .post("http://localhost:8080/libraries")
                .then()
                .statusCode(is(200))
                .and().body(containsString("new_library"));

        // Test get all Users
        when()
                .get("http://localhost:8080/libraries/")
                .then()
                .statusCode(is(200))
                .and().body(containsString("firstLib"))
                .and().body(containsString("secondLib"))
                .and().body(containsString("new_library"));

        // Test finding one user by ID
        when()
                .get("http://localhost:8080/libraries/" + secondLibrary.getId())
                .then()
                .statusCode(is(200))
                .and().body(containsString("secondLib"))
                .and().body(containsString("334-11 43st"));

        // Test updating a library
        secondLibrary.setAddress("changed_address");

        given()
                .contentType(JSON)
                .and().body(secondLibrary)
                .when()
                .patch("http://localhost:8080/libraries/" + secondLibrary.getId())
                .then()
                .statusCode(is(200))
                .and().body(containsString("changed_address"));

        // Test deleting a user
        when()
                .delete("http://localhost:8080/libraries/" + secondLibrary.getId())
                .then()
                .statusCode(is(200));
    }


}
