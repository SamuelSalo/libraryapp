package com.samuelsalo.libraryapp.repository;

import com.samuelsalo.libraryapp.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
