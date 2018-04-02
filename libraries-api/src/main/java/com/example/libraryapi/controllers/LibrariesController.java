package com.example.libraryapi.controllers;


import com.example.libraryapi.models.Library;
import com.example.libraryapi.repositories.LibraryRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class LibrariesController {

    @Autowired
    private LibraryRepository libraryRepository;

    @GetMapping("/")
    public Iterable<Library> findAllLibrary() {
        return libraryRepository.findAll();
    }

    @GetMapping("/{libraryId}")
    public Library findLibraryById(@PathVariable Long libraryId) throws NotFoundException {

        Library foundLibrary = libraryRepository.findOne(libraryId);

        if (foundLibrary == null) {
            throw new NotFoundException("Library with ID of " + libraryId + " was not found!");
        }


        return foundLibrary;
    }

    @DeleteMapping("/{libraryId}")
    public HttpStatus deleteUserById(@PathVariable Long libraryId) throws EmptyResultDataAccessException {
        libraryRepository.delete(libraryId);
        return HttpStatus.OK;
    }

    @PostMapping("/")
    public Library createNewLibrary(@RequestBody Library newLibrary) {
        return libraryRepository.save(newLibrary);
    }

    @PatchMapping("/{libraryId}")
    public Library updateUserById(@PathVariable Long libraryId, @RequestBody Library libraryRequest) throws NotFoundException {
        Library libraryFromDb = libraryRepository.findOne(libraryId);

        if (libraryFromDb == null) {
            throw new NotFoundException("Library with ID of " + libraryId + " was not found!");
        }

        libraryFromDb.setName(libraryRequest.getName());
        libraryFromDb.setAddress(libraryRequest.getAddress());
        libraryFromDb.setCity(libraryRequest.getCity());
        libraryFromDb.setPostalCode(libraryRequest.getPostalCode());

        return libraryRepository.save(libraryFromDb);
    }

    @ExceptionHandler
    void handleLibraryNotFound(
            NotFoundException exception,
            HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler
    void handleDeleteNotFoundException(
            EmptyResultDataAccessException exception,
            HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
