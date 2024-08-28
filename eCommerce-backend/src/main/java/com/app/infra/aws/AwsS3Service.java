package com.app.infra.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class AwsS3Service {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public AwsS3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(String fileKey, File file) throws AmazonServiceException {
        s3Client.putObject(new PutObjectRequest(bucketName, fileKey, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucketName, fileKey).toString();
    }

    public void delete(String fileKey) {
        s3Client.deleteObject(bucketName, fileKey);
    }
}
