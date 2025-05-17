package com.ecopedia.server.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.converter.VerifyImgToImgConverter;
import com.ecopedia.server.domain.Book;
import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.dto.RequestDto;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.global.auth.MemberUtil;
import com.ecopedia.server.repository.BookRepository;
import com.ecopedia.server.repository.CreatureRepository;
import com.ecopedia.server.service.CreatureService;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.service.location.LocationService;
import com.ecopedia.server.service.s3.S3ImageService;
import com.ecopedia.server.web.dto.ai.VerifyImageReturnDto;
import com.ecopedia.server.web.dto.location.LocationReturnDto;
import com.ecopedia.server.web.dto.s3.S3ImageReturnDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ecopedia.server.apiPayload.code.status.ErrorStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.ecopedia.server.dto.RequestDto.*;
import static com.ecopedia.server.dto.ResponseDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/creature")
public class CreatureController {

    private final MemberUtil memberUtil;
    private final S3ImageService s3ImageService;
    private final VerifyImageService verifyImageService;
    private final CreatureService creatureService;
    private final LocationService locationService;
    private final BookRepository bookRepository;
    private final CreatureRepository creatureRepository;

    @Operation(summary = "사진 검증 API")
    @PostMapping(value = "/validation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndAnalyzeImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file,
            @RequestPart("latitude") String latitude,
            @RequestPart("longitude") String longitude) throws IOException {
        if (file.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.INVALID_IMAGE_FILE);
        }

        try {
            // 1. S3 업로드
            S3ImageReturnDto s3Dto = s3ImageService.uploadFile(file);
            if (s3Dto.getImageUrl().isBlank()) {
                throw new ErrorHandler(ErrorStatus.S3_UPLOAD_FAILED);
            }

            // 2. OpenAI Vision 분석
            VerifyImageReturnDto result = verifyImageService.verifyImage(s3Dto.getImageUrl());

            if (result.getCategory().isEmpty() || result.getName().isEmpty() || result.getDescription().isEmpty()) {
                s3ImageService.deleteFile(s3Dto.imageKey);
                throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
            }

            // 3. DB 내에 동일한 book 내 동일 크리처가 존재하는지
            Member member = memberUtil.getMemberFromToken(authHeader);
            Optional<Book> bookByMemberIdx = bookRepository.findByMemberIdx(member.getIdx());
            List<Creature> creatureByBookId = creatureRepository.findAllByBookId(bookByMemberIdx.get().getId());
            for(Creature c : creatureByBookId) {
                if(c.getCreatureName().equals(result.getName())) {
                    s3ImageService.deleteFile(s3Dto.imageKey);
                    throw new ErrorHandler(ErrorStatus.CREATURE_DUPLICATION);
                }
            }
            
            s3ImageService.deleteFile(s3Dto.imageKey);
            

//            LocationReturnDto locDto = locationService.getAdministrativeDong(Double.parseDouble(latitude), Double.parseDouble(longitude));
//
//            ImgDto imgDto = VerifyImgToImgConverter.verifyImgToImgConverter(result, locDto);
            return ResponseEntity.ok(ApiResponse.onSuccess("success"));

        } catch (IOException e) {
            throw new ErrorHandler(ErrorStatus.S3_UPLOAD_FAILED);
        } catch (Exception e) {
            throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
        }

    }

    @Operation(summary = "사진 저장 API")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveCreature(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file,
            @RequestPart("latitude") String latitude,
            @RequestPart("longitude") String longitude) throws IOException {

        if (file.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.INVALID_IMAGE_FILE);
        }

        try {
            // 1. S3 업로드
            S3ImageReturnDto s3Dto = s3ImageService.uploadFile(file);
            if (s3Dto.getImageUrl().isBlank()) {
                throw new ErrorHandler(ErrorStatus.S3_UPLOAD_FAILED);
            }

            // 2. OpenAI Vision 분석
            VerifyImageReturnDto result = verifyImageService.verifyImage(s3Dto.getImageUrl());

            if (result.getCategory().isEmpty() || result.getName().isEmpty() || result.getDescription().isEmpty()) {
                s3ImageService.deleteFile(s3Dto.imageKey);
                throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
            }

            // 3. DB 내에 동일한 book 내 동일 크리처가 존재하는지
            Member member = memberUtil.getMemberFromToken(authHeader);
            Optional<Book> bookByMemberIdx = bookRepository.findByMemberIdx(member.getIdx());
            List<Creature> creatureByBookId = creatureRepository.findAllByBookId(bookByMemberIdx.get().getId());
            for(Creature c : creatureByBookId) {
                if(c.getCreatureName().equals(result.getName())) {
                    s3ImageService.deleteFile(s3Dto.imageKey);
                    throw new ErrorHandler(ErrorStatus.CREATURE_DUPLICATION);
                }
            }

            LocationReturnDto locDto = locationService.getAdministrativeDong(Double.parseDouble(latitude), Double.parseDouble(longitude));

            CreatureSaveRequestDto dto = CreatureSaveRequestDto.builder()
                    .creatureName(result.getName())
                    .creatureExplain(result.getDescription())
                    .category(result.getName())
                    .latitude(Double.parseDouble(latitude))
                    .longitude(Double.parseDouble(longitude))
                    .imageUrl(s3Dto.imageUrl)
                    .imageKey(s3Dto.imageKey)
                    .build();

            creatureService.saveCreature(authHeader, dto);
            
            ImgDto imgDto = VerifyImgToImgConverter.verifyImgToImgConverter(result, locDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(imgDto));

        } catch (IOException e) {
            throw new ErrorHandler(ErrorStatus.S3_UPLOAD_FAILED);
        } catch (Exception e) {
            throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
        }
        
    }

    @Operation(summary = "도감 세부정보 조회 API")
    @GetMapping("/{creatureIdx}")
    public ResponseEntity<?> getCreatureDetail(@PathVariable Long creatureIdx) {
        CreatureDetailResponseDto responseDto = creatureService.getCreatureDetail(creatureIdx);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }


}
