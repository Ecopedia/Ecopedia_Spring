package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
