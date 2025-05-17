package com.ecopedia.server.web.dto.s3;

import lombok.Data;

@Data
public class S3ImageReturnDto {
    public String imageUrl;
    public String imageKey;
}
