package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Creature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
}
