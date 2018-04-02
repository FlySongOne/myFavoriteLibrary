package com.example.libraryapi.controllers;


import com.example.libraryapi.models.Library;
import com.example.libraryapi.repositories.LibraryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(LibrariesController.class)
public class LibrariesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Library newLibrary;

    private Library updatedSecondLibrary;


    @Autowired
    private ObjectMapper jsonObjectMapper;


    @MockBean
    private LibraryRepository mockLibraryRepository;

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

        newLibrary = new Library(
                "new_library_for_create",
                "seasame st",
                "New York",
                "34567"
        );
        given(mockLibraryRepository.save(newLibrary)).willReturn(newLibrary);

        updatedSecondLibrary = new Library(
                "secondLib",
                "334-11 43st",
                "Boston",
                "12346"
        );
        given(mockLibraryRepository.save(updatedSecondLibrary)).willReturn(updatedSecondLibrary);

        Iterable<Library> mockLibrary =
                Stream.of(firstLibrary, secondLibrary).collect(Collectors.toList());

        given(mockLibraryRepository.findAll()).willReturn(mockLibrary);
        given(mockLibraryRepository.findOne(1L)).willReturn(firstLibrary);
        given(mockLibraryRepository.findOne(4L)).willReturn(null);

        doAnswer(invocation -> {
            throw new EmptyResultDataAccessException("ERROR MESSAGE FROM MOCK!!!", 1234);
        }).when(mockLibraryRepository).delete(4L);


    }

    @Test
    public void findAllLibraries_success_returnsStatusOK() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllLibraries_success_returnAllLibrariesAsJSON() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void findAllLibraries_success_returnLibraryNameForEachLibrary() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(jsonPath("$[0].name", is("firstLib")));
    }

    @Test
    public void findAllLibraries_success_returnAddressForEachLibrary() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(jsonPath("$[0].address", is("123-32 23st")));
    }

    @Test
    public void findAllLibraries_success_returnCityForEachLibrary() throws Exception {

        this.mockMvc
                .perform(get("/"))
                .andExpect(jsonPath("$[0].city", is("New York")));
    }

    @Test
    public void findLibraryById_success_returnsStatusOK() throws Exception {

        this.mockMvc
                .perform(get("/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void findLibraryById_success_returnName() throws Exception {

        this.mockMvc
                .perform(get("/1"))
                .andExpect(jsonPath("$.name", is("firstLib")));
    }

    @Test
    public void findLibraryById_success_returnAddress() throws Exception {

        this.mockMvc
                .perform(get("/1"))
                .andExpect(jsonPath("$.address", is("123-32 23st")));
    }

    @Test
    public void findLibraryById_success_returnCity() throws Exception {

        this.mockMvc
                .perform(get("/1"))
                .andExpect(jsonPath("$.city", is("New York")));
    }

    @Test
    public void findLibraryById_failure_libraryNotFoundReturns404() throws Exception {

        this.mockMvc
                .perform(get("/4"))
                .andExpect(status().reason(containsString("Library with ID of 4 was not found!")));
    }

    @Test
    public void deleteLibraryById_success_returnsStatusOk() throws Exception {

        this.mockMvc
                .perform(delete("/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteLibraryById_success_deletesViaRepository() throws Exception {

        this.mockMvc.perform(delete("/1"));

        verify(mockLibraryRepository, times(1)).delete(1L);
    }

    @Test
    public void deleteLibraryById_failure_userNotFoundReturns404() throws Exception {

        this.mockMvc
                .perform(delete("/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createLibrary_success_returnsStatusOk() throws Exception {

        this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(newLibrary))
                )
                .andExpect(status().isOk());
    }

    @Test
    public void createLibrary_success_returnsName() throws Exception {

        this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(newLibrary))
                )
                .andExpect(jsonPath("$.name", is("new_library_for_create")));
    }

    @Test
    public void createLibrary_success_returnsAddress() throws Exception {

        this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(newLibrary))
                )
                .andExpect(jsonPath("$.address", is("seasame st")));
    }

    @Test
    public void createLibrary_success_returnsCity() throws Exception {

        this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(newLibrary))
                )
                .andExpect(jsonPath("$.city", is("New York")));
    }

    @Test
    public void updateLibraryById_success_returnsStatusOk() throws Exception {

        this.mockMvc
                .perform(
                        patch("/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(status().isOk());
    }

    @Test
    public void updateLibraryById_success_returnsUpdatedName() throws Exception {

        this.mockMvc
                .perform(
                        patch("/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(jsonPath("$.name", is("secondLib")));
    }

    @Test
    public void updateLibraryById_success_returnsUpdatedAddress() throws Exception {

        this.mockMvc
                .perform(
                        patch("/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(jsonPath("$.address", is("334-11 43st")));
    }

    @Test
    public void updateLibraryById_success_returnsUpdatedCity() throws Exception {

        this.mockMvc
                .perform(
                        patch("/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(jsonPath("$.city", is("Boston")));
    }

    @Test
    public void updateLibraryById_failure_libraryNotFoundReturns404() throws Exception {

        this.mockMvc
                .perform(
                        patch("/4")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateLibraryById_failure_LibraryNotFoundReturnsNotFoundErrorMessage() throws Exception {

        this.mockMvc
                .perform(
                        patch("/4")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectMapper.writeValueAsString(updatedSecondLibrary))
                )
                .andExpect(status().reason(containsString("Library with ID of 4 was not found!")));
    }



}

