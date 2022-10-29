package dgtc.entity.dto.handler.auction;

import dgtc.entity.base.ListResponseData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class GetAuctionHistoricalResponseData implements Serializable {
  private int auctionStatus;
  private int step;
  private long remainTime;
  private ListResponseData auctionHistoricalList;
}
