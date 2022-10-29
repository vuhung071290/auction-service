package dgtc.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class NotificationConfig {

  @Value("${firebase.firebase-configuration-file}")
  private String firebaseConfigPath;

  @Value("${firebase.firebase-database-url}")
  private String firebaseDatabaseUrl;

  @PostConstruct
  public void initialize() {
    try {
      GoogleCredentials serviceAccount =
          GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream());
      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(serviceAccount)
              .setDatabaseUrl(firebaseDatabaseUrl)
              .build();

      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
