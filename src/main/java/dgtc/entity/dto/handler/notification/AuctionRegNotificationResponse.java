package dgtc.entity.dto.handler.notification;

import dgtc.entity.datasource.admin.Auction;
import lombok.Data;

@Data
public class AuctionRegNotificationResponse {
  private Long auctionId;

  private Long propertyId;
  private String nameProperty;

  private String statusRegister;

  private float registerFee;
  private float depositFee;
  private String auctionName;
  private long startRegisterDate;
  private long endRegisterDate;
  private long startAuctionDate;
  private long endAuctionDate;
  private int auctionMethod;

  public static AuctionRegNotificationResponse create(
      Long propertyId, String statusRegister, String nameProperty, Auction auction) {
    AuctionRegNotificationResponse res = new AuctionRegNotificationResponse();
    res.setStatusRegister(statusRegister);
    res.setNameProperty(nameProperty);
    res.setAuctionId(auction.getAuctionId());
    res.setPropertyId(propertyId);
    res.setRegisterFee(auction.getRegisterFee());
    res.setDepositFee(auction.getDepositFee());
    res.setAuctionName(auction.getName());

    res.setStartRegisterDate(auction.getStartRegisterDate());
    res.setEndRegisterDate(auction.getEndRegisterDate());

    res.setStartAuctionDate(auction.getStartAuctionDate());
    res.setEndAuctionDate(auction.getEndAuctionDate());

    res.setAuctionMethod(auction.getAuctionMethod());

    return res;
  }
}
