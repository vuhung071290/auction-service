package dgtc.service.admin;

import dgtc.common.util.GsonUtils;
import dgtc.config.AuthConfig;
import dgtc.entity.dto.googleauth.GoogleAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

  private final AuthConfig authConfig;

  public boolean verify(String domain, String password) {
    try {
      RestTemplate template = new RestTemplate();

      // only work with form-data
      HttpHeaders header = new HttpHeaders();
      header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> resData = new LinkedMultiValueMap<>();
      resData.add("username", domain);
      resData.add("code", password);
      resData.add("type", authConfig.getGoogleAuthType());

      log.info("Google Auth Request: {}", GsonUtils.toJson(resData));
      HttpEntity<MultiValueMap> request = new HttpEntity<>(resData, header);
      String responseStr =
          template.postForObject(authConfig.getGoogleAuthUrl(), request, String.class);
      log.info("Google Auth Response: {}", responseStr);

      if (StringUtils.isEmpty(responseStr)) {
        return false;
      }

      GoogleAuthResponse response = GsonUtils.fromJson(responseStr, GoogleAuthResponse.class);
      return response.isStatus();
    } catch (Exception ex) {
      log.error("Google Auth Exception: ", ex);
    }
    return false;
  }
}
