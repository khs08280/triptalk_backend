package com.triptalk.triptalk.dto.responseDto;

import lombok.Data;

import java.util.List;

@Data
public class NaverPlaceResponseDto {
  private String lastBuildDate;
  private int total;
  private int start;
  private int display;
  private List<Item> items;

  @Data
  public static class Item {
    private String title;      // 장소명 (HTML 태그 포함)
    private String link;       // 상세 정보 URL
    private String category;   // 카테고리
    private String description;
    private String telephone;  // 전화번호
    private String address;    // 주소
    private String roadAddress; // 도로명 주소
    private String mapx;       // X 좌표 (경도)
    private String mapy;       // Y 좌표 (위도)
  }
}
