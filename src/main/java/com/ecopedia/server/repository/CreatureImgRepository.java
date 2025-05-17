package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.CreatureImg;
import com.ecopedia.server.domain.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatureImgRepository extends JpaRepository<CreatureImg, Long> {

    Optional<CreatureImg> findByCreature(Creature creature);
}

