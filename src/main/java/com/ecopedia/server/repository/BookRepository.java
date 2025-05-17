package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
