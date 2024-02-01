package com.samuelsalo.libraryapp.entity;

import jakarta.persistence.*;
import lombok.*;

//lombok magic

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String title;
    @NonNull
    private String author;
    @NonNull
    private String description;

}
