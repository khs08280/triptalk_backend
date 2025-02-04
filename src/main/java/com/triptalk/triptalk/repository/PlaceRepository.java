package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
