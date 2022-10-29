package dgtc.entity.dto.handler.auction;

import dgtc.entity.datasource.admin.AuctionHistorical;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AuctionHistoricalResponseData implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long auctionHistoricalId;

  private Long userId;

  private String userName;

  private float bidPrice;

  private long createdDate;

  public AuctionHistoricalResponseData(AuctionHistorical auctionHistorical) {
    this.auctionHistoricalId = auctionHistorical.getAuctionHistoricalId();
    this.userId = auctionHistorical.getUserId();
    this.userName = auctionHistorical.getUser().getDisplayName();
    this.bidPrice = auctionHistorical.getBidPrice();
    this.createdDate = auctionHistorical.getCreatedDate();
  }
}
