package dgtc.service.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import dgtc.common.enums.StatusRegisterEnum;
import dgtc.common.exception.*;
import dgtc.common.genid.GenIdUtil;
import dgtc.entity.datasource.admin.Auction;
import dgtc.entity.datasource.admin.AuctionRegister;
import dgtc.entity.datasource.admin.AuctionRegisterId;
import dgtc.entity.datasource.admin.Property;
import dgtc.entity.datasource.user.AuctionRegisterNotification;
import dgtc.entity.datasource.user.User;
import dgtc.entity.dto.handler.notification.AuctionRegNotificationResponse;
import dgtc.entity.dto.handler.notification.NotificationResponse;
import dgtc.pers.UserPersistence;
import dgtc.repository.admin.AuctionRegisterRepository;
import dgtc.repository.notification.AuctionRegisterNotificationRepository;
import dgtc.repository.notification.FirebaseNotificationRepository;
import dgtc.repository.notification.NotificationRepository;
import dgtc.token.TokenManager;
import dgtc.token.UserToken;
import dgtc.utils.MessageUtils;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private static final Long THIRTY_DAYS = 2592000000L;

  @Autowired private final TokenManager tokenManager;
  @Autowired private final GenIdUtil genIdUtil;
  @Autowired private final UserPersistence userPersistence;
  @Autowired private final AuctionRegisterRepository auctionRegisterRepository;

  @Autowired
  private final AuctionRegisterNotificationRepository auctionRegisterNotificationRepository;

  @Autowired private final FirebaseNotificationRepository firebaseNotificationRepository;
  @Autowired private final NotificationRepository notificationRepository;

  public List<NotificationResponse> getNotification(String tokenHaveBearer)
      throws GetNotificationException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(tokenHaveBearer);
    if (userToken == null || userToken.isInValid()) {
      throw new GetNotificationException(MessageUtils.message("token.isExpire"));
    }
    try {
      return notificationRepository
          .findMessageByUserId(userToken.getUserId(), System.currentTimeMillis())
          .stream()
          .map(NotificationResponse::create)
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new GetNotificationException(MessageUtils.message("error.mess"));
    }
  }

  public AuctionRegNotificationResponse getNotificationById(
      Long notificationId, String typeNotification) throws GetNotificationByIdException {
    if ("auction_register_notification".equals(typeNotification)) {
      AuctionRegisterNotification auctionRegNotification =
          auctionRegisterNotificationRepository.findById(notificationId).orElse(null);
      if (auctionRegNotification == null) {
        throw new GetNotificationByIdException("notification.isnotexist");
      }
      Long auctionId = auctionRegNotification.getAuctionId();
      Long userId = auctionRegNotification.getUserId();
      AuctionRegister auctionRegister =
          auctionRegisterRepository
              .findById(AuctionRegisterId.auctionRegisterId(userId, auctionId))
              .orElse(null);

      if (auctionRegister == null) {
        throw new GetNotificationByIdException("notification.isnotexist");
      }

      Auction auction = auctionRegister.getAuctionRegister();
      if (auction == null) {
        throw new GetNotificationByIdException("notification.isnotexist");
      }

      Property property = auction.getProperty();
      if (property == null) {
        throw new GetNotificationByIdException("notification.isnotexist");
      }

      return AuctionRegNotificationResponse.create(
          property.getPropertyId(),
          StatusRegisterEnum.status(auctionRegister.getStatus()).getDescription(),
          property.getName(),
          auction);
    }
    throw new GetNotificationByIdException("notification.notfound");
  }

  @Transactional
  public void createNotificationForUsersRegisterOneAuction(
      Long auctionId, String title, String message)
      throws CreateNotificationToUserRegisterAuctionException {
    try {
      List<User> users =
          auctionRegisterRepository.usersRegisterAuctionWithStatus(
              auctionId, StatusRegisterEnum.READY.getValue());
      Long now = System.currentTimeMillis();
      List<String> tokenList = new ArrayList<>();
      List<AuctionRegisterNotification> auctionRegisterNotifications =
          users.stream()
              .map(
                  user -> {
                    if (StringUtil.isNotEmpty(user.getFirebaseToken())) {
                      tokenList.add(user.getFirebaseToken());
                    }
                    return AuctionRegisterNotification.createNew(
                        auctionId,
                        user.getUserId(),
                        genIdUtil.genUID(),
                        title,
                        message,
                        now,
                        now + THIRTY_DAYS);
                  })
              .collect(Collectors.toList());
      auctionRegisterNotificationRepository.saveAll(auctionRegisterNotifications);
      if (!tokenList.isEmpty()) {
        firebaseNotificationRepository.batchSendNotification(title, message, tokenList);
      }
    } catch (Exception e) {
      throw new CreateNotificationToUserRegisterAuctionException(
          MessageUtils.message("notification.cannotcreatenotification"));
    }
  }

  @Transactional
  public void updateReadStatus(Long notificationId, Boolean isRead)
      throws UpdateReadStatusNotificationException {
    try {
      auctionRegisterNotificationRepository.updateReadStatus(notificationId, isRead);
    } catch (Exception e) {
      throw new UpdateReadStatusNotificationException(
          MessageUtils.message("notification.updatereadstatus"));
    }
  }

  public String sendNotification(Long userID, String title, String mess)
      throws NotificationException {
    try {
      User user = userPersistence.getUserById(userID);
      if (user != null) {
        String firebaseToken = user.getFirebaseToken();
        if (TextUtils.isEmpty(firebaseToken)) {
          throw new NotificationException(MessageUtils.message("firebase.notfoundtoken"));
        }
        return firebaseNotificationRepository.sendNotification(title, mess, firebaseToken);
      }
      throw new NotificationException(MessageUtils.message("username.notexist"));
    } catch (Exception e) {
      throw new NotificationException(MessageUtils.message("firebase.sendnotierror"));
    }
  }

  public void sendTestNotification() {
    try {
      firebaseNotificationRepository.sendNotification(
          "title",
          "mess",
          "dbMngqEnTB-eaBEvuuFZuC:APA91bHn3G9EadeGPPYo7TMoCWgk-F6uiJBu4d7SfCad4p98BUhbmeIalaygdJ30W4u0JPja1lsu491YDPujRpWKsO4HcsZo8tZuD_-rPX_eJ52M09J4e8-yoxbzm8ixzN9QVGE1jdzQ");
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }
}
