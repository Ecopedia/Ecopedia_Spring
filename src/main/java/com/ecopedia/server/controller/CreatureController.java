package com.ecopedia.server.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.converter.VerifyImgToImgConverter;
import com.ecopedia.server.dto.RequestDto;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.service.CreatureService;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.service.s3.S3ImageService;
import com.ecopedia.server.web.dto.ai.VerifyImageReturnDto;
import com.ecopedia.server.web.dto.s3.S3ImageReturnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ecopedia.server.apiPayload.code.status.ErrorStatus;

import java.io.IOException;

import static com.ecopedia.server.dto.RequestDto.*;
import static com.ecopedia.server.dto.ResponseDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/creature")
public class CreatureController {

    private final S3ImageService s3ImageService;
    private final VerifyImageService verifyImageService;
    private final CreatureService creatureService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndAnalyzeImage(@RequestPart("file") MultipartFile file,
                                                   String latitude, String longitude) throws IOException {
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

            if (result.category == null || result.name == null || result.description == null) {
                s3ImageService.deleteFile(s3Dto.imageKey);
                throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
            }

            ImgDto imgDto = VerifyImgToImgConverter.verifyImgToImgConverter(result);
            return ResponseEntity.ok(ApiResponse.onSuccess(imgDto));

        } catch (IOException e) {
            throw new ErrorHandler(ErrorStatus.S3_UPLOAD_FAILED);
        } catch (Exception e) {
            throw new ErrorHandler(ErrorStatus.AI_ANALYSIS_FAILED);
        }

    }

    @PutMapping("/save")
    public ResponseEntity<?> saveCreature(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreatureSaveRequestDto dto
    ) {
        try {
            creatureService.saveCreature(authHeader, dto);
            return ResponseEntity.ok(ApiResponse.onSuccess("생물이 저장되었습니다."));
        } catch (IllegalArgumentException e) {
            throw new ErrorHandler(ErrorStatus.INVALID_CATEGORY);
        }
    }

    @GetMapping("/{creatureIdx}")
    public ResponseEntity<?> getCreatureDetail(@PathVariable Long creatureIdx) {
        CreatureDetailResponseDto responseDto = creatureService.getCreatureDetail(creatureIdx);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }


}
