package dgtc.service.storage;

import java.io.File;
import java.util.List;

public interface AmazonS3Service {
  void uploadFile(String bucketName, String originalFilename, byte[] bytes) throws Exception;

  byte[] downloadFile(String bucketName, String fileUrl) throws Exception;

  void deleteFile(String bucketName, String fileUrl) throws Exception;

  List<String> listFiles(String bucketName) throws Exception;

  File upload(String bucketName, String name, byte[] content) throws Exception;

  byte[] getFile(String bucketName, String key) throws Exception;
}
