package com.example.libraryapi.repositories;

import com.example.libraryapi.models.Library;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public interface LibraryRepository extends CrudRepository<Library, Long> {

}

