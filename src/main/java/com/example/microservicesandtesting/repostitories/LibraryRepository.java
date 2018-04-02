package com.example.microservicesandtesting.repostitories;

import com.example.microservicesandtesting.models.Library;
import org.springframework.data.repository.CrudRepository;

public interface LibraryRepository extends CrudRepository<Library, Long> {

}
