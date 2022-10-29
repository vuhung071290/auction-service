package dgtc.service.admin;

import com.pusher.rest.Pusher;
import dgtc.common.constant.DateTimeConst;
import dgtc.common.enums.*;
import dgtc.common.exception.RegisterAuctionException;
import dgtc.common.exception.StatusRegisterAuctionException;
import dgtc.common.exception.UpdateRegisterAuctionException;
import dgtc.common.genid.GenIdUtil;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.*;
import dgtc.entity.datasource.user.AuctionRegisterNotification;
import dgtc.entity.datasource.user.User;
import dgtc.entity.dto.handler.auction.*;
import dgtc.entity.dto.handler.user.UserRegisterAuctionAdminResponseData;
import dgtc.repository.admin.AuctionHistoricalRepository;
import dgtc.repository.admin.AuctionRegisterRepository;
import dgtc.repository.admin.AuctionRepository;
import dgtc.repository.cache.AuctionCache;
import dgtc.repository.notification.AuctionRegisterNotificationRepository;
import dgtc.repository.notification.FirebaseNotificationRepository;
import dgtc.repository.user.UserRepository;
import dgtc.scheduling.AuctionTask;
import dgtc.scheduling.AuctionTaskRegistrar;
import dgtc.token.TokenManager;
import dgtc.token.UserToken;
import dgtc.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

  private final AuctionRepository auctionRepo;
  private final AuctionRegisterRepository auctionRegisterRepository;
  private final AuctionTaskRegistrar auctionTaskRegistrar;
  private final AuctionCache auctionCache;
  private final Pusher pusher;
  private final GenIdUtil genIdUtil;
  private final TokenManager tokenManager;
  private final AuctionHistoricalRepository auctionHistoricalRepository;
  private final AuctionRegisterNotificationRepository auctionRegisterNotificationRepository;
  private final UserRepository userRepository;
  private final FirebaseNotificationRepository firebaseNotificationRepository;

  public ListResponseData getListAuctionResponseData(GetAuctionRequestData request) {
    try {
      Page<Auction> pageResult = searchAuction(request);
      List<GetAuctionResponseData> getAuctionResponseDataList =
          pageResult.getContent().stream()
              .map(auction -> new GetAuctionResponseData(auction))
              .collect(Collectors.toList());
      return new ListResponseData(
          getAuctionResponseDataList,
          pageResult.getTotalElements(),
          request.getPage(),
          request.getSize());
    } catch (Exception ex) {
      return null;
    }
  }

  public Page<Auction> searchAuction(GetAuctionRequestData request) {
    Sort sort =
        request.getDirection().equals("asc")
            ? Sort.by(request.getFieldDate()).ascending()
            : Sort.by(request.getFieldDate()).descending();

    Pageable paging = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
    try {
      if (StringUtils.isNotEmpty(request.getField())) {
        if (request.getFieldDate().equals("createdDate") && request.getField().equals("status")) {
          return auctionRepo.searchAuctionByCreatedDateAndStatus(
              request.getStartDateSearch(),
              request.getEndDateSearch(),
              Integer.parseInt(request.getSearch()),
              paging);
        }
        if (request.getFieldDate().equals("createdDate") && request.getField().equals("name")) {
          return auctionRepo.searchAuctionByCreatedDateAndName(
              request.getStartDateSearch(),
              request.getEndDateSearch(),
              request.getSearch(),
              paging);
        }
      } else {
        if (request.getFieldDate().equals("createdDate")) {
          return auctionRepo.searchAuctionByCreatedDate(
              request.getStartDateSearch(), request.getEndDateSearch(), paging);
        }
      }
    } catch (Exception ex) {
      log.error("Exception: Get list auction", ex);
    }
    return null;
  }

  public GetAuctionResponseData getAuctionResponseData(long auctionId) {
    Auction auction = getAuctionInfo(auctionId);
    return new GetAuctionResponseData(auction);
  }

  public Auction getAuctionInfo(Long auctionId) {
    return auctionRepo.findByAuctionId(auctionId).orElse(null);
  }

  public Auction saveAuction(Auction auction) {
    return auctionRepo.save(auction);
  }

  public void updateAuctionStatus(Long auctionId, int status) {
    Auction auction = getAuctionInfo(auctionId);
    if (auction == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found");
    }

    Auction editAuction = new Auction(status, auction);
    Auction result = saveAuction(editAuction);
    if (result == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't update auction status");
    }
  }

  public void updateAuctionStatus(Auction auction, int status) {
    Auction editAuction = new Auction(status, auction);
    Auction result = saveAuction(editAuction);
    if (result == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't update auction status");
    }
  }

  public boolean deleteAuction(Auction auction) {
    try {
      auctionRepo.delete(auction);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void registerAuction(Long auctionId, String tokenHaveBearer)
      throws RegisterAuctionException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(tokenHaveBearer);
    if (userToken == null || userToken.isInValid()) {
      throw new RegisterAuctionException(MessageUtils.message("token.isExpire"));
    }

    User user = userRepository.findById(userToken.getUserId()).orElse(null);
    if (user == null) {
      throw new RegisterAuctionException(MessageUtils.message("username.notexist"));
    }

    Auction auction = auctionRepo.findByAuctionId(auctionId).orElse(null);
    if (auction == null) {
      throw new RegisterAuctionException(MessageUtils.message("auction.notexist"));
    }

    AuctionRegisterId auctionRegisterId =
        AuctionRegisterId.auctionRegisterId(userToken.getUserId(), auctionId);
    if (auctionRegisterRepository.existsById(auctionRegisterId)) {
      throw new RegisterAuctionException(MessageUtils.message("auctionregister.userregisterd"));
    }

    try {
      auctionRegisterRepository.save(
          AuctionRegister.create(userToken.getUserId(), auctionId, StatusRegisterEnum.PROGRESS));

      Property property = auction.getProperty();
      if (property == null) {
        throw new RegisterAuctionException(MessageUtils.message("error.mess"));
      }

      String title = MessageUtils.message("auctionregister.notificatontitle");
      String fee = String.valueOf(auction.getRegisterFee());
      String bankNo = "12345678910";
      String bankName = "A Chau";
      String auctionIdTxt = String.valueOf(auctionId);
      String propertyName = property.getName();
      String message =
          MessageUtils.message(
              "auctionregister.notificatonmessage",
              fee,
              bankNo,
              bankName,
              auctionIdTxt,
              propertyName);
      Long now = System.currentTimeMillis();
      AuctionRegisterNotification auctionRegisterNotification =
          AuctionRegisterNotification.createNew(
              auctionId,
              userToken.getUserId(),
              genIdUtil.genUID(),
              title,
              message,
              now + DateTimeConst.THIRTY_DAY,
              now);
      auctionRegisterNotificationRepository.save(auctionRegisterNotification);
      if (StringUtils.isNotEmpty(user.getFirebaseToken())) {
        firebaseNotificationRepository.sendNotification(title, message, user.getFirebaseToken());
      }

    } catch (Exception e) {
      throw new RegisterAuctionException(MessageUtils.message("auctionregister.userregisterd"));
    }
  }

  public void updateStatusAuctionRegister(Long auctionId, Long userId, String status)
      throws UpdateRegisterAuctionException {
    try {
      AuctionRegisterId auctionRegisterId = new AuctionRegisterId();
      auctionRegisterId.setAuctionId(auctionId);
      auctionRegisterId.setUserId(userId);
      AuctionRegister auctionRegister =
          auctionRegisterRepository.findById(auctionRegisterId).orElse(null);
      if (auctionRegister == null) {
        throw new UpdateRegisterAuctionException(MessageUtils.message("auction.notexist"));
      }
      StatusRegisterEnum realStatus = StatusRegisterEnum.status(status);
      if (realStatus == null) {
        throw new UpdateRegisterAuctionException(
            MessageUtils.message("auctionregister.updatestatusnotexist"));
      }

      auctionRegister.setStatus(realStatus.getValue());
      auctionRegister.setUpdatedDate(System.currentTimeMillis());
      auctionRegisterRepository.save(auctionRegister);
    } catch (Exception e) {
      throw new UpdateRegisterAuctionException(
          MessageUtils.message("auctionregister.cannotupdatestatus"));
    }
  }

  public Page<UserRegisterAuctionAdminResponseData> getUserRegisterAuction(
      Long auctionId, int page, int size) {
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    return auctionRegisterRepository
        .usersRegisterAuction(auctionId, paging)
        .map(UserRegisterAuctionAdminResponseData::create);
  }

  public String getStatusRegisterOfOneUser(Long auctionId, String tokenHaveBearer)
      throws StatusRegisterAuctionException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(tokenHaveBearer);
    if (userToken == null || userToken.isInValid()) {
      throw new StatusRegisterAuctionException(MessageUtils.message("token.isExpire"));
    }
    AuctionRegisterId auctionRegisterId = new AuctionRegisterId();
    auctionRegisterId.setAuctionId(auctionId);
    auctionRegisterId.setUserId(userToken.getUserId());
    try {
      AuctionRegister auctionRegister =
          auctionRegisterRepository.findById(auctionRegisterId).orElse(null);
      if (auctionRegister == null) {
        throw new StatusRegisterAuctionException(
            MessageUtils.message("auctionregister.usernotregisteryet"));
      }
      return StatusRegisterEnum.status(auctionRegister.getStatus()).getDescription();
    } catch (Exception e) {
      throw new StatusRegisterAuctionException(MessageUtils.message("error.mess"));
    }
  }

  public void deleteAuctionRegisterOfUser(Long auctionId, Long userId) {
    AuctionRegisterId auctionRegisterId = new AuctionRegisterId();
    auctionRegisterId.setAuctionId(auctionId);
    auctionRegisterId.setUserId(userId);
    auctionRegisterRepository.deleteById(auctionRegisterId);
  }

  public AuctionHistorical saveAuctionHistorical(Long auctionId, long userId, float auctionPrice) {
    try {
      AuctionHistorical auctionHistorical =
          auctionHistoricalRepository.save(
              AuctionHistorical.create(genIdUtil.genUID(), userId, auctionId, auctionPrice));
      return auctionHistorical;
    } catch (Exception ex) {
      log.error("Exception: saveAuctionHistorical", ex);
    }

    return null;
  }

  public ListResponseData getAuctionHistoricalResponseData(
      Long auctionId, GetAuctionHistoricalRequestData request) {
    try {
      Page<AuctionHistorical> pageResult = searchAuctionHistorical(auctionId, request);
      List<AuctionHistoricalResponseData> getAuctionHistoricalResponseDataList =
          pageResult.getContent().stream()
              .map(
                  auctionHistorical -> {
                    if (auctionHistorical.getUser() == null) {
                      Optional<User> user = userRepository.findById(auctionHistorical.getUserId());
                      if (user.isPresent()) {
                        auctionHistorical.setUser(user.get());
                      } else {
                        log.error("User not found");
                      }
                    }
                    return new AuctionHistoricalResponseData(auctionHistorical);
                  })
              .collect(Collectors.toList());
      return new ListResponseData(
          getAuctionHistoricalResponseDataList,
          pageResult.getTotalElements(),
          request.getPage(),
          request.getSize());
    } catch (Exception ex) {
      log.error("Exception: getAuctionHistorical", ex);
    }
    return null;
  }

  public Page<AuctionHistorical> searchAuctionHistorical(
      Long auctionId, GetAuctionHistoricalRequestData request) {
    Pageable paging =
        PageRequest.of(
            request.getPage() - 1, request.getSize(), Sort.by("createdDate").descending());
    try {
      return auctionHistoricalRepository.getAuctionHistoricalByAuctionId(auctionId, paging);
    } catch (Exception ex) {
      log.error("Exception: getAuctionHistorical", ex);
    }
    return null;
  }

  public AuctionHistorical getLatestAuctionHistorical(Long auctionId) {
    Pageable paging = PageRequest.of(1, 1, Sort.by("createdDate").descending());
    try {
      Page<AuctionHistorical> page =
          auctionHistoricalRepository.getAuctionHistoricalByAuctionId(auctionId, paging);
      if (page.getSize() > 0) return page.getContent().get(0);
    } catch (Exception ex) {
      log.error("Exception: getAuctionHistorical", ex);
    }
    return null;
  }

  public void pushAuctionHistorical(Long auctionId, int status, AuctionStep auctionStep) {
    int page = 1, size = 10;
    GetAuctionHistoricalRequestData request = new GetAuctionHistoricalRequestData();
    request.setPage(page);
    request.setSize(size);
    ListResponseData listResponseData = getAuctionHistoricalResponseData(auctionId, request);

    Response response =
        new Response(
            GetAuctionHistoricalResponseData.builder()
                .auctionStatus(status)
                .step(auctionStep.getAuctionStepEnum().getValue())
                .remainTime(auctionStep.getRemainTime())
                .auctionHistoricalList(listResponseData)
                .build());

    try {
      String channel = String.valueOf(auctionId);
      String event = "user-bidding";
      String message = GsonUtils.toJson(response);
      log.info("push channel " + channel + ", event " + event + ", message: " + message);
      pusher.trigger(channel, event, message);
    } catch (Exception ex) {
      log.error("Exception: pushAuctionHistorical", ex);
    }
  }

  @Transactional
  public boolean deleteAllAuctionHistorical(Long auctionId) {
    try {
      auctionHistoricalRepository.deleteAllByAuctionIdEquals(auctionId);
      return true;
    } catch (Exception ex) {
      log.error("Exception: deleteAllAuctionHistorical", ex);
      return false;
    }
  }

  public void startAuction(Long auctionId) {
    auctionCache.setAuctionStatus(String.valueOf(auctionId), AuctionStatusEnum.PROCESS.name());
    AuctionTask task = new AuctionTask(auctionId, this);
    auctionTaskRegistrar.addTask(task, 1000, auctionId);
  }

  @Transactional
  public void cancelAuction(Long auctionId) {
    auctionCache.setAuctionStatus(String.valueOf(auctionId), AuctionStatusEnum.CANCELED.name());
    auctionCache.resetAllStep(String.valueOf(auctionId));
    boolean result = deleteAllAuctionHistorical(auctionId);
    if (!result) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete auction historical");
    }
  }

  public AuctionStep startStep30(Long auctionId) {
    log.info("Start step 30 Auction {}", auctionId);
    auctionCache.resetAllStep(String.valueOf(auctionId));

    auctionCache.setAuctionStep30Status(
        String.valueOf(auctionId), AuctionStepStatusEnum.WAIT.name());
    auctionCache.setAuctionStep30Timeout(
        String.valueOf(auctionId), AuctionStepTimeoutEnum.TRUE.name());

    AuctionStep auctionStep =
        AuctionStep.builder().auctionStepEnum(AuctionStepEnum.STEP_30).remainTime(45000L).build();
    pushAuctionHistorical(auctionId, AuctionStatusEnum.PROCESS.getValue(), auctionStep);
    return auctionStep;
  }

  public AuctionStep checkAuctionSteps(Long auctionId) {
    String auctionStatus = auctionCache.getAuctionStatus(String.valueOf(auctionId));
    if (auctionStatus.equals(AuctionStatusEnum.DONE.name())
        || auctionStatus.equals(AuctionStatusEnum.CANCELED.name())) {
      auctionTaskRegistrar.removeTask(auctionId);
      return AuctionStep.builder().auctionStepEnum(AuctionStepEnum.FINISH).remainTime(0L).build();
    } else {
      return checkStep30(auctionId);
    }
  }

  private AuctionStep checkStep30(Long auctionId) {
    String auctionStep30Status = auctionCache.getAuctionStep30Status(String.valueOf(auctionId));
    if (AuctionStepStatusEnum.WAIT.name().equals(auctionStep30Status)) {
      String auctionStep30Timeout = auctionCache.getAuctionStep30Timeout(String.valueOf(auctionId));
      if (AuctionStepTimeoutEnum.TRUE.name().equals(auctionStep30Timeout)) {
        long remainTime =
            auctionCache.getAuctionStep30RemainTime(String.valueOf(auctionId)) + 15000L;
        // log.info("Wait step 30 Auction {} RemainTime {}", auctionId, remainTime);
        return AuctionStep.builder()
            .auctionStepEnum(AuctionStepEnum.STEP_30)
            .remainTime(remainTime)
            .build();
      } else {
        return checkStep5x1(auctionId);
      }
    } else {
      // log.info("Auction {} no bidding yet", auctionId);
      return AuctionStep.builder()
          .auctionStepEnum(AuctionStepEnum.WAIT_BIDDING)
          .remainTime(0L)
          .build();
    }
  }

  private AuctionStep checkStep5x1(Long auctionId) {
    String auctionStep5x1Status = auctionCache.getAuctionStep5x1Status(String.valueOf(auctionId));
    if (AuctionStepStatusEnum.WAIT.name().equals(auctionStep5x1Status)) {
      String auctionStep5x1Timeout =
          auctionCache.getAuctionStep5x1Timeout(String.valueOf(auctionId));
      if (AuctionStepTimeoutEnum.TRUE.name().equals(auctionStep5x1Timeout)) {
        long remainTime =
            auctionCache.getAuctionStep5x1RemainTime(String.valueOf(auctionId)) + 10000L;
        // log.info("Wait step 5x1 Auction {} RemainTime {}", auctionId, remainTime);
        return AuctionStep.builder()
            .auctionStepEnum(AuctionStepEnum.STEP_5X1)
            .remainTime(remainTime)
            .build();
      } else {
        return checkStep5x2(auctionId);
      }
    } else {
      log.info("Start step 5x1 Auction {}", auctionId);
      auctionCache.setAuctionStep5x1Status(
          String.valueOf(auctionId), AuctionStepStatusEnum.WAIT.name());
      auctionCache.setAuctionStep5x1Timeout(
          String.valueOf(auctionId), AuctionStepTimeoutEnum.TRUE.name());

      AuctionStep auctionStep =
          AuctionStep.builder()
              .auctionStepEnum(AuctionStepEnum.STEP_5X1)
              .remainTime(15000L)
              .build();
      pushAuctionHistorical(auctionId, AuctionStatusEnum.PROCESS.getValue(), auctionStep);
      return auctionStep;
    }
  }

  private AuctionStep checkStep5x2(Long auctionId) {
    String auctionStep5x2Status = auctionCache.getAuctionStep5x2Status(String.valueOf(auctionId));
    if (AuctionStepStatusEnum.WAIT.name().equals(auctionStep5x2Status)) {
      String auctionStep5x2Timeout =
          auctionCache.getAuctionStep5x2Timeout(String.valueOf(auctionId));
      if (AuctionStepTimeoutEnum.TRUE.name().equals(auctionStep5x2Timeout)) {
        long remainTime =
            auctionCache.getAuctionStep5x2RemainTime(String.valueOf(auctionId)) + 5000L;
        ;
        // log.info("Wait step 5x2 Auction {} RemainTime {}", auctionId, remainTime);
        return AuctionStep.builder()
            .auctionStepEnum(AuctionStepEnum.STEP_5X2)
            .remainTime(remainTime)
            .build();
      } else {
        return checkStep5x3(auctionId);
      }
    } else {
      log.info("Start step 5x2 Auction {}", auctionId);
      auctionCache.setAuctionStep5x2Status(
          String.valueOf(auctionId), AuctionStepStatusEnum.WAIT.name());
      auctionCache.setAuctionStep5x2Timeout(
          String.valueOf(auctionId), AuctionStepTimeoutEnum.TRUE.name());

      AuctionStep auctionStep =
          AuctionStep.builder()
              .auctionStepEnum(AuctionStepEnum.STEP_5X2)
              .remainTime(10000L)
              .build();
      pushAuctionHistorical(auctionId, AuctionStatusEnum.PROCESS.getValue(), auctionStep);
      return auctionStep;
    }
  }

  private AuctionStep checkStep5x3(Long auctionId) {
    String auctionStep5x3Status = auctionCache.getAuctionStep5x3Status(String.valueOf(auctionId));
    if (AuctionStepStatusEnum.WAIT.name().equals(auctionStep5x3Status)) {
      String auctionStep5x3Timeout =
          auctionCache.getAuctionStep5x3Timeout(String.valueOf(auctionId));
      if (AuctionStepTimeoutEnum.TRUE.name().equals(auctionStep5x3Timeout)) {
        long remainTime = auctionCache.getAuctionStep5x3RemainTime(String.valueOf(auctionId));
        // log.info("Wait step 5x3 Auction {} RemainTime {}", auctionId, remainTime);
        return AuctionStep.builder()
            .auctionStepEnum(AuctionStepEnum.STEP_5X3)
            .remainTime(remainTime)
            .build();
      } else {
        log.info("Auction {} Finish", auctionId);
        auctionCache.setAuctionStatus(String.valueOf(auctionId), AuctionStatusEnum.DONE.name());
        auctionCache.resetAllStep(String.valueOf(auctionId));
        updateAuctionStatus(auctionId, AuctionStatusEnum.DONE.getValue());
        AuctionStep auctionStep =
            AuctionStep.builder().auctionStepEnum(AuctionStepEnum.FINISH).remainTime(0L).build();
        pushAuctionHistorical(auctionId, AuctionStatusEnum.DONE.getValue(), auctionStep);
        return AuctionStep.builder().auctionStepEnum(AuctionStepEnum.FINISH).remainTime(0L).build();
      }
    } else {
      log.info("Start step 5x3 Auction {}", auctionId);
      auctionCache.setAuctionStep5x3Status(
          String.valueOf(auctionId), AuctionStepStatusEnum.WAIT.name());
      auctionCache.setAuctionStep5x3Timeout(
          String.valueOf(auctionId), AuctionStepTimeoutEnum.TRUE.name());
      AuctionStep auctionStep =
          AuctionStep.builder().auctionStepEnum(AuctionStepEnum.STEP_5X3).remainTime(5000L).build();
      pushAuctionHistorical(auctionId, AuctionStatusEnum.PROCESS.getValue(), auctionStep);
      return auctionStep;
    }
  }
}
