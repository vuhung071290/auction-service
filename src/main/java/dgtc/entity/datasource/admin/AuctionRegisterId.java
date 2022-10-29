package dgtc.entity.datasource.admin;

import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class AuctionRegisterId implements Serializable {
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "auction_id")
  private Long auctionId;

  public static AuctionRegisterId auctionRegisterId(Long userId, Long auctionId) {
    AuctionRegisterId id = new AuctionRegisterId();
    id.setAuctionId(auctionId);
    id.setUserId(userId);
    return id;
  }
}
