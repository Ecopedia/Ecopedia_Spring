package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Donation;
import com.ecopedia.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    int countByMember(Member member);
}
