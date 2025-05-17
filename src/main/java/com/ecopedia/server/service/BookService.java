package com.ecopedia.server.service;

import com.ecopedia.server.domain.Book;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;


    // 도감생성하기
    public Book createBookForMember(Member member) {
        Book book = Book.builder()
                .member(member)
                .build();
        return bookRepository.save(book);
    }

}
