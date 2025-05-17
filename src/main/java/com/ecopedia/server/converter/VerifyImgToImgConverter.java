package com.ecopedia.server.converter;

import com.ecopedia.server.dto.ResponseDto;
import com.ecopedia.server.service.ai.VerifyImageService;
import com.ecopedia.server.web.dto.ai.VerifyImageReturnDto;

import static com.ecopedia.server.dto.ResponseDto.*;


public class VerifyImgToImgConverter {

    public static ImgDto verifyImgToImgConverter(VerifyImageReturnDto dto) {
        return ImgDto.builder()
                .creatureName(dto.name)
                .category(dto.category)
                .explain(dto.description)
                .build();
    }

}
