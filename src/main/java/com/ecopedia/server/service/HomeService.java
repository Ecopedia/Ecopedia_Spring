package com.ecopedia.server.service;

import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.Campaign;
import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.Donation;
import com.ecopedia.server.domain.CreatureImg;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.global.auth.MemberUtil;
import com.ecopedia.server.repository.*;
import com.ecopedia.server.web.dto.HomeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final CreatureRepository creatureRepository;
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final CreatureImgRepository creatureImgRepository;
    private final MemberUtil memberUtil;

    private static final int CREATURES_PER_TREE = 10;
    private static final int MONEY_PER_TREE = 5000;
    private static final String S3_BASE_URL = "https://ecopedia-r.s3.ap-northeast-2.amazonaws.com/";

    @Transactional(readOnly = true)
    public HomeResponseDto getHomeData(String authHeader) {
        Member member = memberUtil.getMemberFromToken(authHeader);
        List<Creature> creatures = creatureRepository.findByBook_Member(member);

        int savedCount = creatures.size();
        int availableTrees = savedCount / CREATURES_PER_TREE;
        int progress = savedCount % CREATURES_PER_TREE;

        int donatedCount = donationRepository.countByMember(member);
        int donatedWon = donatedCount * MONEY_PER_TREE;

        List<HomeResponseDto.RecentCreature> recent = creatures.stream()
                .sorted((a, b) -> Long.compare(b.getIdx(), a.getIdx()))
                .limit(3)
                .map(c -> {
                    String imageKey = creatureImgRepository.findFirstByCreatureIdx(c.getIdx())
                            .map(CreatureImg::getImageKey)
                            .orElse("");

                    String imageUrl = imageKey.isEmpty() ? S3_BASE_URL + imageKey : null;

                    return new HomeResponseDto.RecentCreature(c.getIdx(), c.getCreatureName(), imageUrl);
                })
                .toList();

        return new HomeResponseDto(
                new HomeResponseDto.PlantStatus(savedCount, progress, availableTrees),
                new HomeResponseDto.DonationStatus(donatedCount, donatedWon),
                recent
        );
    }

    @Transactional
    public HomeResponseDto.DonationResult donateTree(String authHeader) {

        Member member = memberUtil.getMemberFromToken(authHeader);

        List<Creature> creatures = creatureRepository.findByBook_Member(member);
        int savedCount = creatures.size();
        int availableTrees = savedCount / CREATURES_PER_TREE;
        int donatedCount = donationRepository.countByMember(member);

        if(availableTrees <= donatedCount) {
            throw new ErrorHandler(ErrorStatus.EXCEEDED_DONATION);
        }

        Campaign defaultCampaign = campaignRepository.findDefaultCampaign()
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CAMPAIGN_NOT_FOUND));

        donationRepository.save(Donation.builder()
                .member(member)
                .campaign(defaultCampaign)
                .money(MONEY_PER_TREE)
                .build());

        int updatedDonated = donatedCount +1;
        int updatedWon = updatedDonated * MONEY_PER_TREE;

        return new HomeResponseDto.DonationResult(availableTrees - donatedCount, updatedDonated, updatedWon);
    }
}