package com.ecopedia.server.service;

import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.*;
import com.ecopedia.server.domain.enums.CreatureCategory;
import com.ecopedia.server.dto.RequestDto;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.global.auth.MemberUtil;
import com.ecopedia.server.repository.BookRepository;
import com.ecopedia.server.repository.CreatureImgRepository;
import com.ecopedia.server.repository.CreatureRepository;
import com.ecopedia.server.repository.LocationRepository;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.service.location.LocationService;
import com.ecopedia.server.service.s3.S3ImageService;
import com.ecopedia.server.web.dto.location.LocationReturnDto;
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
    private final LocationRepository locationRepository;
    private final LocationService locationService;

    public void saveCreature(String authHeader, CreatureSaveRequestDto dto, Double latitude, Double longitude) {
        Member member = memberUtil.getMemberFromToken(authHeader);

        Book book = bookRepository.findByMemberIdx(member.getIdx())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.BOOK_NOT_FOUND));

        LocationReturnDto administrativeDong = locationService.getAdministrativeDong(dto.getLatitude(), dto.getLongitude());
        Location location = Location.builder()
                .si(administrativeDong.getSido())
                .gu(administrativeDong.getSigungu())
                .dong(administrativeDong.getDong())
                .build()
        ;
        Location saveLoc = locationRepository.save(location);

        System.out.println("saveLoc 저장 완료");
        System.out.println("dto.getCategory() >>>> " + dto.getCategory());

        // 1. Creature 저장
        Creature creature = Creature.builder()
                .creatureName(dto.getCreatureName())
                .creatureExplain(dto.getCreatureExplain())
                .category(CreatureCategory.valueOf(dto.getCategory().toUpperCase()))
                .location(saveLoc)
                .book(book)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        creatureRepository.save(creature);

        System.out.println("크리처 저장 완료");

        // 2. CreatureImg 저장
        CreatureImg creatureImg = CreatureImg.builder()
                .imageKey(dto.getImageKey())
                .imageUrl(dto.getImageUrl())
                .creature(creature)
                .build();
        creatureImgRepository.save(creatureImg);

        System.out.println("크리처 이미지 저장 완료");
    }


    public CreatureDetailResponseDto getCreatureDetail(Long creatureIdx) {
        Creature creature = creatureRepository.findById(creatureIdx)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CREATURE_NOT_FOUND));

        String imageUrl = creatureImgRepository.findByCreature(creature)
                .map(img -> img.getImageUrl())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.INVALID_IMAGE_FILE));

        // 위치 엔티티에서 시·구·동 추출
        Location location = creature.getLocation();
        if (location == null) {
            throw new ErrorHandler(ErrorStatus.LOCATION_NOT_FOUND); // 필요 시 에러 추가
        }

        return CreatureDetailResponseDto.builder()
                .creatureIdx(creature.getIdx())
                .creatureName(creature.getCreatureName())
                .creatureExplain(creature.getCreatureExplain())
                .category(creature.getCategory().name())
                .si(location.getSi())
                .gu(location.getGu())
                .dong(location.getDong())
                .imageUrl(imageUrl)
                .build();
    }

}
