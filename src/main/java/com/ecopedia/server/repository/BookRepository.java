package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Book;
import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByMemberIdx(Long memberIdx);
}
