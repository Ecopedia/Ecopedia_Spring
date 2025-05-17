package com.ecopedia.server.web.controller;

import com.ecopedia.server.apiPayload.ApiResponse;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.service.HomeService;
import com.ecopedia.server.web.dto.HomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    // 홈 화면 조회 API
    @GetMapping("/home")
    public ApiResponse<HomeResponseDto> getHome(Member member) {

        return ApiResponse.onSuccess(homeService.getHomeData(member));
    }

    @PostMapping("/donation/trees")
    public ApiResponse<HomeResponseDto.DonationResult> donateTree(Member member) {

        return ApiResponse.onSuccess(homeService.donateTree(member));
    }
}
