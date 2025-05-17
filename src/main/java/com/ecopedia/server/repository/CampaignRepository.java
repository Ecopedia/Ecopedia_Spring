package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query("SELECT c FROM Campaign c ORDER BY c.idx ASC")
    Optional<Campaign> findDefaultCampaign();
}
