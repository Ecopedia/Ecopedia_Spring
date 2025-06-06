package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Creature;
import com.ecopedia.server.domain.enums.CreatureCategory;
import com.ecopedia.server.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreatureRepository extends JpaRepository<Creature, Long> {

    List<Creature> findAllByBookId(Long bookId);

    List<Creature> findAllByBookIdAndCategory(Long bookId, CreatureCategory category);
    List<Creature> findByBook_Member(Member member);


}
