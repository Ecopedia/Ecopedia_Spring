package com.ecopedia.server.service.location;

import com.ecopedia.server.web.dto.location.LocationReturnDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class LocationService {
    private final RestTemplate restTemplate = new RestTemplate();

    private String KAKAO_API_KEY = "4070f306858a1f80286301065d17fb34";

    public LocationReturnDto getAdministrativeDong(double latitude, double longitude) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2regioncode.json")
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                KakaoResponse.class
        );

        LocationReturnDto addressDto = new LocationReturnDto();

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            for (KakaoResponse.Document doc : response.getBody().getDocuments()) {
                if ("H".equals(doc.getRegionType())) {
                    addressDto.setSido(doc.getRegion1DepthName());
                    addressDto.setSigungu(doc.getRegion2DepthName());
                    addressDto.setDong(doc.getRegion3DepthName());
                    return addressDto;
                }
            }
        }

        addressDto.setSido("");
        addressDto.setSigungu("");
        addressDto.setDong("");
        return addressDto; // 없으면 null 반환
    }

    // 내부 DTO 클래스들

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoResponse {
        private List<Document> documents;

        // getter, setter

        public List<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Document {
            @JsonProperty("region_1depth_name")
            private String region1DepthName;   // 시도

            @JsonProperty("region_2depth_name")
            private String region2DepthName;   // 시군구

            @JsonProperty("region_3depth_name")
            private String region3DepthName;   // 동

            @JsonProperty("region_type")
            private String regionType;

            // getter, setter

            public String getRegion1DepthName() {
                return region1DepthName;
            }

            public void setRegion1DepthName(String region1DepthName) {
                this.region1DepthName = region1DepthName;
            }

            public String getRegion2DepthName() {
                return region2DepthName;
            }

            public void setRegion2DepthName(String region2DepthName) {
                this.region2DepthName = region2DepthName;
            }

            public String getRegion3DepthName() {
                return region3DepthName;
            }

            public void setRegion3DepthName(String region3DepthName) {
                this.region3DepthName = region3DepthName;
            }

            public String getRegionType() {
                return regionType;
            }

            public void setRegionType(String regionType) {
                this.regionType = regionType;
            }
        }
    }
}
