package com.app.global.services;

import com.app.global.enums.FileFormat;
import com.app.global.exceptions.FailedToUploadFileException;
import com.app.global.utils.FileUtils;
import com.app.global.vos.Media;
import com.app.infra.aws.AwsS3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class MediaService {

    private final AwsS3Service s3Service;

    public MediaService(AwsS3Service s3Service) {
        this.s3Service = s3Service;
    }

    public List<Media> uploadAndGet(List<MultipartFile> multipartFileList) {
        return multipartFileList.stream()
                .map(this::tryUploadAndGet)
                .toList();
    }

    public Media uploadAndGet(MultipartFile multipartFile) {
        return tryUploadAndGet(multipartFile);
    }

    public void delete(String fileKey) {
        s3Service.delete(fileKey);
    }

    private Media tryUploadAndGet(MultipartFile multipartFile) {
        try {
            return UploadAndGetImpl(multipartFile);
        } catch (IOException e) {
            throw new FailedToUploadFileException();
        }
    }

    private Media UploadAndGetImpl(MultipartFile multipartFile) throws IOException {
        final String name = multipartFile.getOriginalFilename();

        final String format = FileUtils.getValidatedFileFormat(name);
        final String baseName = FilenameUtils.getBaseName(name);
        final FileFormat fileFormat = FileFormat.valueOf(format);
        final String key = UUID.randomUUID().toString();

        final File file = multipartToFile(multipartFile);
        try {
            String url = s3Service.upload(key, file);
            return new Media(baseName, key, url, fileFormat);
        } finally {
            deleteTempFile(file);
        }
    }

    private static File multipartToFile(MultipartFile multipart) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }

    private static void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                throw new IllegalStateException("Failed to delete file."); // TODO
            }
        }
    }
}
