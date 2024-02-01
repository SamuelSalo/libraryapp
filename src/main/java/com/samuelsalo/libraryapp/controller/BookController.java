package com.samuelsalo.libraryapp.controller;

import com.samuelsalo.libraryapp.entity.Book;
import com.samuelsalo.libraryapp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Rest controller for book api
 */
@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books")
    public List<Book> retrieveAllBooks () {
        return (List<Book>) bookRepository.findAll();
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable long id)
    {
        try{
            bookRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/books")
    public ResponseEntity<Object> createBook(@RequestBody Book _book)
    {
        try{
            Book createdBook = bookRepository.save(new Book(_book.getTitle(), _book.getAuthor(), _book.getDescription()));
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable long id)
    {
        book.setId(id);

        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty())
            return ResponseEntity.notFound().build();

        try
        {
            bookRepository.save(book);
            return ResponseEntity.ok().build();

        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().build();
        }
    }
}
