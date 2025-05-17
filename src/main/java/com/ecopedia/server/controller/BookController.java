package com.ecopedia.server.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.Book;
import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.domain.enums.CreatureCategory;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.global.auth.MemberUtil;
import com.ecopedia.server.repository.BookRepository;
import com.ecopedia.server.repository.CreatureRepository;
import com.ecopedia.server.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ecopedia.server.dto.ResponseDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/book")
public class BookController {

    private final MemberUtil memberUtil;

    private final CreatureRepository creatureRepository;

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<?> getMyCollection(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(value = "category", required = false) String categoryParam
    ) {
        Member member = memberUtil.getMemberFromToken(authHeader);
        Book book = bookRepository.findByMemberIdx(member.getIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.BOOK_NOT_FOUND));
        List<Creature> creatures;

        if (categoryParam != null) {
            CreatureCategory category;
            try {
                category = CreatureCategory.valueOf(categoryParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ErrorHandler(ErrorStatus.INVALID_CATEGORY);
            }

            creatures = creatureRepository.findAllByBookIdAndCategory(book.getId(), category);
        } else {
            creatures = creatureRepository.findAllByBookId(book.getId());
        }

        List<CreatureSummaryDto> result = creatures.stream()
                .map(creature -> CreatureSummaryDto.builder()
                        .idx(creature.getIdx())
                        .creatureName(creature.getCreatureName())
                        .creatureExplain(creature.getCreatureExplain())
                        .category(creature.getCategory())
                        .location(creature.getLocation())
                        .imageUrl(
                                creature.getCreatureImg() != null
                                        ? creature.getCreatureImg().getImageUrl()
                                        : ""
                        )
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
