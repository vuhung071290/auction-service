package dgtc.entity.dto.handler.user;

import dgtc.common.enums.StatusRegisterEnum;
import dgtc.entity.datasource.admin.Auction;
import dgtc.entity.datasource.user.IAuctionsRegisterByOneUserAggregate;
import dgtc.entity.datasource.user.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserProfileResponse {
  private Long userId;
  private String phoneNumber;
  private String address;
  private String identity;
  private String displayName;
  private String avatar;
  private String bankAccount;
  private String bankName;
  private List<AuctionResponse> auctionsRegistered;

  public static UserProfileResponse create(
      List<IAuctionsRegisterByOneUserAggregate> aggregate, User user) {
    UserProfileResponse userProfileResponse = new UserProfileResponse();
    List<AuctionResponse> auctionsRegistered =
        aggregate.stream()
            .map(
                a -> {
                  Auction auc = a.getAuction();
                  AuctionResponse auctionResponse = new AuctionResponse();
                  auctionResponse.setAuctionId(auc.getAuctionId());
                  auctionResponse.setStatusAuction(auc.getStatus());
                  auctionResponse.setStatusRegister(
                      StatusRegisterEnum.status(a.getStatus()).getDescription());
                  return auctionResponse;
                })
            .collect(Collectors.toList());

    userProfileResponse.setAuctionsRegistered(auctionsRegistered);
    userProfileResponse.setUserId(user.getUserId());
    userProfileResponse.setPhoneNumber(user.getPhoneNumber());
    userProfileResponse.setAddress(user.getAddress());
    userProfileResponse.setIdentity(user.getIdentity());
    userProfileResponse.setDisplayName(user.getDisplayName());
    userProfileResponse.setAvatar(user.getAvatar());
    userProfileResponse.setBankAccount(user.getBankAccount());
    userProfileResponse.setBankName(user.getBankName());
    return userProfileResponse;
  }

  @Data
  static class AuctionResponse {
    private Long auctionId;
    private int statusAuction;
    private String statusRegister;
    private int startAuctionDate;
    private int endAuctionDate;
  }
}
