package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Book {
  @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

  @Column(nullable= false)
    private String author;

  @Column(nullable= false)
    private String title;

  private String category;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

  private Integer copiesAvailable;

}
