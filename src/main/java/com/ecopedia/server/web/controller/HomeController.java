package com.ecopedia.server.web.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.global.auth.JwtAuthInterceptor;
import com.ecopedia.server.service.HomeService;
import com.ecopedia.server.web.dto.HomeResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    // 홈 화면 조회 API
    @GetMapping("/home")
    public ApiResponse<HomeResponseDto> getHome(@RequestHeader("Authorization") String authHeader) {
        return ApiResponse.onSuccess(homeService.getHomeData(authHeader));
    }

    @PostMapping("/donation/trees")
    public ApiResponse<HomeResponseDto.DonationResult> donateTree(@RequestHeader("Authorization") String authHeader) {
        return ApiResponse.onSuccess(homeService.donateTree(authHeader));
    }
}
