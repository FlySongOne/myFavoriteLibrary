package com.example.libraryapi.repositories;

import com.example.libraryapi.models.Library;
import com.google.common.collect.Iterables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LibraryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LibraryRepository libraryRepository;

    @Before
    public void setUp() {
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
        entityManager.clear();
        entityManager.flush();
//        entityManager.persist(firstLibrary);
//        entityManager.persist(secondLibrary);
        entityManager.flush();
    }


    @Test
    public void findAll_returnsAllLibraries() {
        Iterable<Library> libraryFromDb = libraryRepository.findAll();

        assertThat(Iterables.size(libraryFromDb), is(2));
    }

    @Test
    public void findAll_returnsLibraryName() {
        Iterable<Library> libraryFromDb = libraryRepository.findAll();

        String secondLibraryName = Iterables.get(libraryFromDb, 1).getName();

        assertThat(secondLibraryName, is("secondLib"));
    }

    @Test
    public void findAll_returnsAddress() {
        Iterable<Library> libraryFromDb = libraryRepository.findAll();

        String secondLibraryAddress = Iterables.get(libraryFromDb, 1).getAddress();

        assertThat(secondLibraryAddress, is("334-11 43st"));
    }

    @Test
    public void findAll_returnsCity() {
        Iterable<Library> libraryFromDb = libraryRepository.findAll();

        String secondLibraryCity = Iterables.get(libraryFromDb, 1).getCity();

        assertThat(secondLibraryCity, is("New York"));
    }

    @Test
    public void findAll_returnsPostalCode() {
        Iterable<Library> libraryFromDb = libraryRepository.findAll();

        String secondLibraryPostalCode = Iterables.get(libraryFromDb, 1).getPostalCode();

        assertThat(secondLibraryPostalCode, is("12346"));
    }


}
