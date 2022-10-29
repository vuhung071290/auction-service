package dgtc.controller.common;

import dgtc.common.exception.*;
import dgtc.entity.base.Response;
import dgtc.entity.dto.handler.noti.NotificationRequestData;
import dgtc.entity.dto.handler.notification.AuctionRegNotificationResponse;
import dgtc.entity.dto.handler.notification.NotificationReadRequestData;
import dgtc.entity.dto.handler.notification.NotificationResponse;
import dgtc.entity.dto.handler.notification.NotificationToUserAuctionRequestData;
import dgtc.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification")
@ResponseBody
@RequiredArgsConstructor
public class NotificationController {

  @Autowired private final NotificationService notificationService;

  @PostMapping("/user/{userId}")
  public Response notification(
      @PathVariable Long userId, @Valid @RequestBody NotificationRequestData req) {
    try {
      String mess = notificationService.sendNotification(userId, req.getTitle(), req.getMessage());
      return new Response(mess);
    } catch (NotificationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping("/user-registered-auction")
  public Response createNotificationForUserRegisterAuction(
      @Valid @RequestBody NotificationToUserAuctionRequestData req) {
    try {
      notificationService.createNotificationForUsersRegisterOneAuction(
          req.getAuctionId(), req.getTitle(), req.getMessage());
      return new Response(HttpStatus.OK.value(), null);
    } catch (CreateNotificationToUserRegisterAuctionException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @GetMapping("/me")
  public Response getNotificationOfOneUser(
      @RequestHeader(name = "Authorization") String tokenHaveBearer) {
    try {
      List<NotificationResponse> res = notificationService.getNotification(tokenHaveBearer);
      return new Response(res);
    } catch (GetNotificationException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @GetMapping("/{notificationId}")
  public Response getNotificationById(
      @PathVariable Long notificationId, @RequestParam String typeNotification) {
    try {
      AuctionRegNotificationResponse res =
          notificationService.getNotificationById(notificationId, typeNotification);
      return new Response(res);
    } catch (GetNotificationByIdException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @PostMapping("/{notificationId}/read")
  public Response readNotification(
      @PathVariable Long notificationId, @Valid @RequestBody NotificationReadRequestData req) {
    try {
      notificationService.updateReadStatus(notificationId, req.getIsRead());
      return new Response(null);
    } catch (UpdateReadStatusNotificationException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }
}
