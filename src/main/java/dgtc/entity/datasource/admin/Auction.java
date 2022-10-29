package dgtc.entity.datasource.admin;

import com.google.common.collect.ImmutableSet;
import dgtc.common.enums.AuctionStatusEnum;
import dgtc.entity.dto.handler.auction.CreateAuctionRequestData;
import dgtc.entity.dto.handler.auction.EditAuctionRequestData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/** @author hunglv */
@Getter
@Setter
@Entity
@Data
@Table(name = "auction")
@NoArgsConstructor
public class Auction implements Serializable {

  public static final Set<String> VALID_SEARCH_FIELD =
      ImmutableSet.<String>builder().add("name").build();
  public static final Set<String> VALID_SEARCH_DATE_FIELD =
      ImmutableSet.<String>builder().add("createdDate").build();
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "auction_id")
  private long auctionId;
  @Column(name = "name")
  private String name;
  @Column(name = "description")
  private String description;
  @Column(name = "auction_method")
  private int auctionMethod;
  @Column(name = "register_fee")
  private float registerFee;
  @Column(name = "deposit_fee")
  private float depositFee;
  @Column(name = "start_price")
  private float startPrice;
  @Column(name = "step_price")
  private float stepPrice;
  @Column(name = "status")
  private int status;
  @Column(name = "user_id_won")
  private long userIdWon;
  @Column(name = "start_register_date")
  private long startRegisterDate;
  @Column(name = "end_register_date")
  private long endRegisterDate;
  @Column(name = "start_auction_date")
  private long startAuctionDate;
  @Column(name = "end_auction_date")
  private long endAuctionDate;
  @Column(name = "property_id")
  private long propertyId;
  @Column(name = "created_user")
  private String createdUser;
  @Column(name = "extra_info")
  private String extraInfo;
  @Column(name = "updated_date")
  private long updatedDate;
  @Column(name = "created_date")
  private long createdDate;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id", insertable = false, updatable = false)
  private Property property;

  public Auction(CreateAuctionRequestData request, long auctionId) {
    this.auctionId = auctionId;
    this.name = request.getName();
    this.description = request.getDescription();
    this.auctionMethod = request.getAuctionMethod();
    this.registerFee = request.getRegisterFee();
    this.depositFee = request.getDepositFee();
    this.startPrice = request.getStartPrice();
    this.stepPrice = request.getStepPrice();
    this.status = AuctionStatusEnum.PLAN.getValue();
    this.startRegisterDate = request.getStartRegisterDate();
    this.endRegisterDate = request.getEndRegisterDate();
    this.propertyId = request.getPropertyId();
    this.createdUser = request.getCreatedUser();
    this.extraInfo = request.getExtraInfo();
    this.updatedDate = 0L;
    this.createdDate = System.currentTimeMillis();
  }

  public Auction(EditAuctionRequestData request, Auction savedAuction) {
    this.auctionId = savedAuction.getAuctionId();
    this.name = request.getName();
    this.description = request.getDescription();
    this.auctionMethod = request.getAuctionMethod();
    this.registerFee = request.getRegisterFee();
    this.depositFee = request.getDepositFee();
    this.startPrice = request.getStartPrice();
    this.stepPrice = request.getStepPrice();
    this.status = savedAuction.getStatus();
    this.startRegisterDate = request.getStartRegisterDate();
    this.endRegisterDate = request.getEndRegisterDate();
    this.startAuctionDate = savedAuction.getStartAuctionDate();
    this.endAuctionDate = savedAuction.getEndAuctionDate();
    this.propertyId = request.getPropertyId();
    this.createdUser = savedAuction.getCreatedUser();
    this.updatedDate = System.currentTimeMillis();
    this.createdDate = savedAuction.getCreatedDate();
  }

  public Auction(int status, Auction savedAuction) {
    this.auctionId = savedAuction.getAuctionId();
    this.name = savedAuction.getName();
    this.description = savedAuction.getDescription();
    this.auctionMethod = savedAuction.getAuctionMethod();
    this.registerFee = savedAuction.getRegisterFee();
    this.depositFee = savedAuction.getDepositFee();
    this.startPrice = savedAuction.getStartPrice();
    this.stepPrice = savedAuction.getStepPrice();
    this.status = status;
    this.startRegisterDate = savedAuction.getStartRegisterDate();
    this.endRegisterDate = savedAuction.getEndRegisterDate();
    this.propertyId = savedAuction.getPropertyId();
    this.createdUser = savedAuction.getCreatedUser();
    this.updatedDate = System.currentTimeMillis();
    this.createdDate = savedAuction.getCreatedDate();
    if (status == AuctionStatusEnum.PROCESS.getValue()) {
      this.startAuctionDate = System.currentTimeMillis();
    }
    if (status == AuctionStatusEnum.DONE.getValue()
        || status == AuctionStatusEnum.CANCELED.getValue()) {
      this.startAuctionDate = savedAuction.getStartAuctionDate();
      this.endAuctionDate = System.currentTimeMillis();
    }
  }
}
