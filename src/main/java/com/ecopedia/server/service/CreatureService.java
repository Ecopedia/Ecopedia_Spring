package com.ecopedia.server.service;

import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.Book;
import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.CreatureImg;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.domain.enums.CreatureCategory;
import com.ecopedia.server.dto.RequestDto;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.global.auth.MemberUtil;
import com.ecopedia.server.repository.BookRepository;
import com.ecopedia.server.repository.CreatureImgRepository;
import com.ecopedia.server.repository.CreatureRepository;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.service.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecopedia.server.dto.RequestDto.*;
import static com.ecopedia.server.dto.ResponseDto.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CreatureService {

    private final MemberUtil memberUtil;
    private final BookRepository bookRepository;
    private final CreatureRepository creatureRepository;
    private final CreatureImgRepository creatureImgRepository;

    public void saveCreature(String authHeader, CreatureSaveRequestDto dto) {
        Member member = memberUtil.getMemberFromToken(authHeader);

        Book book = bookRepository.findByMemberIdx(member.getIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.BOOK_NOT_FOUND));

        // 1. Creature 저장
        Creature creature = Creature.builder()
                .creatureName(dto.getCreatureName())
                .creatureExplain(dto.getCreatureExplain())
                .category(CreatureCategory.valueOf(dto.getCategory().toUpperCase()))
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .book(book)
                .build();
        creatureRepository.save(creature);

        // 2. CreatureImg 저장
        CreatureImg creatureImg = CreatureImg.builder()
                .imageKey(dto.getImageKey())
                .imageUrl(dto.getImageUrl())
                .creature(creature)
                .build();
        creatureImgRepository.save(creatureImg);
    }


    public CreatureDetailResponseDto getCreatureDetail(Long creatureIdx) {
        Creature creature = creatureRepository.findById(creatureIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CREATURE_NOT_FOUND));

        String imageUrl = creatureImgRepository.findByCreature(creature)
                .map(img -> img.getImageUrl())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.INVALID_IMAGE_FILE));

        return CreatureDetailResponseDto.builder()
                .creatureIdx(creature.getIdx())
                .creatureName(creature.getCreatureName())
                .creatureExplain(creature.getCreatureExplain())
                .latitude(creature.getLatitude())
                .longitude(creature.getLongitude())
                .category(creature.getCategory())
                .imageUrl(imageUrl)
                .build();
    }

}
