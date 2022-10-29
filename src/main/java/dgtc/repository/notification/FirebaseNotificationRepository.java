package dgtc.repository.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FirebaseNotificationRepository {
  public String sendNotification(String title, String message, String firebaseToken)
      throws FirebaseMessagingException {
    Message messageFire =
        Message.builder()
            .putData("title", title)
            .putData("message", message)
            .setToken(firebaseToken)
            .build();
    return FirebaseMessaging.getInstance().send(messageFire);
  }

  public BatchResponse batchSendNotification(
      String title, String message, List<String> firebaseToken) throws FirebaseMessagingException {
    List<Message> messages =
        firebaseToken.stream()
            .map(
                token ->
                    Message.builder()
                        .putData("title", title)
                        .putData("message", message)
                        .setToken(token)
                        .build())
            .collect(Collectors.toList());
    return FirebaseMessaging.getInstance().sendAll(messages);
  }
}
