package com.triptalk.triptalk.domain.entity;

import com.triptalk.triptalk.domain.enums.PlaceType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Place {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @Column(name = "naver_place_id", length = 255)
//  private String naverPlaceId;

  @Column(name = "google_place_id", length = 255)
  private String googlePlaceId; // Google 고유 ID (nullable)

  @Enumerated(EnumType.STRING)
  @Column(name = "source_type", nullable = false)
  private PlaceType placeType;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "category")
  private String category;

  @Column(name = "address")
  private String address;

  @Column(name = "road_address")
  private String roadAddress;

  @Column(name = "latitude")
  private Double latitude; // Google 지오코딩 API

  @Column(name = "longitude")
  private Double longitude; // Google 지오코딩 API

  @Column(name = "mapx")
  private Integer mapx; // 네이버 mapx

  @Column(name = "mapy")
  private Integer mapy; // 네이버 mapy

  @Column(name = "rating")
  private Double rating;

  @Column(name = "website")
  private String website;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Lob
  @Column(name = "photo_urls")
  private String photoUrls;

  @Lob
  @Column(name = "types")
  private String types;

  @Lob
  @Column(name = "opening_hours")
  private String openingHours;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Schedule> schedules = new ArrayList<>();
}