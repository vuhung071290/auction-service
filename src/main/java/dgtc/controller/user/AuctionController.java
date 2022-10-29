package dgtc.controller.user;

import com.google.common.collect.ImmutableSet;
import dgtc.common.enums.AuctionStatusEnum;
import dgtc.common.exception.RegisterAuctionException;
import dgtc.common.exception.StatusRegisterAuctionException;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Auction;
import dgtc.entity.datasource.admin.AuctionHistorical;
import dgtc.entity.datasource.user.User;
import dgtc.entity.dto.handler.auction.*;
import dgtc.entity.dto.handler.user.UserBiddingRequestData;
import dgtc.repository.user.UserRepository;
import dgtc.service.admin.AuctionService;
import dgtc.token.TokenManager;
import dgtc.token.UserToken;
import dgtc.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Set;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/auction")
@ResponseBody
@RequiredArgsConstructor
public class AuctionController {

  public static final Set<String> VALID_SEARCH_FIELD =
      ImmutableSet.<String>builder().add("name").add("status").build();

  public static final Set<String> VALID_SEARCH_DATE_FIELD =
      ImmutableSet.<String>builder().add("createdDate").build();

  private final AuctionService auctionService;
  private final TokenManager tokenManager;
  private final UserRepository userRepository;
  private final RedissonClient redissonClient;

  @GetMapping
  public Response getListAuction(
      @Valid GetAuctionRequestData request, HttpServletRequest httpServletRequest) {

    request.init();

    if (StringUtils.isNotEmpty(request.getField())) {
      if (!VALID_SEARCH_FIELD.contains(request.getField())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Field must in list " + GsonUtils.toJson(VALID_SEARCH_FIELD));
      }
    }

    if (!VALID_SEARCH_DATE_FIELD.contains(request.getFieldDate())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "FieldDate must in list " + GsonUtils.toJson(VALID_SEARCH_DATE_FIELD));
    }

    ListResponseData listResponse = auctionService.getListAuctionResponseData(request);

    if (listResponse == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list auction");
    }

    Response response = new Response(listResponse);
    return response;
  }

  @GetMapping("/{auctionId}")
  public Response getAuctionInfo(@PathVariable Long auctionId, HttpServletRequest request) {

    GetAuctionResponseData getAuctionResponseData =
        auctionService.getAuctionResponseData(auctionId);
    if (getAuctionResponseData == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found.");
    }
    return new Response(getAuctionResponseData);
  }

  @PostMapping("/{auctionId}/register")
  public Response registerAuction(
      @PathVariable Long auctionId, @RequestHeader(name = "Authorization") String tokenHaveBearer) {
    try {
      auctionService.registerAuction(auctionId, tokenHaveBearer);
      return new Response(HttpStatus.OK.value(), null);
    } catch (RegisterAuctionException e) {
      return new Response(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
  }

  @GetMapping("/{auctionId}/status-user-register")
  public Response statusRegisterOfOneUser(
      @PathVariable Long auctionId, @RequestHeader(name = "Authorization") String tokenHaveBearer) {
    try {
      String status = auctionService.getStatusRegisterOfOneUser(auctionId, tokenHaveBearer);
      StatusUserRegisterAuctionResponse statusRes = new StatusUserRegisterAuctionResponse();
      statusRes.setStatus(status);
      return new Response(statusRes);
    } catch (StatusRegisterAuctionException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{auctionId}/bidding")
  public Response bidding(
      @PathVariable Long auctionId,
      @RequestHeader(name = "Authorization") String tokenHaveBearer,
      @Valid @RequestBody UserBiddingRequestData req) {

    // validate user
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(tokenHaveBearer);
    if (userToken == null || userToken.isInValid()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, MessageUtils.message("token.isExpire"));
    }
    User user = userRepository.findById(userToken.getUserId()).orElse(null);
    if (user == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, MessageUtils.message("username.notexist"));
    }

    // validate Auction
    Auction auction = auctionService.getAuctionInfo(auctionId);
    if (auction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, MessageUtils.message("auction.notexist"));
    }
    if (auction.getStatus() != AuctionStatusEnum.PROCESS.getValue()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Auction status not valid for bidding, status " + auction.getStatus());
    }

    String keyLock = "bidding_lock_" + auctionId;
    RLock lock = redissonClient.getLock(keyLock);
    try {
      lock.lock();

      // validate bid price
      AuctionHistorical auctionHistorical = auctionService.getLatestAuctionHistorical(auctionId);
      if (auctionHistorical == null) {
        if (req.getBidPrice() < auction.getStartPrice()
            || req.getBidPrice() > auction.getStartPrice() + 5 * auction.getStepPrice()) {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, MessageUtils.message("auctionbidding.priceinvalid"));
        }
      } else {
        if (req.getBidPrice() - auctionHistorical.getBidPrice() < auction.getStepPrice()) {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, MessageUtils.message("auctionbidding.priceinvalid"));
        }
      }

      // save historical
      auctionService.saveAuctionHistorical(auctionId, user.getUserId(), req.getBidPrice());

      // start bidding timer
      auctionService.startStep30(auctionId);

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    } finally {
      lock.unlock();
    }

    return new Response(HttpStatus.OK.value(), null);
  }

  @GetMapping("/{auctionId}/historical")
  public Response getAuctionHistorical(
      @PathVariable Long auctionId, @Valid GetAuctionHistoricalRequestData request) {
    // validate Auction
    Auction auction = auctionService.getAuctionInfo(auctionId);
    if (auction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, MessageUtils.message("auction.notexist"));
    }

    ListResponseData listResponseData =
        auctionService.getAuctionHistoricalResponseData(auctionId, request);
    if (listResponseData == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list auction historical");
    }

    AuctionStep auctionStep = auctionService.checkAuctionSteps(auctionId);
    Response response =
        new Response(
            GetAuctionHistoricalResponseData.builder()
                .auctionStatus(auction.getStatus())
                .step(auctionStep.getAuctionStepEnum().getValue())
                .remainTime(auctionStep.getRemainTime())
                .auctionHistoricalList(listResponseData)
                .build());
    return response;
  }
}
