package dgtc.entity.datasource.admin;

import dgtc.common.enums.StatusRegisterEnum;
import dgtc.entity.datasource.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "auction_register")
@IdClass(AuctionRegisterId.class)
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRegister implements Serializable {

  @Id
  @Column(name = "user_id")
  private Long userId;

  @Id
  @Column(name = "auction_id")
  private Long auctionId;

  @Column(name = "status")
  private int status;

  @Column(name = "updated_date")
  private long updatedDate;

  @Column(name = "created_date")
  private long createdDate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User userRegisterAuction;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", insertable = false, updatable = false)
  private Auction auctionRegister;

  public static AuctionRegister create(long userId, long auctionId, StatusRegisterEnum status) {
    AuctionRegister newObj = new AuctionRegister();
    newObj.setAuctionId(auctionId);
    newObj.setUserId(userId);
    newObj.setStatus(status.getValue());
    newObj.setCreatedDate(System.currentTimeMillis());
    return newObj;
  }
}
