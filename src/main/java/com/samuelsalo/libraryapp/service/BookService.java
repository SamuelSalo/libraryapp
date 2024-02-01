package com.samuelsalo.libraryapp.service;

import com.samuelsalo.libraryapp.entity.Book;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service class for rest api requests
 */
@Service
public class BookService {

    private final WebClient webClient;

    public BookService(WebClient.Builder builder) {
        webClient = builder.baseUrl("http://localhost:8080/").build();
    }

    /**
     * Get all books
     * @return Book array
     */
    public Mono<Book[]> getBooks() {
        return webClient
                .get()
                .uri("/books")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Book[].class);
    }

    /**
     * Delete book by ID
     * @param id ID of book to delete
     * @return Consumable Mono for success & error handling
     */
    public Mono<Void> deleteBook(Long id)
    {
        return webClient
            .delete()
            .uri("/books/" + id)
            .retrieve()
            .bodyToMono(Void.class);
    }

    /**
     * Replace book by ID
     *
     * @param book Replacement book
     * @param id Book ID to update
     * @return Mono with book & throwable in case of error
     */
    public Mono<Book> updateBook(Book book, Long id)
    {
            return webClient
                    .put()
                    .uri("/books/" + id)
                    .body(Mono.just(book), Book.class)
                    .retrieve()
                    .bodyToMono(Book.class);
    }

    /**
     * Add new book
     * @param _book Book to add
     * @return Mono with book and throwable in case of error
     */
    public Mono<Book> createBook(Book _book)
    {
        return webClient
                .post()
                .uri("/books")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(_book), Book.class)
                .retrieve()
                .bodyToMono(Book.class);
    }
}