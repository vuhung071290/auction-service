package dgtc.service.storage.impl;

import dgtc.service.storage.AmazonS3Service;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

@Service
public class MinioS3ServiceImpl implements AmazonS3Service {

  private final MinioClient s3;

  @Autowired
  public MinioS3ServiceImpl(MinioClient s3) {
    this.s3 = s3;
  }

  @Override
  public void uploadFile(String bucketName, String originalFilename, byte[] bytes)
      throws Exception {
    File file = upload(bucketName, originalFilename, bytes);
    s3.putObject(
        bucketName,
        originalFilename,
        new FileInputStream(file),
        new PutObjectOptions(file.length(), -1));
  }

  @Override
  public byte[] downloadFile(String bucketName, String fileUrl) throws Exception {
    return getFile(bucketName, fileUrl);
  }

  @Override
  public void deleteFile(String bucketName, String fileUrl) throws Exception {
    s3.removeObject(bucketName, fileUrl);
  }

  @Override
  public List<String> listFiles(String bucketName) throws Exception {
    List<String> list = new LinkedList<>();
    Iterable<Result<Item>> buckets = s3.listObjects(bucketName);
    for (Result<Item> itemResult : buckets) {
      Item item = itemResult.get();
      System.out.println(item.userMetadata());
      System.out.println(item.objectName());
      System.out.println(item.storageClass());
      list.add(item.objectName());
    }
    return list;
  }

  @Override
  public File upload(String bucketName, String name, byte[] content) throws Exception {
    File file = new File("/tmp/" + name);
    file.canWrite();
    file.canRead();
    FileOutputStream iofs = null;
    iofs = new FileOutputStream(file);
    iofs.write(content);
    return file;
  }

  @Override
  public byte[] getFile(String bucketName, String key) throws Exception {
    InputStream stream = s3.getObject(bucketName, key);
    try {
      byte[] content = IOUtils.toByteArray(stream);
      stream.close();
      return content;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
