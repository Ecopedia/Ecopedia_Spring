package com.ecopedia.server.repository;

import com.ecopedia.server.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
