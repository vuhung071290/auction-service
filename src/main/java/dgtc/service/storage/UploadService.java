package dgtc.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import dgtc.common.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

  private final AmazonS3 s3;
  private AmazonS3Service s3Service;

  @Autowired
  public UploadService(AmazonS3 s3, @Qualifier("amazonS3ServiceImpl") AmazonS3Service s3Service) {
    this.s3 = s3;
    this.s3Service = s3Service;
  }

  public String uploadFile(String bucket, String uid, String originalFilename, byte[] bytes)
      throws Exception {
    File file = s3Service.upload(bucket, originalFilename, bytes);
    String generatePath = generatePath(uid, originalFilename);
    s3.putObject(bucket, generatePath, file);
    return generatePath;
  }

  public byte[] downloadFile(String bucket, String fileUrl) throws Exception {
    return s3Service.getFile(bucket, fileUrl);
  }

  private String generatePath(String uid, String fieldName) {
    Date now = new Date();
    long timestampMills = now.getTime();
    String yyyyMMdd = DateUtils.currentTimeStampToString(timestampMills, "yyyy/MM/dd");
    String hours = DateUtils.currentTimeStampToString(timestampMills, "HH");
    return yyyyMMdd + "/" + hours + "/" + uid + "." + FilenameUtils.getExtension(fieldName);
  }
}
