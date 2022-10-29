package dgtc.repository.sms.infrastucture;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dgtc.repository.sms.SmsRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsStringeeRepository implements SmsRepository {

  static final String API_KEY_SECRET = "N01QTW1OOW1CWGhac3A5UG5ia2QyRGpldzdaS0ZtcE4=";
  static final String API_KEY_SID = "SK3fHmfDKyGPekCpiD0xO4G2fPjcoJW85o";

  @Override
  public boolean sendSms(String phoneNumber, String message) {
    RestTemplate template = new RestTemplate();
    String token = tokenSendSms();
    // only work with form-data
    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_JSON);
    header.add("X-STRINGEE-AUTH", token);
    String requestJson =
        "{\n"
            + "    \"sms\":[\n"
            + "        {\n"
            + "            \"from\": \"84829345889\",\n"
            + "            \"to\": \"84829345889\",\n"
            + "            \"text\": \"This is an SMS from your brandname\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";

    HttpEntity<String> request = new HttpEntity<String>(requestJson, header);
    String responseStr =
        template.postForObject("https://api.stringee.com/v1/sms", request, String.class);
    return responseStr != null;
  }

  public String tokenSendSms() {
    long now = System.currentTimeMillis() / 1000;
    long exp = now + 3600;
    Algorithm algorithm = Algorithm.HMAC256(API_KEY_SECRET.getBytes());
    Map<String, Object> header = new HashMap<>();
    header.put("typ", "JWT");
    header.put("alg", "HS256");
    header.put("cty", "stringee-api;v=1");

    return JWT.create()
        .withHeader(header)
        .withClaim("jti", String.format("%s-%d", API_KEY_SID, now))
        .withClaim("iss", API_KEY_SID)
        .withClaim("exp", new Date(exp * 1000))
        .withClaim("rest_api", true)
        .sign(algorithm);
  }
}
