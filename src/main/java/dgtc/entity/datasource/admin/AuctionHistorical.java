package dgtc.entity.datasource.admin;

import dgtc.entity.datasource.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "auction_historical")
@NoArgsConstructor
public class AuctionHistorical implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "auction_historical_id")
  private Long auctionHistoricalId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "auction_id")
  private Long auctionId;

  @Column(name = "bid_price")
  private float bidPrice;

  @Column(name = "created_date")
  private long createdDate;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "auction_id", insertable = false, updatable = false)
  private Auction auction;

  public static AuctionHistorical create(
      long auctionHistoricalId, long userId, long auctionId, float bidPrice) {
    AuctionHistorical newObj = new AuctionHistorical();
    newObj.setAuctionHistoricalId(auctionHistoricalId);
    newObj.setAuctionId(auctionId);
    newObj.setUserId(userId);
    newObj.setBidPrice(bidPrice);
    newObj.setCreatedDate(System.currentTimeMillis());
    return newObj;
  }
}
