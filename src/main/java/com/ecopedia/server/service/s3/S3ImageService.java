package com.ecopedia.server.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ecopedia.server.web.dto.s3.S3ImageReturnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final AmazonS3 client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    private String defaultUrl = "https://ecopedia-r.s3.ap-northeast-2.amazonaws.com/";

    public S3ImageReturnDto uploadFile(MultipartFile file) throws IOException {
        String originFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String storeFilename = uuid + originFilename;
        S3ImageReturnDto s3ImageReturnDto = new S3ImageReturnDto();

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, storeFilename, file.getInputStream(), getObjectMetaData(file))
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(putObjectRequest);
            s3ImageReturnDto.setImageUrl(defaultUrl + storeFilename);
            s3ImageReturnDto.setImageKey(storeFilename);
            return s3ImageReturnDto;
        }catch (SdkClientException e) {
            s3ImageReturnDto.setImageUrl("");
            s3ImageReturnDto.setImageKey("");
            return s3ImageReturnDto;
        }
    }

    public ObjectMetadata getObjectMetaData(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    public String deleteFile(String key) {
        String bucket = "ecopedia-r";
        String result = "";
        if(client.doesObjectExist(bucket, key)) {
            client.deleteObject(bucket, key);
            return "success";
        } else {
            return "";
        }
    }
}
