package com.ecopedia.server.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.service.HomeService;
import com.ecopedia.server.web.dto.HomeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 데이터 조회 API", description = "홈 화면의 데이터들을 불러옵니다.")
    @GetMapping("/home")
    public ApiResponse<HomeResponseDto> getHome(@RequestHeader("Authorization") String authHeader) {
        return ApiResponse.onSuccess(homeService.getHomeData(authHeader));
    }

    @Operation(summary = "나무 후원 API", description = "나무를 한 그루씩 후원합니다.")
    @PostMapping("/donation/trees")
    public ApiResponse<HomeResponseDto.DonationResult> donateTree(@RequestHeader("Authorization") String authHeader) {
        return ApiResponse.onSuccess(homeService.donateTree(authHeader));
    }
}
