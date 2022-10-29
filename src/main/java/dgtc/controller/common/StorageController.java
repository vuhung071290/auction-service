package dgtc.controller.common;

import dgtc.common.genid.GenIdUtil;
import dgtc.service.storage.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

  @Autowired private final GenIdUtil genIdUtil;
  @Autowired private final UploadService uploadService;
  @Value("${app.storage-domain}")
  private String storageDomain;
  @Value("${s3.buckets.images}")
  private String imagesBucket;
  @Value("${s3.buckets.files}")
  private String filesBucket;

  @PostMapping(
      value = "/image",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Map<String, String> uploadImage(@RequestPart(value = "file") MultipartFile files)
      throws Exception {
    String uid = String.valueOf(genIdUtil.genUID());
    String uploadPath =
        uploadService.uploadFile(imagesBucket, uid, files.getOriginalFilename(), files.getBytes());
    Map<String, String> result = new HashMap<>();
    result.put("uid", uid);
    result.put("link", storageDomain + "/api/storage/view-image/" + uploadPath);
    return result;
  }

  @GetMapping(value = "/view-image/**", produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<ByteArrayResource> viewImage(HttpServletRequest request) throws Exception {
    String path = request.getRequestURI().split(request.getContextPath() + "/view-image/")[1];
    byte[] data = uploadService.downloadFile(imagesBucket, path);
    ByteArrayResource resource = new ByteArrayResource(data);
    return ResponseEntity.ok()
        .contentLength(data.length)
        .header("Content-type", "image/jpeg")
        .header(
            "Content-disposition",
            "inline; filename=\"" + path.split("/")[path.split("/").length - 1] + "\"")
        .body(resource);
  }

  @PostMapping(
      value = "/file",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Map<String, String> uploadFile(@RequestPart(value = "file") MultipartFile files)
      throws Exception {
    String uid = String.valueOf(genIdUtil.genUID());
    String uploadPath =
        uploadService.uploadFile(filesBucket, uid, files.getOriginalFilename(), files.getBytes());
    Map<String, String> result = new HashMap<>();
    result.put("uid", uid);
    result.put("link", storageDomain + "/api/storage/download-file/" + uploadPath);
    return result;
  }

  @GetMapping(value = "/download-file/**", produces = "application/octet-stream")
  public ResponseEntity<ByteArrayResource> downloadFile(HttpServletRequest request)
      throws Exception {
    String path = request.getRequestURI().split(request.getContextPath() + "/download-file/")[1];
    byte[] data = uploadService.downloadFile(filesBucket, path);
    ByteArrayResource resource = new ByteArrayResource(data);

    return ResponseEntity.ok()
        .contentLength(data.length)
        .header("Content-type", "application/octet-stream")
        .header(
            "Content-disposition",
            "inline; filename=\"" + path.split("/")[path.split("/").length - 1] + "\"")
        .body(resource);
  }
}
