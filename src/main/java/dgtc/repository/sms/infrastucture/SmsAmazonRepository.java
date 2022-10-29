package dgtc.repository.sms.infrastucture;

import dgtc.repository.sms.SmsRepository;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;

@Component
public class SmsAmazonRepository implements SmsRepository {
  private final SnsClient snsClient;

  public SmsAmazonRepository(SnsClient snsClient) {
    this.snsClient = snsClient;
  }

  @Override
  public boolean sendSms(String phoneNumber, String message) {
    HashMap<String, MessageAttributeValue> attributes = new HashMap<>();
    MessageAttributeValue sMSType =
        MessageAttributeValue.builder().dataType("String").stringValue("Transactional").build();
    MessageAttributeValue SenderID =
        MessageAttributeValue.builder().dataType("String").stringValue("SENDER-ID").build();

    attributes.put("AWS.SNS.SMS.SMSType", sMSType);
    attributes.put("AWS.SNS.SMS.SenderID", SenderID);
    PublishRequest publishRequest =
        PublishRequest.builder()
            .phoneNumber(phoneNumber)
            .messageAttributes(attributes)
            .message(message)
            .build();
    PublishResponse publishResponse = snsClient.publish(publishRequest);
    return publishResponse.sdkHttpResponse().isSuccessful();
  }
}
