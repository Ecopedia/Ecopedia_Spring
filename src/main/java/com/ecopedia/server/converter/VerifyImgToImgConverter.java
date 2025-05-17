package com.ecopedia.server.converter;

import com.ecopedia.server.domain.Location;
import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.web.dto.ai.VerifyImageReturnDto;
import com.ecopedia.server.web.dto.location.LocationReturnDto;

import static com.ecopedia.server.dto.ResponseDto.*;


public class VerifyImgToImgConverter {

    public static ImgDto verifyImgToImgConverter(VerifyImageReturnDto dto, LocationReturnDto locDto) {

        Location location = Location.builder()
                .si(locDto.getSido())
                .gu(locDto.getSigungu())
                .dong(locDto.getDong())
                .build();

        return ImgDto.builder()
                .creatureName(dto.name)
                .category(dto.category)
                .explain(dto.description)
                .location(location)
                .build();
    }

}
