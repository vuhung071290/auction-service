package dgtc.controller.admin;

import dgtc.common.enums.AuctionStatusEnum;
import dgtc.common.exception.UpdateRegisterAuctionException;
import dgtc.common.genid.GenIdUtil;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Auction;
import dgtc.entity.datasource.admin.Property;
import dgtc.entity.dto.handler.auction.*;
import dgtc.entity.dto.handler.user.UserRegisterAuctionAdminResponseData;
import dgtc.entity.dto.handler.user.UserRegisterAuctionRequestData;
import dgtc.service.admin.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/auction-management")
@ResponseBody
@RequiredArgsConstructor
public class AuctionManagementController {

  @Autowired private final GenIdUtil genIdUtil;
  private final AuctionService auctionService;

  @GetMapping
  @PreAuthorize("hasAuthority('AUCTION_MNG_VIEW')")
  public Response getListAuction(
      @Valid GetAuctionRequestData request, HttpServletRequest httpServletRequest) {

    request.init();

    if (StringUtils.isNotEmpty(request.getField())) {
      if (!Property.VALID_SEARCH_FIELD.contains(request.getField())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Field must in list " + GsonUtils.toJson(Property.VALID_SEARCH_FIELD));
      }
    }

    if (!Property.VALID_SEARCH_DATE_FIELD.contains(request.getFieldDate())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "FieldDate must in list " + GsonUtils.toJson(Property.VALID_SEARCH_DATE_FIELD));
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
  @PreAuthorize("hasAuthority('AUCTION_MNG_VIEW')")
  public Response getPropertyInfo(@PathVariable Long auctionId, HttpServletRequest request) {

    Auction auctionInfo = auctionService.getAuctionInfo(auctionId);
    if (auctionInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found.");
    }
    return new Response(auctionInfo);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('AUCTION_MNG_ADD')")
  public Response createAuction(@Valid @RequestBody CreateAuctionRequestData request) {
    Auction newAuction = new Auction(request, genIdUtil.genUID());
    Auction result = auctionService.saveAuction(newAuction);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save auction");
    }

    return new Response("Auction " + request.getName() + " has been successfully created");
  }

  @PutMapping("/{auctionId}")
  @PreAuthorize("hasAuthority('AUCTION_MNG_EDIT')")
  public Response editAuction(
      @PathVariable Long auctionId, @Valid @RequestBody EditAuctionRequestData request) {
    Auction auctionInfo = auctionService.getAuctionInfo(auctionId);
    if (auctionInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found");
    }
    Auction editAuction = new Auction(request, auctionInfo);
    Auction result = auctionService.saveAuction(editAuction);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save auction");
    }

    return new Response("Auction " + request.getName() + " has been successfully updated");
  }

  @PutMapping("/{auctionId}/status")
  @PreAuthorize("hasAuthority('AUCTION_MNG_EDIT')")
  public Response updateStatusAuction(
      @PathVariable Long auctionId, @Valid @RequestBody UpdateStatusAuctionRequestData request) {
    Auction auctionInfo = auctionService.getAuctionInfo(auctionId);
    if (auctionInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found");
    }

    if (request.getStatus() == AuctionStatusEnum.PROCESS.getValue()) {
      if (auctionInfo.getStatus() == AuctionStatusEnum.PROCESS.getValue()
          || auctionInfo.getStatus() == AuctionStatusEnum.DONE.getValue()) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Can not start auction, current status "
                + AuctionStatusEnum.status(auctionInfo.getStatus()).name());
      }
      auctionService.startAuction(auctionId);
    }

    if (request.getStatus() == AuctionStatusEnum.CANCELED.getValue()) {
      if (auctionInfo.getStatus() == AuctionStatusEnum.CANCELED.getValue()
          || auctionInfo.getStatus() == AuctionStatusEnum.DONE.getValue()) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Can not cancel auction, current status "
                + AuctionStatusEnum.status(auctionInfo.getStatus()).name());
      }
      auctionService.cancelAuction(auctionId);
    }

    auctionService.updateAuctionStatus(auctionInfo, request.getStatus());

    return new Response("Auction " + auctionInfo.getName() + " status was changed");
  }

  @DeleteMapping("/{auctionId}")
  @PreAuthorize("hasAuthority('AUCTION_MNG_DELETE')")
  public Response deleteAuction(@PathVariable Long auctionId) {
    Auction auctionInfo = auctionService.getAuctionInfo(auctionId);
    if (auctionInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction was not found");
    }

    if (auctionInfo.getStatus() == AuctionStatusEnum.PROCESS.getValue()) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Can not delete auction with status "
              + AuctionStatusEnum.status(auctionInfo.getStatus()).name());
    }

    boolean result = auctionService.deleteAuction(auctionInfo);
    if (!result) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete Auction");
    }

    return new Response("Auction has been successfully deleted");
  }

  @PutMapping("/{auctionId}/status-register/{userId}")
  @PreAuthorize("hasAuthority('AUCTION_MNG_EDIT')")
  public Response updateStatusAuctionRegister(
      @PathVariable Long auctionId,
      @PathVariable Long userId,
      @Valid @RequestBody UpdateStatusAucRegisterRequestData req) {
    try {
      auctionService.updateStatusAuctionRegister(auctionId, userId, req.getStatus());
      return new Response(HttpStatus.OK.value(), null);
    } catch (UpdateRegisterAuctionException e) {
      return new Response(HttpStatus.NOT_FOUND.value(), null);
    }
  }

  @GetMapping("/{auctionId}/user-register")
  @PreAuthorize("hasAuthority('AUCTION_MNG_VIEW')")
  public Response getUserRegisterAuction(
      @PathVariable Long auctionId, @Valid UserRegisterAuctionRequestData req) {
    try {
      Page<UserRegisterAuctionAdminResponseData> page =
          auctionService.getUserRegisterAuction(auctionId, req.getPage(), req.getSize());
      ListResponseData listRes =
          new ListResponseData(
              page.getContent(), page.getTotalElements(), req.getPage(), req.getSize());
      return new Response(listRes);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null);
    }
  }

  @GetMapping("/{auctionId}/historical")
  @PreAuthorize("hasAuthority('AUCTION_MNG_VIEW')")
  public Response getAuctionHistorical(
      @PathVariable Long auctionId, @Valid GetAuctionHistoricalRequestData request) {

    ListResponseData listResponse =
        auctionService.getAuctionHistoricalResponseData(auctionId, request);
    if (listResponse == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list auction historical");
    }

    Response response = new Response(listResponse);
    return response;
  }
}
