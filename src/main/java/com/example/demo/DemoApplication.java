package com.example.demo;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner initBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() == 0) {
                bookRepository.save(new Book(null, "Les Misérables", "Victor Hugo", "Roman",
                        new GregorianCalendar(1862, Calendar.JANUARY, 1).getTime(), 5));
                bookRepository.save(new Book(null, "1984", "George Orwell", "Science-fiction",
                        new GregorianCalendar(1949, Calendar.JUNE, 8).getTime(), 3));
                bookRepository.save(new Book(null, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte",
                        new GregorianCalendar(1943, Calendar.APRIL, 6).getTime(), 8));
                bookRepository.save(new Book(null, "L'Étranger", "Albert Camus", "Roman",
                        new GregorianCalendar(1942, Calendar.MAY, 19).getTime(), 4));
            }
        };
    }
}