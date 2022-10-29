package dgtc.entity.dto.handler.auction;

import dgtc.entity.datasource.admin.Auction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
public class GetAuctionResponseData implements Serializable {

  private static final long serialVersionUID = 1L;

  private long auctionId;

  private String name;

  private String description;

  private int auctionMethod;

  private float registerFee;

  private float depositFee;

  private float startPrice;

  private float stepPrice;

  private int status;

  //    private long userIdWon;

  private long startRegisterDate;

  private long endRegisterDate;

  private long startAuctionDate;

  private long endAuctionDate;

  //    private String createdUser;
  //
  //    private String extraInfo;
  //
  //    private long updatedDate;

  private long createdDate;

  private long propertyId;

  private String propertyName;

  private String propertyDescription;

  private String images;

  private String registrationForms;

  private String auctionMinutes;

  private String contracts;

  public GetAuctionResponseData(Auction auction) {
    this.auctionId = auction.getAuctionId();
    this.name = auction.getName();
    this.description = auction.getDescription();
    this.auctionMethod = auction.getAuctionMethod();
    this.registerFee = auction.getRegisterFee();
    this.depositFee = auction.getDepositFee();
    this.startPrice = auction.getStartPrice();
    this.stepPrice = auction.getStepPrice();
    this.status = auction.getStatus();
    this.startRegisterDate = auction.getStartRegisterDate();
    this.endRegisterDate = auction.getEndRegisterDate();
    this.startAuctionDate = auction.getStartAuctionDate();
    this.endAuctionDate = auction.getEndAuctionDate();
    this.propertyId = auction.getProperty().getPropertyId();
    this.propertyName = auction.getProperty().getName();
    this.propertyDescription = auction.getProperty().getDescription();
    this.images = auction.getProperty().getImages();
    this.registrationForms = auction.getProperty().getRegistrationForms();
    this.auctionMinutes = auction.getProperty().getAuctionMinutes();
    this.contracts = auction.getProperty().getContracts();
    this.createdDate = auction.getCreatedDate();
  }
}
