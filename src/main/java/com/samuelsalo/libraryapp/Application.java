package com.samuelsalo.libraryapp;

import com.samuelsalo.libraryapp.entity.Book;
import com.samuelsalo.libraryapp.repository.BookRepository;
import com.samuelsalo.libraryapp.service.BookService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "library-app")
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    //add test book
    @Bean
    public CommandLineRunner bookTest(BookRepository bookRepository)
    {
        return args -> {
            bookRepository.save(new Book("TestTitle", "TestAuthor", "TestDescription"));
        };
    }
}
