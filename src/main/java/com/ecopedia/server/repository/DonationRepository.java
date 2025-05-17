package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
